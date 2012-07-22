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
package org.codeslayer.usage.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolManager {
    
    private List<Symbol> symbols = new ArrayList<Symbol>();
    
    /*
     * We need to create the args before the class or method.
     */
    private List<Arg> args = new ArrayList<Arg>();
    
    private Symbol symbolTree;

    public Symbol getSymbolTree() {
        
        if (symbolTree != null) {
            return symbolTree;
        }

        Collections.reverse(symbols);
       
        SymbolFactory symbolFactory = new SymbolFactory(symbols);
        this.symbolTree = symbolFactory.createSymbolTree();

        return this.symbolTree;
    }
    
    public NewClass addNewClass(String value) {
        
        NewClass newClass = new NewClass(value);
        newClass.setArgs(new ArrayList<Arg>(args));
        symbols.add(newClass);
        args.clear();
        return newClass;
    }
    
    public Identifier addIdentifier(String value) {
        
        Identifier identifier = new Identifier(value);
        symbols.add(identifier);
        return identifier;
    }
    
    public Member addMember(String value) {
        
        Member member = new Member(value);
        member.setArgs(new ArrayList<Arg>(args));
        symbols.add(member);
        args.clear();
        return member;
    }
    
    public void addArg(Arg arg) {
        
        args.add(arg);
    }
    
    public void removeLastSymbol() {
        
        if (symbols.isEmpty()) {
            return;
        }
        
        if (symbolTree != null) {
            throw new IllegalStateException("Not able to remove the last symbol");
        }
        
        symbols.remove(0);
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(symbolTree);
        return sb.toString();
    }   
}
