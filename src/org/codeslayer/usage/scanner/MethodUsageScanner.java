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
import com.sun.source.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codeslayer.indexer.domain.IndexClass;
import org.codeslayer.indexer.domain.IndexMethod;
import org.codeslayer.usage.domain.*;
import org.codeslayer.usage.factory.ScopeTreeFactory;

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
            JavacTask javacTask = ScannerUtils.getJavacTask(input.getSourceFolders());
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
        private final List<Usage> usages;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Usage> usages) {

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
    //      System.out.println("type " + type);
    //      System.out.println("name " + name);
            scopeTree.addVariable(name, type);

            return scopeTree;                    
        }

        /**
         * Find all occurrences of method that we are trying to find. This will most of the method information
         * that we are interested in. However we will still need to go through the visitMethodInvocation() method
         * to find the method parameters.
         */
        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {

            super.visitMemberSelect(memberSelectTree, scopeTree);

            if (methodMatch.getName().toString().equals(memberSelectTree.getIdentifier().toString())) {

                SymbolManager symbolManager = new SymbolManager();
                memberSelectTree.accept(new SymbolScanner(), symbolManager);

    //          for (Results.Result result : results.get()) {
    //              System.out.println("result " + result.getType() + ":" + result.getValue());
    //          }

                String packageName = ScannerUtils.getPackageName(compilationUnitTree);
                String simpleClassName = ScannerUtils.getSimpleClassName(compilationUnitTree);

                Usage usage = new Usage();
                usage.setClassName(packageName + "." + simpleClassName);
                usage.setSimpleClassName(simpleClassName);
                usage.setMethodName(methodMatch.getName());
                usage.setFile(new File(compilationUnitTree.getSourceFile().toUri().toString()));                
                usage.setLineNumber(ScannerUtils.getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree));
                usage.setStartPosition(ScannerUtils.getStartPosition(compilationUnitTree, sourcePositions, memberSelectTree));
                usage.setEndPosition(ScannerUtils.getEndPosition(compilationUnitTree, sourcePositions, memberSelectTree));

                usages.add(usage);
            }

            return scopeTree;
        }

        /**
         * At this point we have the method usages figured out, but we still need the method parameters.
         */
        @Override
        public ScopeTree visitMethodInvocation(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {

            super.visitMethodInvocation(methodInvocationTree, scopeTree);

            int lineNumber = ScannerUtils.getLineNumber(compilationUnitTree, sourcePositions, methodInvocationTree);
            int startPosition = ScannerUtils.getStartPosition(compilationUnitTree, sourcePositions, methodInvocationTree);

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

                System.out.println("argument " + usage.getSimpleClassName() + " : " + expressionTree.getKind() + " -- " + expressionTree);

                Tree.Kind kind = expressionTree.getKind();
                String name = expressionTree.toString();

                if (kind == Tree.Kind.IDENTIFIER) { // items
                    String variable = scopeTree.getVariable(name);
                    if (variable != null) {
                        usage.addMethodArgument(variable);
                    }
                } else if (kind == Tree.Kind.METHOD_INVOCATION) { // dao.getPresidents()
                    String methodArgument = getMethodArgument(expressionTree, scopeTree);
                    System.out.println("methodArgument " + methodArgument);
                    usage.addMethodArgument(methodArgument);
                    
//                    String variable = scopeTree.getVariable(methodArgument);
//                    if (variable != null) {
//                        usage.addMethodArgument(variable);
//                    }
                } else if (kind == Tree.Kind.NEW_CLASS) { // new AllItems()
                    NewClassTree newClassTree = (NewClassTree) expressionTree;
                    String identifier = newClassTree.getIdentifier().toString();
                    System.out.println("class identifier " + identifier);
                    String variable = scopeTree.getClassName(identifier);
                    if (variable != null) {
                        System.out.println("class variable " + variable);
                        usage.addMethodArgument(variable);
                    }
                }
            }
        }

        private String getMethodArgument(ExpressionTree expressionTree, ScopeTree scopeTree) {

            MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionTree;
            SymbolManager symbolManager = new SymbolManager();
            methodInvocationTree.accept(new SymbolScanner(), symbolManager);
            
            //symbol  type: [IDENTIFIER] value: [presidentService]
            //symbol  type: [MEMBER] value: [getPresidents]
            
            for (Symbol symbol : symbolManager.getSymbols()) {
                System.out.println("symbol " + symbol);
            }

            for (Symbol symbol : symbolManager.getSymbols()) {
                SymbolType symbolType = symbol.getType();
                if (symbolType == SymbolType.IDENTIFIER) {
                    String variable = scopeTree.getVariable(symbol.getValue());
                    if (variable != null) {
                        Iterator<Symbol> iterator = symbolManager.getSymbols().iterator();
                        Method method = getMethod(iterator, scopeTree);
                        IndexClass indexClass = getIndexClass(method.getClassName());
                        for (IndexMethod indexMethod : indexClass.getMethods()) {
                            System.out.println(">> " + indexMethod.getName() + " : " + indexMethod.getParametersVariables());
                            if (indexMethod.getName().equals(method.getName())) { // still need to compare the method parameters
                                return indexMethod.getReturnType();
                            }
                        }
                    } else { // must be a method of this class
                        Method methodToFind = new Method(); // would create this from the symbol
                        methodToFind.setName(symbol.getValue());
                        String className = getClassMethod(methodToFind).getReturnType();
                        return className;
//                        System.out.println("className " + className);
                    }
                }
            }

            return null;
        }
        
        private Method getMethod(Iterator<Symbol> iterator, ScopeTree scopeTree) {
            
            Method method = new Method();
            
            while(iterator.hasNext()) {
                Symbol symbol = iterator.next();
                switch (symbol.getType()) {
                    case IDENTIFIER:
                        String variable = scopeTree.getVariable(symbol.getValue());
                        String className = scopeTree.getClassName(variable);
                        method.setClassName(className);
                        break;
                    case MEMBER:
                        method.setName(symbol.getValue());
                        break;
                    case ARG:
                        Parameter parameter = new Parameter();
                        parameter.setName(symbol.getValue());
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
        
        private IndexClass getIndexClass(String className) {

            IndexClass indexClass = null;

            try{
                FileInputStream fstream = new FileInputStream(input.getIndexesFile());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (strLine == null || strLine.trim().length() == 0) {
                        continue;
                    }

                    if (strLine.startsWith(className)) {
                        String[] split = strLine.split("\\t");

                        if (indexClass == null) {
                            indexClass = new IndexClass();
                            indexClass.setClassName(split[0]);
                            indexClass.setSimpleClassName(split[1]);
                        }

                        IndexMethod indexMethod = new IndexMethod();
                        indexMethod.setModifier(split[2]);
                        indexMethod.setName(split[3]);
                        indexMethod.setParameters(split[4]);
                        indexMethod.setParametersVariables(split[5]);
                        indexMethod.setParametersTypes(split[6]);
                        indexMethod.setReturnType(split[7]);

                        indexClass.addMethod(indexMethod);
                    } else if (indexClass != null) {
                        break;
                    }
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("not able to load the libs.indexes file.");
            }

            return indexClass;
        }
    }
}
