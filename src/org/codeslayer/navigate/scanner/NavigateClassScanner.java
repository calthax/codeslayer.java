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

import java.io.File;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.source.tree.*;
import org.apache.log4j.Logger;
import org.codeslayer.navigate.Navigate;
import org.codeslayer.navigate.NavigateInput;
import org.codeslayer.navigate.NavigateManager;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.*;

public class NavigateClassScanner {
    
    private static Logger logger = Logger.getLogger(NavigateClassScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final NavigateInput input;

    public NavigateClassScanner(HierarchyManager hierarchyManager, NavigateInput input) {
    
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
                
            String className = SourceUtils.getClassName(scopeTree, identifierTree.getName().toString());
            Hierarchy hierarchy = hierarchyManager.getHierarchy(className);
            if (hierarchy == null) {
                return scopeTree;
            }
            
            String filePath = hierarchy.getFilePath();

            Navigate navigate = new Navigate();
            navigate.setFilePath(filePath);
            navigate.setLineNumber(0);
            navigateManager.setNavigate(navigate);

            return scopeTree;
        }
    }
}
