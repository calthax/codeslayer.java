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
package org.codeslayer.indexer;

public class Index {

    private String className;
    private String simpleClassName;
    private String superClass;
    private String interfaces;
    private String methodName;
    private String methodModifier;
    private String methodParameters;
    private String methodParametersVariables;
    private String methodParametersTypes;
    private String methodReturnType;
    private String methodSimpleReturnType;
    private String filePath;
    private int lineNumber;

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
    
    public String getSuperClass() {
        
        return superClass;
    }

    public void setSuperClass(String superClass) {
     
        this.superClass = superClass;
    }

    public String getInterfaces() {
        
        return interfaces;
    }

    public void setInterfaces(String interfaces) {
     
        this.interfaces = interfaces;
    }
    
    public String getMethodName() {

        return methodName;
    }

    public void setMethodName(String methodName) {

        this.methodName = methodName;
    }

    public String getMethodModifier() {

        return methodModifier;
    }

    public void setMethodModifier(String methodModifier) {

        this.methodModifier = methodModifier;
    }

    public String getMethodParameters() {

        return methodParameters;
    }

    public void setMethodParameters(String methodParameters) {

        this.methodParameters = methodParameters;
    }

    public String getMethodParametersVariables() {
        
        return methodParametersVariables;
    }

    public void setMethodParametersVariables(String methodParametersVariables) {
     
        this.methodParametersVariables = methodParametersVariables;
    }

    public String getMethodParametersTypes() {
        
        return methodParametersTypes;
    }

    public void setMethodParametersTypes(String methodParametersTypes) {
     
        this.methodParametersTypes = methodParametersTypes;
    }

    public String getMethodSimpleReturnType() {
        
        return methodSimpleReturnType;
    }

    public void setMethodSimpleReturnType(String methodSimpleReturnType) {
     
        this.methodSimpleReturnType = methodSimpleReturnType;
    }

    public String getMethodReturnType() {
        
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
     
        this.methodReturnType = methodReturnType;
    }

    public String getFilePath() {

        return filePath;
    }

    public void setFilePath(String filePath) {

        this.filePath = filePath;
    }
    
    public int getLineNumber() {

        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {

        this.lineNumber = lineNumber;
    }
}
