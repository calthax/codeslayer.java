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

import org.codeslayer.source.Symbol;
import org.codeslayer.source.scanner.SymbolHandler;
import org.codeslayer.source.scanner.SymbolScanner;
import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.source.*;

public class ParameterScanner {
    
    private static Logger logger = Logger.getLogger(ParameterScanner.class);
    
    private final CompilationUnitTree compilationUnitTree;
    private final SourcePositions sourcePositions;
    private final HierarchyManager hierarchyManager;

    public ParameterScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, HierarchyManager hierarchyManager) {
     
        this.compilationUnitTree = compilationUnitTree;
        this.sourcePositions = sourcePositions;
        this.hierarchyManager = hierarchyManager;
    }    
    
    public List<Parameter> scan(MethodInvocationTree methodInvocationTree, ScopeTree scopeTree) {

        List<Parameter> parameters = new ArrayList<Parameter>();
        
        List<? extends ExpressionTree> expressionTrees = methodInvocationTree.getArguments();
        for (ExpressionTree expressionTree : expressionTrees) {

            Tree.Kind kind = expressionTree.getKind();
            String name = expressionTree.toString();
                    
            Parameter parameter = new Parameter();
            parameter.setVariable(name);

            try {
                if (kind == Tree.Kind.STRING_LITERAL) {
                    parameter.setSimpleType(String.class.getSimpleName());
                    parameter.setType(String.class.getName());

                    parameters.add(parameter);
                } else if (kind == Tree.Kind.NULL_LITERAL) {
                    parameters.add(parameter);
                } else if (kind == Tree.Kind.PLUS) { // todo: this needs to be much more flexible
                    parameter.setSimpleType(String.class.getSimpleName());
                    parameter.setType(String.class.getName());

                    parameters.add(parameter);
                } else if (kind == Tree.Kind.IDENTIFIER) { // items
                    String simpleType = scopeTree.getSimpleType(name);
                    if (simpleType == null) {
                        simpleType = SourceUtils.getStaticImportType(hierarchyManager, scopeTree, name);
                    }
                    String className = SourceUtils.getClassName(scopeTree, simpleType);

                    parameter.setSimpleType(simpleType);
                    parameter.setType(className);

                    parameters.add(parameter);
                } else if (kind == Tree.Kind.ARRAY_ACCESS) { // items
                    String simpleType = scopeTree.getSimpleType(SourceUtils.removeSpecialTypeCharacters(name));
                    if (simpleType == null) {
                        simpleType = SourceUtils.getStaticImportType(hierarchyManager, scopeTree, name);
                    }
                    String className = SourceUtils.getClassName(scopeTree, simpleType);

                    parameter.setSimpleType(simpleType);
                    parameter.setType(className);

                    parameters.add(parameter);
                } else if (kind == Tree.Kind.METHOD_INVOCATION || kind == Tree.Kind.MEMBER_SELECT) { // dao.getPresidents()
                    Symbol symbol = expressionTree.accept(new SymbolScanner(), null);

                    SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);
                    String type = symbolHandler.getType(symbol, scopeTree);
                    parameter.setSimpleType(SourceUtils.getSimpleType(type));
                    parameter.setType(type);

                    parameters.add(parameter);
                } else if (kind == Tree.Kind.NEW_CLASS) { // new AllItems()
                    NewClassTree newClassTree = (NewClassTree) expressionTree;
                    String simpleType = newClassTree.getIdentifier().toString();
                    String className = SourceUtils.getClassName(scopeTree, simpleType);

                    parameter.setSimpleType(simpleType);
                    parameter.setType(className);

                    parameters.add(parameter);
                } else {
                    parameter.setSimpleType(SourceUtils.UNDEFINED);
                    parameter.setType(SourceUtils.UNDEFINED);
                    parameters.add(parameter);

                    if (logger.isDebugEnabled()) {
                        logger.error("Tree.Kind '" + kind + "' not implemented for: " + SourceUtils.getClassLogInfo(compilationUnitTree, sourcePositions, expressionTree) + " - " + name);
                    }
                }
            } catch (Exception e) {
                parameter.setSimpleType(SourceUtils.UNDEFINED);
                parameter.setType(SourceUtils.UNDEFINED);
                parameters.add(parameter);
                
                if (logger.isDebugEnabled()) {
                    logger.error("type null for: " + SourceUtils.getClassLogInfo(compilationUnitTree, sourcePositions, expressionTree) + " - " + name);
                }
            }
        }
        
        return parameters;
    }
}
