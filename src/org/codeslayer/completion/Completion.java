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
package org.codeslayer.completion;

public class Completion {
    
    private String methodName;
    private String methodParameters;
    private String methodParameterVariables;
    private String methodReturnType;

    public String getMethodName() {
        
        return methodName;
    }

    public void setMethodName(String methodName) {
        
        this.methodName = methodName;
    }

    public String getMethodParameters() {
        
        return methodParameters;
    }

    public void setMethodParameters(String methodParameters) {
        
        this.methodParameters = methodParameters;
    }

    public String getMethodParameterVariables() {
        
        return methodParameterVariables;
    }

    public void setMethodParameterVariables(String methodParameterVariables) {
        
        this.methodParameterVariables = methodParameterVariables;
    }

    public String getMethodReturnType() {
        
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
    
        this.methodReturnType = methodReturnType;
    }
}
