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
    
    private String simpleClassName;
    private String className;
    private String primative;
    private String variable;

    public String getSimpleClassName() {
        
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        
        this.simpleClassName = simpleClassName;
    }

    public String getClassName() {
        
        return className;
    }

    public void setClassName(String className) {
     
        this.className = className;
    }

    public String getPrimative() {
        
        return primative;
    }

    public void setPrimative(String primative) {
     
        this.primative = primative;
    }

    public String getVariable() {
        
        return variable;
    }

    public void setVariable(String variable) {
        
        this.variable = variable;
    }
    
    public String getSimpleType() {
        
        if (primative != null) {
            return primative;
        }
        
        return simpleClassName;
    }
    
    public String getType() {
        
        if (primative != null) {
            return primative;
        }
        
        return className;
    }
}
