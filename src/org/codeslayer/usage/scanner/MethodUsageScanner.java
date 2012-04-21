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
import java.util.List;
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
                
                List<Parameter> parameters = new ArrayList<Parameter>();
                ParameterScanner parameterScanner = new ParameterScanner(compilationUnitTree, sourcePositions, input, parameters);
                parameterScanner.scan(methodInvocationTree, scopeTree);
                
                for (Parameter parameter : parameters) {
                    usage.addMethodParameter(parameter);
                }
            }

            return scopeTree;
        }
    }
}
