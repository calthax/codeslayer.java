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
package org.codeslayer.indexer.domain;

public class IndexMethod {
    
    private String name;
    private String modifier;
    private String parameters;
    private String parametersVariables;
    private String parametersTypes;
    private String returnType;
    private String lineNumber;

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

    public String getParameters() {
        
        return parameters;
    }

    public void setParameters(String parameters) {
        
        this.parameters = parameters;
    }

    public String getParametersVariables() {
        
        return parametersVariables;
    }

    public void setParametersVariables(String parametersVariables) {
        
        this.parametersVariables = parametersVariables;
    }

    public String getParametersTypes() {
        
        return parametersTypes;
    }

    public void setParametersTypes(String parametersTypes) {
     
        this.parametersTypes = parametersTypes;
    }

    public String getLineNumber() {
        
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        
        this.lineNumber = lineNumber;
    }

    public String getReturnType() {
        
        return returnType;
    }

    public void setReturnType(String returnType) {
        
        this.returnType = returnType;
    }
}
