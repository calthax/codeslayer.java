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
package org.codeslayer.usage;

import java.io.File;

public class Usage {
    
    private String packageName;
    private String className;
    private String methodName;
    private String expression;
    private File file;
    private int lineNumber;

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

    public String getExpression() {
        
        return expression;
    }

    public void setExpression(String expression) {
     
        this.expression = expression;
    }

    public File getFile() {
        
        return file;
    }

    public void setFile(File file) {
     
        this.file = file;
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
        sb.append(" packageName: ").append(packageName);
        sb.append(" className: ").append(className);
        sb.append(" methodName: ").append(methodName);
        sb.append(" expression: ").append(expression);
        sb.append(" file: ").append(file.getAbsolutePath());
        sb.append(" lineNumber: ").append(lineNumber);
        return sb.toString();
    }   
}
