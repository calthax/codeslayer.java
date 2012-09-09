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
package org.codeslayer.source.scanner;

import org.codeslayer.source.Arg;
import org.codeslayer.source.Symbol;
import com.sun.source.tree.CompilationUnitTree;
import java.util.List;
import org.codeslayer.source.*;

public class SymbolHandler {
    
    private final CompilationUnitTree compilationUnitTree;
    private final HierarchyManager hierarchyManager;

    public SymbolHandler(CompilationUnitTree compilationUnitTree, HierarchyManager hierarchyManager) {
     
        this.compilationUnitTree = compilationUnitTree;
        this.hierarchyManager = hierarchyManager;
    }    
    
    /**
     * Try to resolve  the following type of statement.
     * 
     * html.span().styleClass(getStyleClass()).style(getStyle()).title(getTooltip()).alt(getAlt()).close();
     */
    public String getType(Symbol symbol, ScopeTree scopeTree) {

        Symbol prevSymbol = symbol.getPrevSymbol();
        Symbol nextSymbol = symbol.getNextSymbol();
        
        if (prevSymbol == null) {
            
            String className = getClassName(symbol, scopeTree);
            
            if (className == null) {                
                return null;
            }
            
            symbol.setType(className);
            
            if (nextSymbol != null) {
                return getType(nextSymbol, scopeTree);                
            }
        } else if (prevSymbol != null) {
            
            Method method = createMethod(symbol, scopeTree);
            String returnType = getReturnType(method);
            
            if (returnType == null) {
                returnType = SourceUtils.getClassVariableType(hierarchyManager, symbol.getPrevSymbol().getType(), symbol.getValue());
            }
            
            if (returnType == null) {
                return null;
            }
            
            symbol.setType(returnType);

            if (nextSymbol != null) {
                return getType(nextSymbol, scopeTree);                
            }
        }
        
        return symbol.getType();
    }
    
    private String getClassName(Symbol symbol, ScopeTree scopeTree) {

        String value = symbol.getValue();
        
        if (value.equals("this")) {
            return SourceUtils.getClassName(compilationUnitTree);
        } else if (value.equals("super")) {
            String className = SourceUtils.getClassName(compilationUnitTree);
            Hierarchy hierarchy = hierarchyManager.getHierarchy(className);
            return hierarchy.getSuperClass();
        }
        
        if (SourceUtils.isClass(value)) {
            String className = SourceUtils.getClassName(scopeTree, value);
            if (className == null) {
                throw new IllegalStateException("Not able to find the parameter identifier " + value);
            }
            return className;
        }
        
        Method staticMethod = SourceUtils.getStaticMethod(scopeTree, value);
        if (staticMethod != null) {
            return getReturnType(staticMethod);
        }
        
        String simpleType = scopeTree.getSimpleType(value);
        if (simpleType == null) { // assume this is a method of this class
            Method method = new Method();
            method.setName(value);
            Clazz clazz = new Clazz();
            clazz.setClassName(SourceUtils.getClassName(compilationUnitTree));
            clazz.addMethod(method);
            return getReturnType(method);
        } else {
            return SourceUtils.getClassName(scopeTree, simpleType);
        }
    }
    
    private String getReturnType(Method method) {
        
        for (Hierarchy hierarchy : hierarchyManager.getHierarchyList(method.getClazz().getClassName())) {
            List<Method> classMethods = SourceUtils.getClassMethodsByName(hierarchy.getFilePath(), method.getName());
            for (Method classMethod : classMethods) {
                if (SourceUtils.methodsEqual(hierarchyManager, classMethod, method)) {
                    return classMethod.getReturnType();
                }
            }
        }
        
        return null;
    }
    
    private Method createMethod(Symbol symbol, ScopeTree scopeTree) {
        
        Symbol prevSymbol = symbol.getPrevSymbol();
        if (prevSymbol.getType() == null) {
            String className = getClassName(prevSymbol, scopeTree);
            prevSymbol.setType(className);
        }
        
        Method method = new Method();
        method.setName(symbol.getValue());
        
        Clazz clazz = new Clazz();
        clazz.setClassName(prevSymbol.getType());
        method.setClazz(clazz);
        
        for (Arg arg : symbol.getArgs()) {
            if (arg.getSymbol() == null) {
                continue;
            }
            
            Parameter parameter = new Parameter();
            parameter.setType(getType(arg.getSymbol(), scopeTree));
            method.addParameter(parameter);
        }
        
        return method;
    }
}
