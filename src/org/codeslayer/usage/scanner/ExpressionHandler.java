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
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.source.*;
import org.codeslayer.usage.domain.*;

public class ExpressionHandler {
    
    private final CompilationUnitTree compilationUnitTree;
    private final SourcePositions sourcePositions;
    private final Input input;

    public ExpressionHandler(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Input input) {
     
        this.compilationUnitTree = compilationUnitTree;
        this.sourcePositions = sourcePositions;
        this.input = input;
    }    
    
    public String getType(SymbolManager symbolManager, ScopeTree scopeTree) {

        Method method = new Method();

        for (Symbol symbol : symbolManager.getSymbols()) {
            switch (symbol.getSymbolType()) {
                case IDENTIFIER:
                    String className = getIdentifierClassName(symbol, scopeTree);
                    System.out.println("param identifier => " + className);
                    method.setClassName(className);
                    break;
                case MEMBER:
                    System.out.println("param member => " + symbol.getValue());
                    method.setName(symbol.getValue());                    
                    break;
                case ARG:
                    System.out.println("param arg => " + symbol.getValue());
                    Parameter parameter = new Parameter();
                    parameter.setVariable(symbol.getValue());
                    method.addParameter(parameter);
                    break;
            }
        }
        
        if (method.getName() == null) {
            return method.getClassName();
        }

        return getClassReturnType(method, scopeTree);
    }
    
    private String getIdentifierClassName(Symbol symbol, ScopeTree scopeTree) {
       
        String symbolValue = symbol.getValue();
        
        if (symbolValue.equals("this")) {
            return SourceUtils.getClassName(compilationUnitTree);
        } 
        
        if (SourceUtils.isClass(symbolValue)) {
            String className = SourceUtils.getClassName(scopeTree, symbolValue);
            if (className == null) {
                throw new IllegalStateException("not able to find the parameter identifier " + symbolValue);
            }        
            return className;
        }
        
        String simpleType = scopeTree.getSimpleType(symbolValue);
        if (simpleType == null) { // assume this is a method of this class
            Method method = new Method();
            method.setName(symbolValue);
            method.setClassName(SourceUtils.getClassName(compilationUnitTree));
            return getClassReturnType(method, scopeTree);
        } else {
            String className = SourceUtils.getClassName(scopeTree, simpleType);                   
            System.out.println("param identifier => " + simpleType);
            return className;
        }
    }
    
    private String getClassReturnType(Method method, ScopeTree scopeTree) {
        
        Klass klass  = IndexerUtils.getIndexClass(input.getIndexesFile(), method.getClassName());
        if (klass == null) {
            System.out.println("Not able to find class " + method.getClassName() + ". This may be ok if no source is available.");
            return null;
        }
        
        Method klassMethod = SourceUtils.findClassMethod(klass, method);
        String returnType = klassMethod.getSimpleReturnType();

        System.out.println("param return type => " + returnType);

        return SourceUtils.getClassName(scopeTree, returnType);
    }
}
