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

    private String packageName;
    private String className;
    private String methodName;
    private String methodModifier;
    private String methodParameters;
    private String methodCompletion;
    private String methodReturnType;
    private String filePath;
    private String lineNumber;

    public String getPackageName() {

        return packageName;
    }

    public void setPackageName(String packageName) {

        this.packageName = packageName;
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {

        this.className = className;
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

    public String getMethodCompletion() {
        
        return methodCompletion;
    }

    public void setMethodCompletion(String methodCompletion) {
     
        this.methodCompletion = methodCompletion;
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
    
    public String getLineNumber() {

        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {

        this.lineNumber = lineNumber;
    }
}
