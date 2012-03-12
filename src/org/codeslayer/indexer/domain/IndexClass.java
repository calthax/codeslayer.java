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

import java.util.ArrayList;
import java.util.List;

public class IndexClass {
    
    private String packageName;
    private String className;
    private String filePath;
    private String superClass;
    private List<String> interfaces;
    private List<String> imports;
    private List<IndexMethod> methods = new ArrayList<IndexMethod>();

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

    public String getFilePath() {
        
        return filePath;
    }

    public void setFilePath(String filePath) {
     
        this.filePath = filePath;
    }

    public List<IndexMethod> getMethods() {
        
        return methods;
    }

    public void addMethod(IndexMethod method) {
     
        this.methods.add(method);
    }

    public List<String> getInterfaces() {
        
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
     
        this.interfaces = interfaces;
    }

    public List<String> getImports() {
        
        return imports;
    }

    public void setImports(List<String> imports) {
     
        this.imports = imports;
    }

    public String getSuperClass() {
        
        return superClass;
    }

    public void setSuperClass(String superClass) {
     
        this.superClass = superClass;
    }
}
