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
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import java.util.ArrayList;
import java.util.List;
import org.codeslayer.source.*;

public class MethodScanner extends TreeScanner<ScopeTree, ScopeTree> {
    
    private final CompilationUnitTree compilationUnitTree;
    private final SourcePositions sourcePositions;
    private final String methodName;
    private final List<Method> methodMatches = new ArrayList<Method>();

    public MethodScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, String methodName) {

        this.compilationUnitTree = compilationUnitTree;
        this.sourcePositions = sourcePositions;
        this.methodName = methodName;
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
    public ScopeTree visitClass(ClassTree classTree, ScopeTree scopeTree) {
        
        super.visitClass(classTree, scopeTree);

        List<? extends Tree> members = classTree.getMembers();

//        Klass klass = new Klass();
//        klass.setImports(SourceUtils.getImports(compilationUnitTree));
//        klass.setSimpleClassName(simpleClassName);
//        klass.setClassName(className);
//        klass.setFilePath(SourceUtils.getSourceFilePath(compilationUnitTree));
//        klass.setSuperClass(SourceUtils.getSuperClass(classTree, scopeTree));
//        klass.setInterfaces(SourceUtils.getInterfaces(classTree, scopeTree));

        for (Tree memberTree : members) {
            if (memberTree instanceof MethodTree) {
                MethodTree methodTree = (MethodTree)memberTree;

                if (methodName.equals(methodTree.getName().toString())) {
                    Method method = new Method();
                    method.setClassName(SourceUtils.getClassName(compilationUnitTree));
                    method.setSimpleClassName(SourceUtils.getSimpleClassName(compilationUnitTree));
                    method.setSuperClass(SourceUtils.getSuperClass(classTree, scopeTree));
                    method.setInterfaces(SourceUtils.getInterfaces(classTree, scopeTree));
                    
                    method.setLineNumber(SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, methodTree));
                    method.setName(methodTree.getName().toString());
                    method.setParameters(SourceUtils.getParameters(methodTree, scopeTree));

                    String simpleReturnType = methodTree.getReturnType().toString();
                    method.setReturnType(SourceUtils.getClassName(scopeTree, simpleReturnType));
                    method.setSimpleReturnType(simpleReturnType);

                    methodMatches.add(method);
                }
            }
        }

        return scopeTree;
    }
    
    public List<Method> getScanResults() {
        
        return methodMatches;
    }
}
