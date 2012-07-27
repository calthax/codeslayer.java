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
import java.util.List;

public class Symbol {
    
    private final String value;
    private String type;
    private Symbol nextSymbol;
    private Symbol prevSymbol;
    private List<Arg> args = new ArrayList<Arg>();

    public Symbol(String value) {
     
        this.value = value;
    }

    public String getValue() {

        return value;
    }

    public String getType() {
        
        return type;
    }

    public void setType(String type) {
    
        this.type = type;
    }
    
    public List<Arg> getArgs() {
     
        return args;
    }

    public void addArg(Arg arg) {
     
        this.args.add(arg);
    }
    
    public Symbol getPrevSymbol() {
        
        return prevSymbol;
    }

    public void setPrevSymbol(Symbol prevSymbol) {
     
        this.prevSymbol = prevSymbol;
    }

    public Symbol getNextSymbol() {
        
        return nextSymbol;
    }

    public void setNextSymbol(Symbol nextSymbol) {
     
        this.nextSymbol = nextSymbol;
        this.nextSymbol.setPrevSymbol(this);
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("[value=").append(getValue());
        sb.append(", type=").append(getType());        
        sb.append(", args={");
        for (Arg arg: args) {
            sb.append(arg).append(",");
        }
        sb.append("}");
        return sb.append("]").toString();
    }   
}
