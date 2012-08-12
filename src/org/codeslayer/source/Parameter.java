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
package org.codeslayer.source;

public class Parameter {
    
    private String simpleType;
    private String type;
    private String variable;

    public String getSimpleType() {
        
        return simpleType;
    }

    public void setSimpleType(String simpleType) {
        
        this.simpleType = simpleType;
    }

    public String getType() {
        
        return type;
    }

    public void setType(String type) {
     
        this.type = type;
    }

    public String getVariable() {
        
        return variable;
    }

    public void setVariable(String variable) {
        
        this.variable = variable;
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[type=").append(type);
        sb.append(", simpleType=").append(simpleType);
        sb.append(", variable=").append(variable);
        return sb.append("]").toString();
    }    
}
