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

public class Input {
    
    private File[] sourceFolders;
    private File usageFile;
    private String methodUsage;
    private String classUsage;
    private int lineNumber;

    public File getUsageFile() {
        
        return usageFile;
    }

    public void setUsageFile(File usageFile) {
        
        this.usageFile = usageFile;
    }

    public String getClassUsage() {
        
        return classUsage;
    }

    public void setClassUsage(String classUsage) {
        
        this.classUsage = classUsage;
    }

    public String getMethodUsage() {
        
        return methodUsage;
    }

    public void setMethodUsage(String methodUsage) {
        
        this.methodUsage = methodUsage;
    }

    public int getLineNumber() {
        
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        
        this.lineNumber = lineNumber;
    }

    public File[] getSourceFolders() {
        
        return sourceFolders;
    }

    public void setSourceFolders(File[] sourceFolders) {

        this.sourceFolders = sourceFolders;
    }
}
