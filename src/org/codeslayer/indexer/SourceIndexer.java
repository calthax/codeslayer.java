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
import com.sun.source.util.TreeScanner;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.*;
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
            Import impt = new Import(importName, importTree.isStatic());
            scopeTree.addImport(impt);
            return super.visitImport(importTree, scopeTree);
        }
        
        @Override
        public ScopeTree visitClass(ClassTree classTree, ScopeTree scopeTree) {
            
            List<? extends Tree> members = classTree.getMembers();
            
            String packageName = SourceUtils.getPackageName(compilationUnitTree);

            if (!IndexerUtils.includePackage(suppressions, packageName)) {
                return super.visitClass(classTree, scopeTree);
            }
            
            System.out.println("className " + className);
            
            Klass klass = new Klass();
            klass.setImports(SourceUtils.getImports(compilationUnitTree));
            klass.setSimpleClassName(simpleClassName);
            klass.setClassName(className);
            klass.setFilePath(SourceUtils.getSourceFilePath(compilationUnitTree));
            klass.setSuperClass(SourceUtils.getSuperClass(classTree, scopeTree));
            klass.setInterfaces(SourceUtils.getInterfaces(classTree, scopeTree));
            
            for (Tree memberTree : members) {
                if (memberTree instanceof MethodTree) {
                    MethodTree methodTree = (MethodTree)memberTree;

                    Method method = new Method();
                    method.setName(methodTree.getName().toString());
                    method.setModifier(SourceUtils.getModifier(methodTree));
                    method.setParameters(SourceUtils.getParameters(methodTree, scopeTree));
                    
                    String simpleReturnType = SourceUtils.getSimpleReturnType(methodTree);
                    method.setReturnType(SourceUtils.getClassName(scopeTree, simpleReturnType));
                    method.setSimpleReturnType(simpleReturnType);
                    method.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodTree));

                    klass.addMethod(method);
                }
            }
            
            indexClasses.add(klass);
            
            return super.visitClass(classTree, scopeTree);
        }
    }
}
