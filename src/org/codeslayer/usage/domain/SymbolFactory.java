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

import java.util.List;

public class SymbolFactory {
    
    private final List<Symbol> symbols;

    public SymbolFactory(List<Symbol> symbols) {
    
        this.symbols = symbols;
    }
    
    public Symbol createSymbolTree() {
        
        create();
        return symbols.get(0);
    }
    
    public void create() {
        
        Symbol lastSymbol = null;
        
        for (Symbol symbol : symbols) {

            if (symbol instanceof Identifier) {
                lastSymbol = symbol;
                continue;
            }
            
            if (symbol instanceof Member) {
                Identifier identifier = (Identifier)lastSymbol;
                Member member = (Member)symbol;
                identifier.setMember(member);
                lastSymbol = member;
                continue;
            }
            
            if (symbol instanceof Arg) {
                Arg arg = (Arg)symbol;
                
                if (lastSymbol instanceof Identifier) {
                    Identifier identifier = (Identifier)lastSymbol;
                    identifier.addArg(arg);
                } else {
                    Member member = (Member)lastSymbol;
                    member.addArg(arg);
                }
            }
        }
    }    
}
