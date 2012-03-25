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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codeslayer.usage.domain.Method;
import org.codeslayer.usage.domain.Parameter;
import org.codeslayer.usage.domain.ScannerUtils;
import org.codeslayer.usage.domain.ScopeTree;

public class MethodScanner extends TreeScanner<ScopeTree, ScopeTree> {
    
    private final CompilationUnitTree compilationUnitTree;
    private final SourcePositions sourcePositions;
    private final String methodName;
    private final List<Method> methodMatches;

    public MethodScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, String methodName, List<Method> methodMatches) {

        this.compilationUnitTree = compilationUnitTree;
        this.sourcePositions = sourcePositions;
        this.methodName = methodName;
        this.methodMatches = methodMatches;
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

        String variable = variableTree.getType().toString();
        String name = variableTree.getName().toString();
        scopeTree.addVariable(variable, name);

        return scopeTree;                    
    }

    @Override
    public ScopeTree visitMethod(MethodTree methodTree, ScopeTree scopeTree) {

        super.visitMethod(methodTree, scopeTree);

        if (methodName.equals(methodTree.getName().toString())) {
            String packageName = ScannerUtils.getPackageName(compilationUnitTree);
            String simpleClassName = ScannerUtils.getSimpleClassName(compilationUnitTree);

            Method method = new Method();
            method.setClassName(packageName + "." + simpleClassName);
            method.setSimpleClassName(simpleClassName);
            method.setLineNumber(ScannerUtils.getLineNumber(compilationUnitTree, sourcePositions, methodTree));
            method.setName(methodTree.getName().toString());
            method.setParameters(getParameters(methodTree, scopeTree));
            method.setReturnType(methodTree.getReturnType().toString());
            
            methodMatches.add(method);
        }

        return scopeTree;
    }

    private List<Parameter> getParameters(MethodTree methodTree, ScopeTree scopeTree) {

        List<Parameter> results = new ArrayList<Parameter>(); 

        Iterator<? extends VariableTree> iterator = methodTree.getParameters().iterator();
        while (iterator.hasNext()) {
            VariableTree variableTree = iterator.next();

            String type = variableTree.getType().toString();
            String name = variableTree.getName().toString();
            String className = scopeTree.getClassName(type);

            Parameter parameter = new Parameter();
            parameter.setType(type);
            parameter.setName(name);
            parameter.setClassName(className);

            results.add(parameter);
        }

        return results;
    }    
}
