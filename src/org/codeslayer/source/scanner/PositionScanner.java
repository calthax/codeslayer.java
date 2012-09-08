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
package org.codeslayer.source.scanner;

import java.io.File;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.source.tree.*;
import org.apache.log4j.Logger;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.*;

public class PositionScanner {
    
    private static Logger logger = Logger.getLogger(PositionScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final PositionInput positionInput;

    public PositionScanner(HierarchyManager hierarchyManager, PositionInput positionInput) {
    
        this.hierarchyManager = hierarchyManager;
        this.positionInput = positionInput;
    }
    
    public PositionResult scan() 
            throws Exception {
        
        PositionResult positionResult = new PositionResult();
        positionResult.setHierarchyManager(hierarchyManager);
        
        try {
            String sourceFile = positionInput.getSourceFile();
            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{new File(sourceFile)});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions, positionResult);
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            logger.error("method usage scan error", e);
        }
        
        return positionResult;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final PositionResult positionResult;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, PositionResult methodPositionResult) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.positionResult = methodPositionResult;
            
            methodPositionResult.setCompilationUnitTree(compilationUnitTree);
            methodPositionResult.setSourcePositions(sourcePositions);
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
            configureResult(identifierTree, scopeTree);
            return scopeTree;
        }
        
        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {

            super.visitMemberSelect(memberSelectTree, scopeTree);
            configureResult(memberSelectTree, scopeTree);
            return scopeTree;
        }
        
        private void configureResult(Tree tree, ScopeTree scopeTree) {
            
            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, tree);
            if (lineNumber != positionInput.getLineNumber()) {
                return;
            }
                
            int startPosition = SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, tree);
            int endPosition = SourceUtils.getEndPosition(compilationUnitTree, sourcePositions, tree);
                
            int position = positionInput.getPosition();
            if (position < startPosition || position > endPosition) {
                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("** scan class " + SourceUtils.getClassLogInfo(compilationUnitTree, sourcePositions, tree) + " **");
            }
            
            positionResult.setScopeTree(scopeTree);
            positionResult.setTree(tree);
            
            logger.debug("tree: " + tree);
            logger.debug("startPosition: " + startPosition);
            logger.debug("endPosition: " + endPosition);
        }
    }
}
