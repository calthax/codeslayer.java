/*
 * Copyright (C) 2010 - Jeff Johnston
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.codeslayer.indexer;

import com.sun.source.tree.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.element.Modifier;
import com.sun.source.util.TreeScanner;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.*;
import javax.tools.FileObject;
import org.codeslayer.indexer.domain.IndexClass;
import org.codeslayer.indexer.domain.IndexMethod;

public class SourceIndexer implements Indexer {
    
    private final File[] files;
    private final IndexFactory indexFactory;
    private final List<String> suppressions;

    public SourceIndexer(File[] files, IndexFactory indexFactory, List<String> suppressions) {
     
        this.files = files;
        this.indexFactory = indexFactory;
        this.suppressions = suppressions;
    }

    public List<Index> createIndexes() 
            throws Exception {

        List<IndexClass> indexClasses = new ArrayList<IndexClass>();
        
        try {
            JavacTask javacTask = getJavacTask(files);
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                compilationUnitTree.accept(new ClassScanner(compilationUnitTree, sourcePositions, indexClasses), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return indexFactory.createIndexes(indexClasses);
    }

    private JavacTask getJavacTask(File[] files)
            throws Exception {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(files);
        return (JavacTask) compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
    }

    private class ClassScanner extends TreeScanner<Void, Void> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final LineMap lineMap;
        private final List<IndexClass> indexClasses;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<IndexClass> indexClasses) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.lineMap = compilationUnitTree.getLineMap();
            this.indexClasses = indexClasses;
        }

        @Override
        public Void visitClass(ClassTree classTree, Void arg1) {
            
            List<? extends Tree> members = classTree.getMembers();
            
            String packageName = getPackageName();

            if (!IndexerUtils.includePackage(suppressions, packageName)) {
                return super.visitClass(classTree, arg1);
            }
            
            IndexClass indexClass = new IndexClass();
            String className = getClassName();
            indexClass.setImports(getImports());
            indexClass.setClassName(className);
            indexClass.setPackageName(packageName + "." + className);
            indexClass.setFilePath(getFilePath());
            indexClass.setSuperClass(getSuperClass(classTree));
            
            for (Tree memberTree : members) {
                if (memberTree instanceof MethodTree) {
                    MethodTree methodTree = (MethodTree)memberTree;

                    IndexMethod indexMethod = new IndexMethod();
                    indexMethod.setName(methodTree.getName().toString());
                    indexMethod.setModifier(getModifier(methodTree));
                    indexMethod.setParameters(getParameters(methodTree));
                    indexMethod.setCompletion(getCompletion(methodTree));
                    indexMethod.setReturnType(getReturnType(methodTree));
                    indexMethod.setLineNumber(getLineNumber(methodTree));

                    indexClass.addMethod(indexMethod);
                }
            }
            
            indexClasses.add(indexClass);

            return super.visitClass(classTree, arg1);
        }

        private List<String> getImports() {
            
            List<String> results = new ArrayList<String>();

            for (ImportTree importTree : compilationUnitTree.getImports()) {
                results.add(importTree.toString());
            }
                
            return results;
        }

        private String getPackageName() {

            ExpressionTree expressionTree = compilationUnitTree.getPackageName();
            return expressionTree.toString();
        }

        private String getClassName() {

            FileObject sourceFile = compilationUnitTree.getSourceFile();
            String className = sourceFile.getName().toString();
            return className.substring(0, className.length()-5);
        }
        
        private String getSuperClass(ClassTree classTree) {
            
            Tree tree = classTree.getExtendsClause();
            if (tree == null) {
                return null;
            }
            
            return tree.toString();
        }

        private String getModifier(MethodTree methodTree) {

            ModifiersTree modifiersTree = methodTree.getModifiers();
            Set<Modifier> flags = modifiersTree.getFlags();
            for (Modifier modifier : flags) {
                return modifier.toString();
            }

            return "package";
        }

        private String getParameters(MethodTree methodTree) {

            StringBuilder sb = new StringBuilder();
            
            sb.append("(");

            Iterator<? extends VariableTree> iterator = methodTree.getParameters().iterator();
            while (iterator.hasNext()) {
                VariableTree variableTree = iterator.next();
                sb.append(variableTree.getType().toString()).append(" ");
                sb.append(variableTree.getName().toString());
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append(")");

            return sb.toString();
        }

        private String getCompletion(MethodTree methodTree) {

            StringBuilder sb = new StringBuilder();
            
            sb.append("(");

            Iterator<? extends VariableTree> iterator = methodTree.getParameters().iterator();
            while (iterator.hasNext()) {
                VariableTree variableTree = iterator.next();
                sb.append(variableTree.getName().toString());
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append(")");

            return sb.toString();
        }

        private String getReturnType(MethodTree methodTree) {

            Tree returnType = methodTree.getReturnType();
            if (returnType == null) {
                return "void";
            }
            
            return returnType.toString();
        }

        private String getFilePath() {
            
            FileObject sourceFile = compilationUnitTree.getSourceFile();
            return sourceFile.toUri().getPath();
        }

        private String getLineNumber(MethodTree methodTree) {

            long startPosition = sourcePositions.getStartPosition(compilationUnitTree, methodTree);
            return String.valueOf(lineMap.getLineNumber(startPosition));
        }
    }
}
