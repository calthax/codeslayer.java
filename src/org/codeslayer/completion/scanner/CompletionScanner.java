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
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.completion.Completion;
import org.codeslayer.completion.CompletionInput;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.*;
import org.codeslayer.source.scanner.SymbolHandler;
import org.codeslayer.source.scanner.SymbolScanner;

public class CompletionScanner {
    
    private static Logger logger = Logger.getLogger(CompletionScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final CompletionInput input;

    public CompletionScanner(HierarchyManager hierarchyManager, CompletionInput input) {
    
        this.hierarchyManager = hierarchyManager;
        this.input = input;
    }
    
    public List<Completion> scan() 
            throws Exception {
        
        List<Completion> completions = new ArrayList<Completion>();
        
        try {
            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{new File(input.getSourceFile())});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions, completions);
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            logger.error("method usage scan error", e);
        }
        
        return completions;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final List<Completion> completions;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Completion> completions) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.completions = completions;
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

            logger.debug("visitIdentifier " + identifierTree.toString());
            
            String path = identifierTree.toString();
            
            String stripComments = SourceUtils.stripComments(path);
            
            String expression = input.getExpression();
            expression = SourceUtils.stripEnds(expression);
            
            if (!expression.equals(stripComments)) {
                return scopeTree;
            }

            logger.debug("expression " + expression);
            logger.debug("stripComments " + stripComments);

            Symbol symbol = identifierTree.accept(new SymbolScanner(), null);
            if (symbol == null) {
                if (logger.isDebugEnabled()) {
                    logger.error("symbol is null");
                }
                return scopeTree;
            }

            Symbol firstSymbol = SourceUtils.findFirstSymbol(symbol);

            SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);                

            String className = symbolHandler.getType(firstSymbol, scopeTree);

            if (className == null) {
                return scopeTree;
            }

            File indexesFile = new File(input.getIndexesFolder(), "projects.indexes");
            createCompletions(indexesFile, className);
            
            return scopeTree;
        }

        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {

            super.visitMemberSelect(memberSelectTree, scopeTree);

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree);
            if (lineNumber != input.getLineNumber()) {
                return scopeTree;
            }

//            logger.debug("visitMemberSelect1 " + memberSelectTree.toString());
//            logger.debug("visitMemberSelect2 " + memberSelectTree.getExpression().toString());
//            logger.debug("visitMemberSelect3 " + memberSelectTree.getIdentifier().toString());
            
            return scopeTree;
        }
        
        @Override
        public ScopeTree visitMethodInvocation(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {

            super.visitMethodInvocation(methodInvocationTree, scopeTree);

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodInvocationTree);
            if (lineNumber != input.getLineNumber()) {
                return scopeTree;
            }

            logger.debug("visitMethodInvocation1 " + methodInvocationTree.toString());
            logger.debug("visitMethodInvocation2 " + methodInvocationTree.getMethodSelect().toString());
            
            String path = methodInvocationTree.toString();
            
            String stripComments = SourceUtils.stripComments(path);
            
            String expression = input.getExpression();
            expression = SourceUtils.stripEnds(expression);
            
            if (!expression.equals(stripComments)) {
                return scopeTree;
            }

            logger.debug("expression " + expression);
            logger.debug("stripComments " + stripComments);

            Symbol symbol = methodInvocationTree.getMethodSelect().accept(new SymbolScanner(), null);
            if (symbol == null) {
                if (logger.isDebugEnabled()) {
                    logger.error("symbol is null");
                }
                return scopeTree;
            }

            Symbol firstSymbol = SourceUtils.findFirstSymbol(symbol);

            SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);                

            String className = symbolHandler.getType(firstSymbol, scopeTree);

            if (className == null) {
                return scopeTree;
            }

            File indexesFile = new File(input.getIndexesFolder(), "projects.indexes");
            createCompletions(indexesFile, className);

            return scopeTree;
        }
        
        private void createCompletions(File file, String className) {

            try{
                FileInputStream fstream = new FileInputStream(file);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (strLine == null || strLine.trim().length() == 0) {
                        continue;
                    }

                    if (strLine.startsWith(className)) {
                        String[] split = strLine.split("\\t");

                        if (split[0].equals(className)) {
                            Completion completion = new Completion();
                            completion.setMethodName(split[3]);
                            completion.setMethodParameters(split[4]);
                            completion.setMethodParameterVariables(split[5]);
                            completion.setMethodReturnType(split[7]);
                            completions.add(completion);
                        }
                    } else if (!completions.isEmpty()) {
                        break;
                    }
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("not able to load the libs.indexes file.");
            }
        }
    }
}
