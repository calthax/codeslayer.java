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

import java.util.List;
import java.io.File;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.source.tree.*;
import org.apache.log4j.Logger;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.Parameter;
import org.codeslayer.source.*;
import org.codeslayer.usage.UsageUtils;
import org.codeslayer.usage.domain.*;

public class MethodUsageScanner {
    
    private static Logger logger = Logger.getLogger(MethodUsageScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final Method methodMatch;
    private final Input input;

    public MethodUsageScanner(HierarchyManager hierarchyManager, Method methodMatch, Input input) {
    
        this.hierarchyManager = hierarchyManager;
        this.methodMatch = methodMatch;
        this.input = input;
    }
    
    public List<Usage> scan() 
            throws Exception {
        
        UsageManager usageManager = new UsageManager();
        
        try {
            JavacTask javacTask = SourceUtils.getJavacTask(input.getSourceFolders());
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new InternalScanner(compilationUnitTree, sourcePositions, hierarchyManager, usageManager);
                ScopeTreeFactory scopeTreeFactory = new ScopeTreeFactory(compilationUnitTree);
                ScopeTree scopeTree = scopeTreeFactory.createScopeTree();
                compilationUnitTree.accept(scanner, scopeTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return usageManager.getUsages();
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final HierarchyManager hierarchyManager;
        private final UsageManager usageManager;
        private final File sourceFile;

        public InternalScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, HierarchyManager hierarchyManager, UsageManager usageManager) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.hierarchyManager = hierarchyManager;
            this.usageManager = usageManager;
            this.sourceFile = SourceUtils.getSourceFile(compilationUnitTree);
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

            if (methodMatch.getName().toString().equals(identifierTree.getName().toString())) {
                
//                if (!SourceUtils.getClassName(compilationUnitTree).equals("org.jmesa.view.html.AbstractHtmlView")) {
//                    return null;
//                }
                
                if (logger.isDebugEnabled()) {
                    logger.debug("** scan class (identifier)" + SourceUtils.getClassLogInfo(compilationUnitTree, sourcePositions, identifierTree) + " **");
                }
                
                Method staticMethod = SourceUtils.getStaticMethod(scopeTree, methodMatch.getName());
                if (staticMethod != null) {
                    usageManager.addUsage(createUsage(staticMethod, identifierTree));
                } else {
                    // assume this is a method of this class
                    
                    String className = SourceUtils.getClassName(compilationUnitTree);

                    if (!SourceUtils.hasMethodMatch(hierarchyManager, methodMatch, className)) {
                        return scopeTree;
                    }

                    Method method = new Method();
                    method.setName(methodMatch.getName());
                    Klass klass = new Klass();
                    klass.setClassName(className);
                    klass.setSimpleClassName(SourceUtils.getSimpleType(className));
                    method.setKlass(klass);

                    usageManager.addUsage(createUsage(method, identifierTree));
                }
            }            
            
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
                
//                if (!SourceUtils.getClassName(compilationUnitTree).equals("org.jmesa.view.html.AbstractHtmlView")) {
//                    return null;
//                }
                
                if (logger.isDebugEnabled()) {
                    logger.debug("** scan class " + SourceUtils.getClassLogInfo(compilationUnitTree, sourcePositions, memberSelectTree) + " **");
                }
                
                //memberSelectTree.getExpression().accept(new DebugScanner(), null);
                
                Symbol symbol = memberSelectTree.getExpression().accept(new SymbolScanner(), null);
                if (symbol == null) {
                    if (logger.isDebugEnabled()) {
                        logger.error("symbol is null");
                    }
                    return scopeTree;
                }
                
                Symbol firstSymbol = UsageUtils.findFirstSymbol(symbol);
                
                SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);                
                
                String type = symbolHandler.getType(firstSymbol, scopeTree);
                if (type == null) {
                    return scopeTree;
                }

                if (!SourceUtils.hasMethodMatch(hierarchyManager, methodMatch, type)) {
                    return scopeTree;
                }
                
                Method method = new Method();
                method.setName(methodMatch.getName());
                Klass klass = new Klass();
                klass.setClassName(type);
                klass.setSimpleClassName(SourceUtils.getSimpleType(type));
                method.setKlass(klass);
                
                usageManager.addUsage(createUsage(method, memberSelectTree));
            }

            return scopeTree;
        }
        
        private Usage createUsage(Method method, Tree tree) {
            
            Usage usage = new Usage();
            usage.setMethod(method);
            usage.setClassName(SourceUtils.getClassName(compilationUnitTree));
            usage.setSimpleClassName(SourceUtils.getSimpleClassName(compilationUnitTree));
            usage.setFile(new File(compilationUnitTree.getSourceFile().toUri().toString()));                
            usage.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, tree));
            usage.setStartPosition(SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, tree));
            usage.setEndPosition(SourceUtils.getEndPosition(compilationUnitTree, sourcePositions, tree));
            return usage;
        }
        
        /**
         * At this point we have the method usages figured out, but now we need the method parameters.
         */
        @Override
        public ScopeTree visitMethodInvocation(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {

            super.visitMethodInvocation(methodInvocationTree, scopeTree);

            int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodInvocationTree);
            int startPosition = SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, methodInvocationTree);

            for (Usage usage : usageManager.getUsages()) {
                if (lineNumber != usage.getLineNumber() || startPosition != usage.getStartPosition()) {
                    continue;
                }
                
                if (!sourceFile.equals(usage.getFile())) {
                    continue;
                }
                
                ParameterScanner parameterScanner = new ParameterScanner(compilationUnitTree, sourcePositions, hierarchyManager);
                parameterScanner.scan(methodInvocationTree, scopeTree);
                List<Parameter> parameters = parameterScanner.getScanResults();
                         
                for (Parameter parameter : parameters) {
                    usage.getMethod().addParameter(parameter);
                }
            }

            return scopeTree;
        }
    }
}
