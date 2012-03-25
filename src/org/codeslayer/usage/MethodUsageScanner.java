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
package org.codeslayer.usage;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                TreeScanner<ScopeTree, ScopeTree> scanner = new ClassScanner(compilationUnitTree, sourcePositions, usages);
                ScopeTree scopeTree = new ScopeTree();             
                compilationUnitTree.accept(scanner, scopeTree);
            }            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return usages;
    }
    
    private class ClassScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final List<Usage> usages;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Usage> usages) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.usages = usages;
        }

        @Override
        public ScopeTree visitVariable(VariableTree variableTree, ScopeTree scopeTree) {
            
            super.visitVariable(variableTree, scopeTree);
            
            String variable = variableTree.getType().toString();
            String name = variableTree.getName().toString();
//            System.out.println("variable " + variable);
//            System.out.println("name " + name);
            scopeTree.addVariable(variable, name);
            
            return scopeTree;                    
        }
        
        @Override
        public ScopeTree visitMemberSelect(MemberSelectTree memberSelectTree, ScopeTree scopeTree) {
            
            super.visitMemberSelect(memberSelectTree, scopeTree);
            
            if (methodMatch.getName().toString().equals(memberSelectTree.getIdentifier().toString())) {

                Results results = new Results();
                memberSelectTree.accept(new ExpressionScanner(), results);
                
//                for (Results.Result result : results.get()) {
//                    System.out.println("result " + result.getType() + ":" + result.getValue());
//                }
                
                String packageName = getPackageName(compilationUnitTree);
                String className = getClassName(compilationUnitTree);
                
                Usage usage = new Usage();
                usage.setPackageName(packageName + "." + className);
                usage.setClassName(className);
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

                addMethodArguments(usage, methodInvocationTree);
                break;
            }

            return scopeTree;
        }
        
        private void addMethodArguments(Usage usage, MethodInvocationTree methodInvocationTree) {
            
            List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
            for (ExpressionTree argument : arguments) {
                usage.addMethodArgument(argument.toString());
            }            
        }
    }
            
    private static class ExpressionScanner extends SimpleTreeVisitor<Results, Results> {
    
        @Override
        public Results visitIdentifier(IdentifierTree identifierTree, Results results) {
            
            results.add(Results.Result.Type.IDENTIFIER, identifierTree.toString());
            
            return results;
        }

        @Override
        public Results visitMemberSelect(MemberSelectTree memberSelectTree, Results results) {
            
            results.add(Results.Result.Type.MEMBER, memberSelectTree.getIdentifier().toString());
            
            ExpressionTree expression = memberSelectTree.getExpression();
            return expression.accept(new ExpressionScanner(), results);
        }

        @Override
        public Results visitMethodInvocation(MethodInvocationTree methodInvocationTree, Results results) {
            
            List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
            
            List<ExpressionTree> args = new ArrayList<ExpressionTree>(arguments);
            
            Collections.reverse(args);

            for (ExpressionTree arg : args) {
                results.add(Results.Result.Type.ARG, arg.toString());
            }
            
            ExpressionTree methodSelect = methodInvocationTree.getMethodSelect();
            return methodSelect.accept(new ExpressionScanner(), results);
        }
    }
    
    private static class Results {
        
        private List<Result> parts = new ArrayList<Result>();

        public List<Result> get() {
            
            Collections.reverse(parts);
            
            return parts;
        }
        
        public void add(Result.Type type, String value) {
            
            parts.add(new Result(type, value));
        }

        private static class Result {
            
            private final Type type;
            private final String value;

            public Result(Type type, String value) {
             
                this.type = type;
                this.value = value;
            }

            public Type getType() {
                
                return type;
            }

            public String getValue() {
                
                return value;
            }
            
            static enum Type {
                ARG, MEMBER, IDENTIFIER
            }
        }
    }
}
