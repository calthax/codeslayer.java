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
import java.util.ArrayList;
import java.util.List;
import org.codeslayer.source.*;
import org.codeslayer.usage.domain.*;

public class ParameterScanner {
    
    private final CompilationUnitTree compilationUnitTree;
    private final HierarchyManager hierarchyManager;
    private final List<Parameter> parameterMatches = new ArrayList<Parameter>();

    public ParameterScanner(CompilationUnitTree compilationUnitTree, HierarchyManager hierarchyManager) {
     
        this.compilationUnitTree = compilationUnitTree;
        this.hierarchyManager = hierarchyManager;
    }    
    
    public void scan(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {
        
        List<? extends ExpressionTree> expressionTrees = methodInvocationTree.getArguments();
        for (ExpressionTree expressionTree : expressionTrees) {

            Tree.Kind kind = expressionTree.getKind();
            String name = expressionTree.toString();

            if (kind == Tree.Kind.STRING_LITERAL) {
                Parameter parameter = new Parameter();
                
                parameter.setSimpleType(String.class.getSimpleName());
                parameter.setType(String.class.getName());

                parameterMatches.add(parameter);
            } else if (kind == Tree.Kind.IDENTIFIER) { // items
                Parameter parameter = new Parameter();

                String simpleType = scopeTree.getSimpleType(name);
                if (simpleType == null) {
                    simpleType = SourceUtils.getStaticImportType(hierarchyManager, scopeTree, name);
                }
                String className = SourceUtils.getClassName(scopeTree, simpleType);

                parameter.setSimpleType(simpleType);
                parameter.setType(className);

                parameterMatches.add(parameter);
            } else if (kind == Tree.Kind.METHOD_INVOCATION) { // dao.getPresidents()
                Parameter parameter = new Parameter();
                
                Symbol symbol = expressionTree.accept(new SymbolScanner(), null);

                SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);
                String type = symbolHandler.getType(symbol, scopeTree);
                parameter.setSimpleType(SourceUtils.getSimpleType(type));
                parameter.setType(type);

                parameterMatches.add(parameter);
            } else if (kind == Tree.Kind.NEW_CLASS) { // new AllItems()
                Parameter parameter = new Parameter();

                NewClassTree newClassTree = (NewClassTree) expressionTree;
                String simpleType = newClassTree.getIdentifier().toString();
                String className = SourceUtils.getClassName(scopeTree, simpleType);

                parameter.setSimpleType(simpleType);
                parameter.setType(className);

                parameterMatches.add(parameter);
            }
        }
    }
    
    public List<Parameter> getScanResults() {
        
        return parameterMatches;
    }
}
