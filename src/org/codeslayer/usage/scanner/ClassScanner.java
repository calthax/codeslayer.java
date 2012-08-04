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
package org.codeslayer.usage.scanner;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.List;
import org.codeslayer.source.*;

public class ClassScanner {
    
    private final HierarchyManager hierarchyManager;
    private final String className;

    public ClassScanner(HierarchyManager hierarchyManager, String className) {
     
        this.hierarchyManager = hierarchyManager;
        this.className = className;
    }
    
    public Klass scan() {
        
        try {
            Hierarchy hierarchy = hierarchyManager.getHierarchy(className);
            if (hierarchy == null) {
                return null;
            }
            
            File file = new File(hierarchy.getFilePath());

            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{file});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                ScopeTreeFactory scopeTreeFactory = new ScopeTreeFactory(compilationUnitTree);
                ScopeTree scopeTree = scopeTreeFactory.createScopeTree();

                Klass klass = new Klass();
                InternalScanner internalScanner = new InternalScanner(compilationUnitTree, sourcePositions, klass);
                compilationUnitTree.accept(internalScanner, scopeTree);
                return klass;
            }            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return null;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final Klass klass;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Klass klass) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.klass = klass;
        }

        @Override
        public ScopeTree visitImport(ImportTree importTree, ScopeTree scopeTree) {

            super.visitImport(importTree, scopeTree);

            String importName = importTree.getQualifiedIdentifier().toString();
            Import impt = new Import(importName, importTree.isStatic());
            scopeTree.addImport(impt);

            return scopeTree;
        }

        @Override
        public ScopeTree visitVariable(VariableTree variableTree, ScopeTree scopeTree) {

            super.visitVariable(variableTree, scopeTree);

            String type = variableTree.getType().toString();
            String variable = variableTree.getName().toString();
            scopeTree.addSimpleType(variable, type);

            return scopeTree;                    
        }

        @Override
        public ScopeTree visitClass(ClassTree classTree, ScopeTree scopeTree) {

            super.visitClass(classTree, scopeTree);

            List<? extends Tree> members = classTree.getMembers();

            klass.setImports(SourceUtils.getImports(compilationUnitTree));
            klass.setSimpleClassName(SourceUtils.getSimpleClassName(compilationUnitTree));
            klass.setClassName(SourceUtils.getClassName(compilationUnitTree));
            klass.setFilePath(SourceUtils.getSourceFilePath(compilationUnitTree));
            klass.setSuperClass(SourceUtils.getSuperClass(classTree, scopeTree));
            klass.setInterfaces(SourceUtils.getInterfaces(classTree, scopeTree));

            for (Tree memberTree : members) {
                if (memberTree instanceof MethodTree) {
                    MethodTree methodTree = (MethodTree)memberTree;
                    
                    Method method = new Method();
                    method.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodTree));
                    method.setName(methodTree.getName().toString());
                    method.setParameters(SourceUtils.getParameters(methodTree, scopeTree));
                    
                    Tree returnType = methodTree.getReturnType();
                    if (returnType != null) {
                        String simpleReturnType = returnType.toString();
                        method.setReturnType(SourceUtils.getClassName(scopeTree, simpleReturnType));
                        method.setSimpleReturnType(simpleReturnType);
                    }
                    
                    klass.addMethod(method);
                }
            }

            return scopeTree;
        }
    }
}
