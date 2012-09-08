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
package org.codeslayer.navigate.scanner;

import org.codeslayer.source.Symbol;
import org.codeslayer.source.scanner.SymbolHandler;
import org.codeslayer.source.scanner.SymbolScanner;
import java.util.List;
import java.io.File;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.source.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.Index;
import org.codeslayer.indexer.IndexFactory;
import org.codeslayer.indexer.SourceIndexer;
import org.codeslayer.navigate.Navigate;
import org.codeslayer.navigate.NavigateInput;
import org.codeslayer.navigate.NavigateManager;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.*;

public class NavigateMethodScanner {
    
    private static Logger logger = Logger.getLogger(NavigateMethodScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final NavigateInput input;

    public NavigateMethodScanner(HierarchyManager hierarchyManager, NavigateInput input) {
    
        this.hierarchyManager = hierarchyManager;
        this.input = input;
    }
    
    public Navigate scan() 
            throws Exception {
        
        NavigateManager navigateManager = new NavigateManager();
        
        try {
            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{new File(input.getSourceFile())});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions, navigateManager);
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            logger.error("method usage scan error", e);
        }
        
        return navigateManager.getNavigate();
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final NavigateManager navigateManager;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, NavigateManager navigateManager) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.navigateManager = navigateManager;
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

        @Override
        public ScopeTree visitIdentifier(IdentifierTree identifierTree, ScopeTree scopeTree) {
            
            super.visitIdentifier(identifierTree, scopeTree);

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, identifierTree);
            if (lineNumber != input.getLineNumber()) {
                return scopeTree;
            }

            if (!input.getSymbol().equals(identifierTree.getName().toString())) {
                return scopeTree;
            }
                
            Method staticMethod = SourceUtils.getStaticMethod(scopeTree, identifierTree.getName().toString());
            if (staticMethod != null) {
                Navigate navigate = createNavigate(staticMethod);
                navigateManager.setNavigate(navigate);
            } 

            SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);

            Symbol firstSymbol = new Symbol(identifierTree.getName().toString());

            String className = symbolHandler.getType(firstSymbol, scopeTree);

            Method method = new Method();
            method.setName(identifierTree.getName().toString());
            Clazz clazz = new Clazz();
            clazz.setClassName(className);
            clazz.setSimpleClassName(SourceUtils.getSimpleType(className));
            clazz.addMethod(method);
            
            Navigate navigate = createNavigate(method);
            navigateManager.setNavigate(navigate);
            
            return scopeTree;
        }

        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {

            super.visitMemberSelect(memberSelectTree, scopeTree);

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree);
            if (lineNumber != input.getLineNumber()) {
                return scopeTree;
            }

            if (!input.getSymbol().equals(memberSelectTree.getIdentifier().toString())) {
                return scopeTree;
            }
                
            Symbol symbol = memberSelectTree.getExpression().accept(new SymbolScanner(), null);
            if (symbol == null) {
                if (logger.isDebugEnabled()) {
                    logger.error("symbol is null");
                }
                return scopeTree;
            }

            Symbol firstSymbol = SourceUtils.findFirstSymbol(symbol);

            SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);                

            // assume this is a method of this class

            String className = symbolHandler.getType(firstSymbol, scopeTree);

            if (className == null) {
                return scopeTree;
            }

//            if (!SourceUtils.hasMethodMatch(hierarchyManager, methodMatch, className)) {
//                return scopeTree;
//            }

            Method method = new Method();
            method.setName(memberSelectTree.getIdentifier().toString());
            Clazz clazz = new Clazz();
            clazz.setClassName(className);
            clazz.setSimpleClassName(SourceUtils.getSimpleType(className));
            clazz.addMethod(method);

            Navigate navigate = createNavigate(method);
            navigateManager.setNavigate(navigate);

            return scopeTree;
        }
        
        private Navigate createNavigate(Method method) {

            Navigate navigateOutput = new Navigate();

            IndexFactory indexFactory = new IndexFactory();
            List<Hierarchy> hierarchyList = hierarchyManager.getHierarchyList(method.getClazz().getClassName());
            List<File> files = getHierarchyFiles(hierarchyList);

            List<String> suppressions = Collections.emptyList();
            SourceIndexer sourceIndexer = new SourceIndexer(files, indexFactory, suppressions);

            try {
                List<Index> Indexes = sourceIndexer.createIndexes();
                for (Index index : Indexes) {
                    String methodName = index.getMethodName();
                    if (method.getName().equals(methodName)) {
                        navigateOutput.setFilePath(index.getFilePath());
                        navigateOutput.setLineNumber(index.getLineNumber());
                    }                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return navigateOutput;
        }

        private List<File> getHierarchyFiles(List<Hierarchy> hierarchyList) {

            List<File> files = new ArrayList<File>();

            for (Hierarchy hierarchy : hierarchyList) {
                files.add(new File(hierarchy.getFilePath()));
            }

            return files;
        }
    }
}
