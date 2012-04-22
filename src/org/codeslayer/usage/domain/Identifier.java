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

public class Identifier extends AbstractSymbol {
    
    private Member member;
    private List<Arg> args = new ArrayList<Arg>();

    public Identifier(String value) {

        super(value);
    }

    public Member getMember() {
        
        return member;
    }

    public void setMember(Member member) {
     
        this.member = member;
    }

    public List<Arg> getArgs() {
     
        return args;
    }

    public void addArg(Arg arg) {
     
        this.args.add(arg);
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(" value: [").append(getValue()).append("]");
        sb.append(" type: [").append(getType()).append("]");
        
        if (member != null) {
            sb.append(" member: [").append(member).append("]");            
        }
        
        sb.append(" args: [\n");
        for (Arg arg: args) {
            sb.append(arg).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }   
}
