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

        Symbol symbol = symbolManager.getSymbolTree();
        return resolveType(null, symbol, scopeTree);
    }
    
    private String resolveType(Symbol parent, Symbol child, ScopeTree scopeTree) {
        
        if (child instanceof Identifier) {
            Identifier identifier = (Identifier)child;
            System.out.println("symbol identifier => " + identifier.getValue());
            
            for (Arg arg : identifier.getArgs()) {
                System.out.println("symbol arg => " + arg.getValue());
            }
            
            String className = getClassName(parent, identifier, scopeTree);
            identifier.setType(className);
            System.out.println("identifier type => " + identifier.getType());
            
            Member member = identifier.getMember();
            if (member != null) {
                return resolveType(identifier, member, scopeTree);
            }
            
            return identifier.getType();
        } else if (child instanceof Member) {
            Member member = (Member)child;
            System.out.println("symbol member => " + member.getValue());
            
            for (Arg arg : member.getArgs()) {
                System.out.println("symbol arg => " + arg.getValue());
            }
            
            Identifier identifier = (Identifier)parent;
            Method method = createMethod(identifier, member);
            String returnType = getClassReturnType(method, scopeTree);
            member.setType(returnType);
            System.out.println("member type => " + member.getType());
            
            return member.getType();
        }

        throw new IllegalStateException("Not able to find the type for " + child);
    }
    
    private Method createMethod(Identifier identifier, Member member) {
        
        Method method = new Method();
        method.setName(member.getValue());
        
        method.setClassName(identifier.getType());
        
        for (Arg arg : member.getArgs()) {
            Parameter parameter = new Parameter();
            parameter.setType(arg.getType());
            method.addParameter(parameter);
        }
        
        return method;
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
            method.setClassName(SourceUtils.getClassName(compilationUnitTree));
            return getClassReturnType(method, scopeTree);
        } else {
            String className = SourceUtils.getClassName(scopeTree, childSimpleType);                   
//            System.out.println("param identifier => " + simpleType);
            return className;
        }
    }
    
    private String getClassReturnType(Method method, ScopeTree scopeTree) {
        
        Klass klass  = IndexerUtils.getIndexClass(input.getIndexesFile(), method.getClassName());
        if (klass == null) {
            System.out.println("Not able to find class " + method.getClassName() + ". This may be ok if no source is available");
            return null;
        }
        
        Method klassMethod = SourceUtils.findClassMethod(klass, method);
        String returnType = klassMethod.getSimpleReturnType();

//        System.out.println("param return type => " + returnType);

        return SourceUtils.getClassName(scopeTree, returnType);
    }
}
