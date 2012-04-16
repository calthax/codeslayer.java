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

import java.util.ArrayList;
import java.util.List;

public class Method {
    
    private String name;
    private String modifier;
    private List<Parameter> parameters = new ArrayList<Parameter>();
    private String simpleReturnType;
    private String returnType;
    private String className;
    private String simpleClassName;
    private int lineNumber;

    public String getName() {
        
        return name;
    }

    public void setName(String name) {
     
        this.name = name;
    }

    public String getModifier() {
        
        return modifier;
    }

    public void setModifier(String modifier) {
     
        this.modifier = modifier;
    }
    
    public List<Parameter> getParameters() {
        
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
     
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter) {
     
        this.parameters.add(parameter);
    }

    public String getSimpleReturnType() {
        
        return simpleReturnType;
    }

    public void setSimpleReturnType(String simpleReturnType) {
     
        this.simpleReturnType = simpleReturnType;
    }

    public String getReturnType() {
        
        return returnType;
    }

    public void setReturnType(String returnType) {
     
        this.returnType = returnType;
    }
    
    public String getClassName() {
        
        return className;
    }

    public void setClassName(String className) {
     
        this.className = className;
    }

    public String getSimpleClassName() {
        
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
     
        this.simpleClassName = simpleClassName;
    }

    public int getLineNumber() {
        
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
     
        this.lineNumber = lineNumber;
    }
}
