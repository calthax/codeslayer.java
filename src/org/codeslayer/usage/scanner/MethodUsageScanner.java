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

import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.Parameter;
import com.sun.source.tree.*;
import com.sun.source.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.source.Klass;
import org.codeslayer.source.Method;
import org.codeslayer.usage.domain.*;
import org.codeslayer.source.ScopeTreeFactory;

public class MethodUsageScanner {
    
    private final Method methodMatch;
    private final Input input;

    public MethodUsageScanner(Method methodMatch, Input input) {
    
        this.methodMatch = methodMatch;
        this.input = input;
    }
    
    public List<Usage> scan() 
            throws Exception {
        
        List<Usage> usages = new ArrayList<Usage>();

        try {
            JavacTask javacTask = SourceUtils.getJavacTask(input.getSourceFolders());
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions, usages);
                ScopeTreeFactory scopeTreeFactory = new ScopeTreeFactory(compilationUnitTree);
                ScopeTree scopeTree = scopeTreeFactory.createScopeTree();
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return usages;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final File sourceFile;
        private final List<Usage> usages;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Usage> usages) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.usages = usages;
            this.sourceFile = SourceUtils.getSourceFile(compilationUnitTree);
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

            if (methodMatch.getName().toString().equals(memberSelectTree.getIdentifier().toString())) {

                SymbolManager symbolManager = new SymbolManager();
                memberSelectTree.accept(new SymbolScanner(), symbolManager);

                Usage usage = new Usage();
                usage.setClassName(SourceUtils.getClassName(compilationUnitTree));
                usage.setSimpleClassName(SourceUtils.getSimpleClassName(compilationUnitTree));
                usage.setMethodName(methodMatch.getName());
                usage.setFile(new File(compilationUnitTree.getSourceFile().toUri().toString()));                
                usage.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree));
                usage.setStartPosition(SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, memberSelectTree));
                usage.setEndPosition(SourceUtils.getEndPosition(compilationUnitTree, sourcePositions, memberSelectTree));

                usages.add(usage);
            }

            return scopeTree;
        }

        /**
         * At this point we have the method usages figured out, but now we need the method parameters.
         */
        @Override
        public ScopeTree visitMethodInvocation(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {

            super.visitMethodInvocation(methodInvocationTree, scopeTree);

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodInvocationTree);
            int startPosition = SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, methodInvocationTree);

            for (Usage usage : usages) {
                if (lineNumber != usage.getLineNumber() || startPosition != usage.getStartPosition()) {
                    continue;
                }

                if (!sourceFile.equals(usage.getFile())) {
                    continue;
                }
                
                addMethodParameters(usage, methodInvocationTree, scopeTree);
                break;
            }

            return scopeTree;
        }

        private void addMethodParameters(Usage usage, MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {

            List<? extends ExpressionTree> expressionTrees = methodInvocationTree.getArguments();
            for (ExpressionTree expressionTree : expressionTrees) {

//                System.out.println("parameter " + usage.getSimpleClassName() + " : " + expressionTree.getKind() + " -- " + expressionTree);

                Tree.Kind kind = expressionTree.getKind();
                String name = expressionTree.toString();

                if (kind == Tree.Kind.IDENTIFIER) { // items
                    Parameter parameter = new Parameter();
                    
                    String simpleType = scopeTree.getSimpleType(name);
                    String className = SourceUtils.getClassName(scopeTree, simpleType);
                    
                    parameter.setSimpleType(simpleType);
                    parameter.setType(className);
                    
                    usage.addMethodParameter(parameter);
                } else if (kind == Tree.Kind.METHOD_INVOCATION) { // dao.getPresidents()
                    Parameter parameter = new Parameter();
                    
                    String type = getParameterType(expressionTree, scopeTree);
                    parameter.setType(type);
                    
                    usage.addMethodParameter(parameter);
                } else if (kind == Tree.Kind.NEW_CLASS) { // new AllItems()
                    Parameter parameter = new Parameter();
                    
                    NewClassTree newClassTree = (NewClassTree) expressionTree;
                    String simpleType = newClassTree.getIdentifier().toString();
                    String className = SourceUtils.getClassName(scopeTree, simpleType);

                    parameter.setSimpleType(simpleType);
                    parameter.setType(className);
                    
                    usage.addMethodParameter(parameter);
                }
            }
        }

        private String getParameterType(ExpressionTree expressionTree, ScopeTree scopeTree) {

            MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionTree;
            SymbolManager symbolManager = new SymbolManager();
            methodInvocationTree.accept(new SymbolScanner(), symbolManager);
            
            //symbol  type: [IDENTIFIER] value: [presidentService]
            //symbol  type: [MEMBER] value: [getPresidents]
            
//            for (Symbol symbol : symbolManager.getSymbols()) {
//                System.out.println("symbol " + symbol);
//            }

            for (Symbol symbol : symbolManager.getSymbols()) {
                SymbolType symbolType = symbol.getSymbolType();
                if (symbolType == SymbolType.IDENTIFIER) {
                    String simpleType = scopeTree.getSimpleType(symbol.getValue());
                    if (simpleType != null) {
                        Iterator<Symbol> iterator = symbolManager.getSymbols().iterator();
                        Method method = getMethod(iterator, scopeTree);
                        
                        Klass klass  = IndexerUtils.getIndexKlass(input.getIndexesFile(), method.getClassName());
                        for (Method klassMethod : klass.getMethods()) {
                            if (klassMethod.getName().equals(klassMethod.getName())) { // still need to compare the method parameters
                                return klassMethod.getReturnType();
                            }                        
                        }
                    } else { // must be a method of this class
                        Method methodToFind = new Method(); // would create this from the symbol
                        methodToFind.setName(symbol.getValue());
                        String className = getClassMethod(methodToFind).getReturnType();
                        return className;
                    }
                }
            }

            return null;
        }
        
        private Method getMethod(Iterator<Symbol> iterator, ScopeTree scopeTree) {
            
            Method method = new Method();
            
            while(iterator.hasNext()) {
                Symbol symbol = iterator.next();
                switch (symbol.getSymbolType()) {
                    case IDENTIFIER:
                        String simpleType = scopeTree.getSimpleType(symbol.getValue());
                        String className = SourceUtils.getClassName(scopeTree, simpleType);
                        method.setClassName(className);
                        break;
                    case MEMBER:
                        method.setName(symbol.getValue());
                        break;
                    case ARG:
                        Parameter parameter = new Parameter();
                        parameter.setVariable(symbol.getValue());
                        method.addParameter(parameter);
                        break;
                    default:
                        return method;
                }
            }
            
            return method;
        }
        
        private Method getClassMethod(Method methodToFind) {
            
            List<Method> methods = new ArrayList<Method>();

            ScopeTreeFactory scopeTreeFactory = new ScopeTreeFactory(compilationUnitTree);
            ScopeTree scopeTree = scopeTreeFactory.createScopeTree();
            
            MethodScanner methodScanner = new MethodScanner(compilationUnitTree, sourcePositions, methodToFind.getName(), methods);
            
            compilationUnitTree.accept(methodScanner, scopeTree);
            
            for (Method method : methods) {
                return method;
            }
            
            return null;
        }
    }
}
