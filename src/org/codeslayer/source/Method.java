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
import java.util.Collections;
import java.util.List;

public class Method {
    
    private String name;
    private String modifier;
    private List<Parameter> parameters;
    private String simpleReturnType;
    private String returnType;
    private int lineNumber;
    private Klass klass;

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
        
        if (parameters == null) {
            return Collections.emptyList();
        }
        
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
     
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter) {
        
        if (parameters == null) {
            this.parameters = new ArrayList<Parameter>();
        }
     
        this.parameters.add(parameter);
    }

    public String getReturnType() {
        
        return returnType;
    }

    public void setReturnType(String returnType) {
     
        this.returnType = returnType;
    }
    
    public String getSimpleReturnType() {
        
        return simpleReturnType;
    }

    public void setSimpleReturnType(String simpleReturnType) {
     
        this.simpleReturnType = simpleReturnType;
    }

    public Klass getKlass() {
        
        return klass;
    }

    public void setKlass(Klass klass) {
    
        this.klass = klass;
    }
    
    public int getLineNumber() {
        
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
     
        this.lineNumber = lineNumber;
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(" name: [").append(name).append("]");
        sb.append(" parameters: [\n");
        for (Parameter parameter: getParameters()) {
            sb.append(parameter.getType()).append("\n");
        }
        sb.append("]");
        sb.append(" modifier: [").append(modifier).append("]");
        sb.append(" returnType: [").append(returnType).append("]");        
        sb.append(" simpleReturnType: [").append(simpleReturnType).append("]");        
        sb.append(" lineNumber: [").append(lineNumber).append("]");        
        return sb.toString();
    }    
}
