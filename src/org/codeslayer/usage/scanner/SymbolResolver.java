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
import static org.codeslayer.usage.domain.SymbolType.*;

public class SymbolResolver {
    
    private final CompilationUnitTree compilationUnitTree;
    private final HierarchyManager hierarchyManager;

    public SymbolResolver(CompilationUnitTree compilationUnitTree, HierarchyManager hierarchyManager) {
     
        this.compilationUnitTree = compilationUnitTree;
        this.hierarchyManager = hierarchyManager;
    }    
    
    public String getType(Symbol symbol, ScopeTree scopeTree) {

        return resolveType(null, symbol, scopeTree);
    }
    
    private String resolveType(Symbol parent, Symbol child, ScopeTree scopeTree) {
        
        if (child.getSymbolType() == NEW_CLASS) {
            System.out.println("symbol resolve NewClass => " + child.getValue());
            
            for (Arg arg : child.getArgs()) {
                System.out.println("symbol resolve Arg => " + arg.toString());
            }

            String className = getClassName(parent, child, scopeTree);
            child.setType(className);
            System.out.println("symbol resolve NewClass className => " + child.getType());
            
            return child.getType();
        } else if (child.getSymbolType() == IDENTIFIER) {
            System.out.println("symbol resolve Identifier => " + child.getValue());
            
            for (Arg arg : child.getArgs()) {
                System.out.println("symbol resolve Arg => " + arg.toString());
            }
            
            String className = getClassName(parent, child, scopeTree);
            child.setType(className);
            System.out.println("symbol resolve Identifier className => " + child.getType());
            
            Symbol member = child.getNextSymbol();
            if (member != null) {
                return resolveType(child, member, scopeTree);
            }
            
            return child.getType();
        } else if (child.getSymbolType() == MEMBER) {
            System.out.println("symbol resolve Member => " + child.getValue());
            
            for (Arg arg : child.getArgs()) {
                System.out.println("symbol resolve Arg => " + arg.toString());
            }
            
            Method method = createMethod(parent, child);
            String returnType = getReturnType(method);
            child.setType(returnType);
            System.out.println("symbol resolve Member className => " + child.getType());
            
            Symbol member = child.getNextSymbol();
            if (member != null) {
                return resolveType(child, member, scopeTree);
            }
            
            return child.getType();
        }

        throw new IllegalStateException("Not able to find the class name for " + child);
    }
    
    private String getClassName(Symbol parent, Symbol child, ScopeTree scopeTree) {
       
        String childValue = child.getValue();
        
        if (childValue.equals("this")) {
            return SourceUtils.getClassName(compilationUnitTree);
        } 
        
        if (SourceUtils.isClass(childValue)) {
            String className = SourceUtils.getClassName(scopeTree, childValue);
            if (className == null) {
                throw new IllegalStateException("Not able to find the parameter identifier " + childValue);
            }        
            return className;
        }
        
        String childSimpleType = scopeTree.getSimpleType(childValue);
        if (childSimpleType == null) { // assume this is a method of this class
            Method method = new Method();
            method.setName(childValue);
            Klass klass = new Klass();
            klass.setClassName(SourceUtils.getClassName(compilationUnitTree));
            method.setKlass(klass);
            return getReturnType(method);
        } else {
            String className = SourceUtils.getClassName(scopeTree, childSimpleType);                   
//            System.out.println("param identifier => " + simpleType);
            return className;
        }
    }
    
    private String getReturnType(Method method) {
        
        for (Hierarchy hierarchy : hierarchyManager.getHierarchyList(method.getKlass().getClassName())) {
            List<Method> classMethods = UsageUtils.getClassMethodsByName(hierarchy.getFilePath(), method.getName());
            for (Method classMethod : classMethods) {
                if (SourceUtils.methodsEqual(classMethod, method)) {
                    return classMethod.getReturnType();
                }
            }
        }
        
        return null;
    }
    
    private Method createMethod(Symbol parent, Symbol child) {
        
        Method method = new Method();
        method.setName(child.getValue());
        
        Klass klass = new Klass();
        klass.setClassName(parent.getType());
        method.setKlass(klass);
        
        for (Arg arg : child.getArgs()) {
            Parameter parameter = new Parameter();
            parameter.setType(arg.getSymbol().getType());
            method.addParameter(parameter);
        }
        
        return method;
    }
}
