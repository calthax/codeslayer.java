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
package org.jindex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.element.Modifier;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.List;
import javax.tools.FileObject;

public class Indexer {

    public List<Method> indexSourceFiles(File[] sourceFiles)
            throws Exception {

        List<Method> methods = new ArrayList<Method>();
        
        try {
            JavacTask javacTask = getJavacTask(sourceFiles);
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                compilationUnitTree.accept(new MethodScanner(compilationUnitTree, sourcePositions, methods), null);
            }
        } catch (Exception e) {
            System.err.println("Not able to generate the index.");
        }

        return methods;
    }

    private JavacTask getJavacTask(File[] sourceFiles)
            throws Exception {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(sourceFiles);
        return (JavacTask) compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
    }

    private static class MethodScanner extends TreeScanner<Void, Void> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final LineMap lineMap;
        private final List<Method> methods;

        private MethodScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Method> methods) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.lineMap = compilationUnitTree.getLineMap();
            this.methods = methods;
        }

        @Override
        public Void visitMethod(MethodTree methodTree, Void arg1) {

            Method method = new Method();
            method.setName(methodTree.getName().toString());
            method.setParameters(getParameters(methodTree));
            method.setLineNumber(getLineNumber(methodTree));
            method.setModifier(getModifier(methodTree));
            method.setFilePath(getFilePath());
            method.setClassName(getClassName());
            method.setPackageName(getPackageName());
            methods.add(method);
            return super.visitMethod(methodTree, arg1);
        }

        private String getParameters(MethodTree methodTree) {

            StringBuilder result = new StringBuilder();

            Iterator<? extends VariableTree> iterator = methodTree.getParameters().iterator();
            while (iterator.hasNext()) {
                VariableTree variableTree = iterator.next();
                result.append(variableTree.getType().toString()).append(" ");
                result.append(variableTree.getName().toString());
                if (iterator.hasNext()) {
                    result.append(", ");
                }
            }

            return result.toString();
        }

        private String getLineNumber(MethodTree methodTree) {

            long startPosition = sourcePositions.getStartPosition(compilationUnitTree, methodTree);
            return String.valueOf(lineMap.getLineNumber(startPosition));
        }

        private String getFilePath() {
            
            FileObject sourceFile = compilationUnitTree.getSourceFile();
            return sourceFile.toUri().getPath();
        }

        private String getClassName() {

            FileObject sourceFile = compilationUnitTree.getSourceFile();
            String className = sourceFile.getName().toString();
            return className.substring(0, className.length()-5);
        }

        private String getPackageName() {

            ExpressionTree expressionTree = compilationUnitTree.getPackageName();
            return expressionTree.toString();
        }

        private String getModifier(MethodTree methodTree) {

            ModifiersTree modifiersTree = methodTree.getModifiers();
            Set<Modifier> flags = modifiersTree.getFlags();
            for (Modifier modifier : flags) {
                return modifier.toString();
            }

            return "";
        }
    }
}
