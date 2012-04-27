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
package org.codeslayer.usage.domain;

import java.io.File;
import org.codeslayer.source.Method;

public class Usage {
    
    private String className;
    private String simpleClassName;
    private Method method;
    private File file;
    private int lineNumber;
    private int startPosition;
    private int endPosition;

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

    public Method getMethod() {
        
        return method;
    }

    public void setMethod(Method method) {
     
        this.method = method;
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
        sb.append(super.toString());
        sb.append("[className=").append(className);
        sb.append(", simpleClassName=").append(simpleClassName);
        sb.append(", file=").append(file.getAbsolutePath());
        sb.append(", lineNumber=").append(lineNumber);
        sb.append(", startPosition=").append(startPosition);
        sb.append(", endPosition=").append(endPosition);
        sb.append(", ").append(method);
        return sb.append("]").toString();
    }   
}
