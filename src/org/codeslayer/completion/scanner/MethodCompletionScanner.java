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
package org.codeslayer.completion.scanner;

import java.io.File;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.source.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.completion.Input;
import org.codeslayer.indexer.Index;
import org.codeslayer.indexer.IndexFactory;
import org.codeslayer.indexer.SourceIndexer;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.*;
import org.codeslayer.usage.domain.Usage;
import org.codeslayer.usage.domain.UsageManager;

public class MethodCompletionScanner {
    
    private static Logger logger = Logger.getLogger(MethodCompletionScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final Input input;

    public MethodCompletionScanner(HierarchyManager hierarchyManager, Input input) {
    
        this.hierarchyManager = hierarchyManager;
        this.input = input;
    }
    
    public Usage scan() 
            throws Exception {
        
        UsageManager usageManager = new UsageManager();
        
        try {
            File sourceFile = input.getSourceFile();
            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{sourceFile});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions, hierarchyManager, usageManager);
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            logger.error("method usage scan error", e);
        }
        
        List<Usage> usages = usageManager.getUsages();
        if (usages != null && usages.isEmpty()) {
            return null;
        }
        
        return usages.get(0);
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final HierarchyManager hierarchyManager;
        private final File sourceFile;
        private final UsageManager usageManager;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, HierarchyManager hierarchyManager, UsageManager usageManager) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.hierarchyManager = hierarchyManager;
            this.sourceFile = SourceUtils.getSourceFile(compilationUnitTree);
            this.usageManager = usageManager;
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

            String simpleType = variableTree.getType().toString();
            String variable = variableTree.getName().toString();
            scopeTree.addSimpleType(variable, simpleType);

            return scopeTree;                    
        }

        /**
         * Find all occurrences of the method that we are trying to find. This will get most of the information
         * that we are interested in. However we will still need to go through the visitMethodInvocation() to 
         * find the method parameters.
         */
        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {

            super.visitMemberSelect(memberSelectTree, scopeTree);

//            if (!input.getSymbol().toString().equals(memberSelectTree.getIdentifier().toString())) {
//                return scopeTree;
//            }
            
            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree);
            if (lineNumber != input.getLineNumber()) {
                return scopeTree;
            }
            
            int startPosition = SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, memberSelectTree);
            int endPosition = SourceUtils.getEndPosition(compilationUnitTree, sourcePositions, memberSelectTree);
            
            logger.debug("** startPosition: " + startPosition + " endPosition: " + endPosition + " **");
            
            
            //filterSet.addFilter(filter);
            
                
            if (logger.isDebugEnabled()) {
                logger.debug("** scan class " + SourceUtils.getClassLogInfo(compilationUnitTree, sourcePositions, memberSelectTree) + " **");
            }
            
//            Symbol symbol = memberSelectTree.getExpression().accept(new SymbolScanner(), null);
//            if (symbol == null) {
//                if (logger.isDebugEnabled()) {
//                    logger.error("symbol is null");
//                }
//                return scopeTree;
//            }
//
//            Symbol firstSymbol = SourceUtils.findFirstSymbol(symbol);
//
//            SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);                
//
//            // assume this is a method of this class
//            
//            String className = symbolHandler.getType(firstSymbol, scopeTree);
//
//            if (className == null) {
//                return scopeTree;
//            }
//
////            if (!SourceUtils.hasMethodMatch(hierarchyManager, methodMatch, className)) {
////                return scopeTree;
////            }
//
//            Method method = new Method();
//            method.setName(input.getSymbol().toString());
//            Clazz clazz = new Clazz();
//            clazz.setClassName(className);
//            clazz.setSimpleClassName(SourceUtils.getSimpleType(className));
//            clazz.addMethod(method);
//
//            usageManager.addUsage(createUsage(method));

            return scopeTree;
        }
        
        private Usage createUsage(Method method) {
            
            Usage usage = new Usage();
            usage.setMethod(method);
            usage.setClassName(method.getClazz().getClassName());
            usage.setSimpleClassName(method.getClazz().getSimpleClassName());            
            
            IndexFactory indexFactory = new IndexFactory();
            List<Hierarchy> hierarchyList = hierarchyManager.getHierarchyList(method.getClazz().getClassName());
            File[] files = getHierarchyFiles(hierarchyList);
            
            List<String> suppressions = Collections.emptyList();
            SourceIndexer sourceIndexer = new SourceIndexer(files, indexFactory, suppressions);
            
            try {
                List<Index> Indexes = sourceIndexer.createIndexes();
                for (Index index : Indexes) {
                    String methodName = index.getMethodName();
                    if (method.getName().equals(methodName)) {
                        usage.setFile(new File(index.getFilePath()));
                        usage.setLineNumber(index.getLineNumber());
                    }                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return usage;
        }
        
        private File[] getHierarchyFiles(List<Hierarchy> hierarchyList) {

            List<File> files = new ArrayList<File>();
            
            for (Hierarchy hierarchy : hierarchyList) {
                files.add(new File(hierarchy.getFilePath()));
            }
            
            return files.toArray(new File[files.size()]);
        }
    }
}
