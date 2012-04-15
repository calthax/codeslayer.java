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
import org.codeslayer.source.Method;
import org.codeslayer.source.Parameter;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.ScopeTree;

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

        String type = variableTree.getType().toString();
        String variable = variableTree.getName().toString();
        scopeTree.addSimpleType(variable, type);

        return scopeTree;                    
    }

    @Override
    public ScopeTree visitMethod(MethodTree methodTree, ScopeTree scopeTree) {

        super.visitMethod(methodTree, scopeTree);

        if (methodName.equals(methodTree.getName().toString())) {
            Method method = new Method();
            method.setClassName(SourceUtils.getClassName(compilationUnitTree));
            method.setSimpleClassName(SourceUtils.getSimpleClassName(compilationUnitTree));
            method.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodTree));
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

            String simpleType = variableTree.getType().toString();
            String variable = variableTree.getName().toString();

            Parameter parameter = new Parameter();
            parameter.setVariable(variable);
            
            if (SourceUtils.isPrimative(simpleType)) {
                parameter.setPrimative(simpleType);
            } else {
                parameter.setSimpleClassName(simpleType);
                parameter.setClassName(SourceUtils.getClassName(scopeTree, simpleType));
            }

            results.add(parameter);
        }

        return results;
    }    
}
