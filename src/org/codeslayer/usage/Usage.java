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
import java.util.ArrayList;
import java.util.List;

public class Usage {
    
    private String packageName;
    private String className;
    private String methodName;
    private List<String> methodArguments = new ArrayList<String>();
    private File file;
    private int lineNumber;
    private int startPosition;
    private int endPosition;

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

    public List<String> getMethodArguments() {
        
        return methodArguments;
    }

    public void addMethodArgument(String methodArgument) {
     
        this.methodArguments.add(methodArgument);
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

    public int getStartPosition() {
        
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
     
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        
        this.endPosition = endPosition;
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(" packageName: [").append(packageName).append("]");
        sb.append(" className: [").append(className).append("]");
        sb.append(" methodName: [").append(methodName).append("]");        
        sb.append(" methodArguments: [");
        for (String methodArgument : methodArguments) {
            sb.append(methodArgument);
        }
        sb.append("]");
        sb.append(" file: [").append(file.getAbsolutePath()).append("]");
        sb.append(" lineNumber: [").append(lineNumber).append("]");
        sb.append(" startPosition: [").append(startPosition).append("]");
        sb.append(" endPosition: [").append(endPosition).append("]");
        return sb.toString();
    }   
}
