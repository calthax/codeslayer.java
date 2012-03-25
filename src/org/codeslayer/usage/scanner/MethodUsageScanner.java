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
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.codeslayer.usage.domain.*;

public class MethodUsageScanner extends AbstractScanner {
    
    private final MethodMatch methodMatch;

    public MethodUsageScanner(MethodMatch methodMatch) {
    
        this.methodMatch = methodMatch;
    }
    
    public List<Usage> scan() 
            throws Exception {
        
        List<Usage> usages = new ArrayList<Usage>();

        try {
            JavacTask javacTask = getJavacTask(methodMatch.getSourceFolders());
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new MethodScanner(compilationUnitTree, sourcePositions, usages);
                ScopeTree scopeTree = new ScopeTree();
                scopeTree.setPackageName(getPackageName(compilationUnitTree));
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return usages;
    }
    
    private class MethodScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final List<Usage> usages;

        private MethodScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Usage> usages) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.usages = usages;
        }
        
        @Override
        public ScopeTree visitImport(ImportTree importTree, ScopeTree scopeTree) {
        
            super.visitImport(importTree, scopeTree);
            
            String importName = importTree.getQualifiedIdentifier().toString();
            scopeTree.addImportName(importName);
            
            return scopeTree;
        }

        @Override
        public ScopeTree visitVariable(VariableTree variableTree, ScopeTree scopeTree) {
            
            super.visitVariable(variableTree, scopeTree);
            
            String type = variableTree.getType().toString();
            String name = variableTree.getName().toString();
//            System.out.println("type " + type);
//            System.out.println("name " + name);
            scopeTree.addVariable(name, type);
            
            return scopeTree;                    
        }
        
        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {
            
            super.visitMemberSelect(memberSelectTree, scopeTree);
            
            if (methodMatch.getName().toString().equals(memberSelectTree.getIdentifier().toString())) {

                SymbolManager symbolManager = new SymbolManager();
                memberSelectTree.accept(new SymbolScanner(), symbolManager);
                
//                for (Results.Result result : results.get()) {
//                    System.out.println("result " + result.getType() + ":" + result.getValue());
//                }
                
                String packageName = getPackageName(compilationUnitTree);
                String className = getClassName(compilationUnitTree);
                
                Usage usage = new Usage();
                usage.setClassName(packageName + "." + className);
                usage.setSimpleClassName(className);
                usage.setMethodName(methodMatch.getName());
                usage.setFile(new File(compilationUnitTree.getSourceFile().toUri().toString()));                
                usage.setLineNumber(getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree));
                usage.setStartPosition(getStartPosition(compilationUnitTree, sourcePositions, memberSelectTree));
                usage.setEndPosition(getEndPosition(compilationUnitTree, sourcePositions, memberSelectTree));
                
                usages.add(usage);
            }
         
            return scopeTree;
        }
        
        @Override
        public ScopeTree visitMethodInvocation(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {
            
            super.visitMethodInvocation(methodInvocationTree, scopeTree);
            
            int lineNumber = getLineNumber(compilationUnitTree, sourcePositions, methodInvocationTree);
            int startPosition = getStartPosition(compilationUnitTree, sourcePositions, methodInvocationTree);

            for (Usage usage : usages) {                
                if (lineNumber != usage.getLineNumber() || startPosition != usage.getStartPosition()) {
                    continue;
                }
                
                File file = new File(compilationUnitTree.getSourceFile().toUri().toString());
                if (!file.equals(usage.getFile())) {
                    continue;
                }

                addMethodArguments(usage, methodInvocationTree, scopeTree);
                break;
            }

            return scopeTree;
        }
        
        private void addMethodArguments(Usage usage, MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {
            
            List<? extends ExpressionTree> expressionTrees = methodInvocationTree.getArguments();
            for (ExpressionTree expressionTree : expressionTrees) {
                
                System.out.println("argument " + expressionTree.getKind() + " -- " + expressionTree);
                
                Kind kind = expressionTree.getKind();
                String name = expressionTree.toString();

                if (kind == Tree.Kind.IDENTIFIER) { // items
                    String variable = scopeTree.getVariable(name);
                    if (variable != null) {
                        usage.addMethodArgument(variable);
                    }
                } else if (kind == Tree.Kind.METHOD_INVOCATION) { // dao.getPresidents()
                    String methodArgument = getMethodArgument(expressionTree, scopeTree);
                    String variable = scopeTree.getVariable(methodArgument);
                    if (variable != null) {
                        usage.addMethodArgument(variable);
                    }
                } else if (kind == Tree.Kind.NEW_CLASS) { // new AllItems()
                    NewClassTree newClassTree = (NewClassTree) expressionTree;
                    String identifier = newClassTree.getIdentifier().toString();
                    String variable = scopeTree.getVariable(identifier);
                    if (variable != null) {
                        usage.addMethodArgument(variable);
                    }
                }
            }
        }
        
        private String getMethodArgument(ExpressionTree expressionTree, ScopeTree scopeTree) {
            
            MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionTree;

            SymbolManager symbolManager = new SymbolManager();

            methodInvocationTree.accept(new SymbolScanner(), symbolManager);

            for (Symbol symbol : symbolManager.getSymbols()) {
                SymbolType symbolType = symbol.getType();
                if (symbolType == SymbolType.IDENTIFIER) {
                    String variable = scopeTree.getVariable(symbol.getValue());
                    
                    if (variable == null) {
                        continue;
                        //throw new IllegalStateException("Not able to find the variable " + symbol.getValue());
                    }
                    
                    String className = scopeTree.getClassName(variable);
                    
                    if (className == null) {
                        continue;
                        //throw new IllegalStateException("Not able to find the class name " + variable);
                    }
                    
                    System.out.println("className " + className);
                }
            }
            
            return null;
        }
    }
}
