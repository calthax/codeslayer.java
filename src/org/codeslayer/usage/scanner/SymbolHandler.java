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
import java.util.List;
import org.codeslayer.source.*;
import org.codeslayer.usage.UsageUtils;
import org.codeslayer.usage.domain.*;

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
                System.out.println("Not able to get the type for symbol " + symbol);
                return null;
            }
            
            symbol.setType(className);
            
            System.out.println("SymbolHandler.getType() " + symbol);
        
            if (nextSymbol != null) {
                return getType(nextSymbol, scopeTree);                
            }
        } else if (prevSymbol != null) {
            
            Method method = createMethod(symbol, scopeTree);
            String returnType = getReturnType(method);
            
            if (returnType == null) {
                System.out.println("Not able to get the return type for symbol " + symbol);
                return null;
            }
            
            symbol.setType(returnType);

            System.out.println("SymbolHandler.getType() " + symbol);

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
            Klass klass = new Klass();
            klass.setClassName(SourceUtils.getClassName(compilationUnitTree));
            method.setKlass(klass);
            return getReturnType(method);
        } else {
            String className = SourceUtils.getClassName(scopeTree, simpleType);                   
//            System.out.println("param identifier => " + simpleType);
            return className;
        }
    }
    
    private String getReturnType(Method method) {
        
        for (Hierarchy hierarchy : hierarchyManager.getHierarchyList(method.getKlass().getClassName())) {
            List<Method> classMethods = UsageUtils.getClassMethodsByName(hierarchy.getFilePath(), method.getName());
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
        
        Method method = new Method();
        method.setName(symbol.getValue());
        
        Klass klass = new Klass();
        klass.setClassName(prevSymbol.getType());
        method.setKlass(klass);
        
        for (Arg arg : symbol.getArgs()) {
            Parameter parameter = new Parameter();
            parameter.setType(getType(arg.getSymbol(), scopeTree));
            method.addParameter(parameter);
        }
        
        return method;
    }
}
