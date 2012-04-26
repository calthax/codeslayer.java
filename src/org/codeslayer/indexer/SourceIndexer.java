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
import java.util.Set;
import javax.lang.model.element.Modifier;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.*;
import javax.tools.FileObject;
import org.codeslayer.source.*;
import org.codeslayer.source.ScopeTreeFactory;

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

        List<Klass> indexClasses = new ArrayList<Klass>();
        
        try {
            JavacTask javacTask = SourceUtils.getJavacTask(files);
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                ScopeTreeFactory scopeTreeFactory = new ScopeTreeFactory(compilationUnitTree);
                ScopeTree scopeTree = scopeTreeFactory.createScopeTree();
                compilationUnitTree.accept(new ClassScanner(compilationUnitTree, sourcePositions, indexClasses), scopeTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return indexFactory.createIndexes(indexClasses);
    }

    private class ClassScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final List<Klass> indexClasses;
        private final String className;
        private final String simpleClassName;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Klass> indexClasses) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.indexClasses = indexClasses;
            className = SourceUtils.getClassName(compilationUnitTree);
            simpleClassName = SourceUtils.getSimpleClassName(compilationUnitTree);
        }

        @Override
        public ScopeTree visitImport(ImportTree importTree, ScopeTree scopeTree) {

            String importName = importTree.getQualifiedIdentifier().toString();
            scopeTree.addImportName(importName);
            return super.visitImport(importTree, scopeTree);
        }
        
        @Override
        public ScopeTree visitClass(ClassTree classTree, ScopeTree scopeTree) {
            
            List<? extends Tree> members = classTree.getMembers();
            
            String packageName = SourceUtils.getPackageName(compilationUnitTree);

            if (!IndexerUtils.includePackage(suppressions, packageName)) {
                return super.visitClass(classTree, scopeTree);
            }
            
            Klass klass = new Klass();
            klass.setImports(getImports());
            klass.setSimpleClassName(simpleClassName);
            klass.setClassName(className);
            klass.setFilePath(getFilePath());
            klass.setSuperClass(getSuperClass(classTree, scopeTree));
            klass.setInterfaces(getInterfaces(classTree, scopeTree));
            
            for (Tree memberTree : members) {
                if (memberTree instanceof MethodTree) {
                    MethodTree methodTree = (MethodTree)memberTree;

                    Method method = new Method();
                    method.setName(methodTree.getName().toString());
                    method.setModifier(getModifier(methodTree));
                    method.setParameters(getParameters(methodTree, scopeTree));
                    
                    String simpleReturnType = getSimpleReturnType(methodTree);
                    method.setReturnType(SourceUtils.getClassName(scopeTree, simpleReturnType));
                    method.setSimpleReturnType(simpleReturnType);
                    method.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodTree));

                    klass.addMethod(method);
                }
            }
            
            indexClasses.add(klass);

            return super.visitClass(classTree, scopeTree);
        }

        private List<String> getImports() {
            
            List<String> results = new ArrayList<String>();

            for (ImportTree importTree : compilationUnitTree.getImports()) {
                results.add(importTree.toString());
            }
                
            return results;
        }

        private String getSuperClass(ClassTree classTree, ScopeTree scopeTree) {
            
            Tree tree = classTree.getExtendsClause();
            if (tree == null) {
                return "java.lang.Object";
            }
            
            return SourceUtils.getClassName(scopeTree, tree.toString());
        }
        
        private List<String> getInterfaces(ClassTree classTree, ScopeTree scopeTree) {
            
            List<String> results = new ArrayList<String>();

            for (Tree implementsTree : classTree.getImplementsClause()) {
                results.add(SourceUtils.getClassName(scopeTree, implementsTree.toString()));
            }
                
            return results;
        }

        private String getModifier(MethodTree methodTree) {

            ModifiersTree modifiersTree = methodTree.getModifiers();
            Set<Modifier> flags = modifiersTree.getFlags();
            for (Modifier modifier : flags) {
                return modifier.toString();
            }

            return "package";
        }

        private List<Parameter> getParameters(MethodTree methodTree, ScopeTree scopeTree) {

            List<Parameter> parameters = new ArrayList<Parameter>();
            
            for (VariableTree variableTree : methodTree.getParameters()) {
                Parameter parameter = new Parameter();
                parameter.setVariable(variableTree.getName().toString());

                String simpleType = variableTree.getType().toString();
                parameter.setSimpleType(simpleType);
                if (SourceUtils.isPrimative(simpleType)) {
                    parameter.setType(simpleType);
                } else {
                    parameter.setType(SourceUtils.getClassName(scopeTree, simpleType));
                }                
                
                parameters.add(parameter);
            }

            return parameters;
        }

        private String getSimpleReturnType(MethodTree methodTree) {

            Tree simpleReturnType = methodTree.getReturnType();
            if (simpleReturnType == null) {
                return "void";
            }
            
            return simpleReturnType.toString();
        }

        private String getFilePath() {
            
            FileObject sourceFile = compilationUnitTree.getSourceFile();
            return sourceFile.toUri().getPath();
        }
    }
}
