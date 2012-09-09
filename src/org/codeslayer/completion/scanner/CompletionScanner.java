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
import org.apache.log4j.Logger;
import org.codeslayer.completion.CompletionInput;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.*;

public class CompletionScanner {
    
    private static Logger logger = Logger.getLogger(CompletionScanner.class);
    
    private final CompletionInput input;

    public CompletionScanner(CompletionInput input) {
    
        this.input = input;
    }
    
    public ScopeContext scan() 
            throws Exception {
        
        ScopeContext scopeContext = new ScopeContext();
        
        try {
            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{new File(input.getSourceFile())});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions);
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(scanner, scopeTree);
                
                scopeContext.setScopeTree(scopeTree);
                scopeContext.setCompilationUnitTree(compilationUnitTree);
            }
        } catch (Exception e) {
            logger.error("completion scan error", e);
        }
        
        return scopeContext;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
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

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, variableTree);
            if (lineNumber <= input.getLineNumber()) {
                return scopeTree;
            }
            
            return scopeTree;                    
        }
    }
}
