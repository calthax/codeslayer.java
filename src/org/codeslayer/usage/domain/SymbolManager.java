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

public class SymbolManager {
    
    private Symbol symbol;
    private Symbol lastSymbol;

    public Symbol getSymbol() {

        return symbol;
    }

    public void addIdentifier(String value) {
        
        Identifier identifier = new Identifier(value);

        if (symbol == null) {
            symbol = identifier;
        }

        lastSymbol = identifier;
    }
    
    public void addMember(String value) {
        
        Member member = new Member(value);
        
        if (lastSymbol instanceof Identifier) {
            ((Identifier)lastSymbol).setMember(member);
        }

        lastSymbol = member;
    }
    
    public void addArg(String value) {
        
        Arg arg = new Arg(value);
        
        if (lastSymbol instanceof Identifier) {
            ((Identifier)lastSymbol).addArg(arg);
        } else if (lastSymbol instanceof Member) {
            ((Member)lastSymbol).addArg(arg);
        }

        lastSymbol = arg;
    }
    
//    public void removeLastSymbol() {
//        
//        Identifier identifier = (Identifier)symbol;
//        Member member = identifier.getMember();
//        
//        if (member == null) {
//            symbol = null;
//            return;
//        }
//        
//        Identifier lastParent = (Identifier)findLastParent(identifier, member);
//        lastParent.setMember(null);
//    }
//    
//    private Symbol findLastParent(Symbol parent, Symbol child) {
//        
//        if (child == null) {
//            return parent;
//        }
//
//        if (parent != null && parent instanceof Identifier) {
//            Identifier identifier = (Identifier)parent;
//            Member member = identifier.getMember();
//            return findLastParent(identifier, member);
//        }
//        
//        throw new IllegalStateException("Not able to remove the last symbol");
//    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("symbolManager: ").append(symbol);
        return sb.toString();
    }   
}
