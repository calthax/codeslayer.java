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

import java.util.Collections;
import java.util.List;

public class Klass {
    
    private String className;
    private String simpleClassName;
    private String filePath;
    private String superClass;
    private List<String> imports;
    private List<String> interfaces;
    private List<Method> methods;

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

    public List<String> getImports() {
        
        if (imports == null) {
            return Collections.emptyList();
        }
        
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

    public List<String> getInterfaces() {
        
        if (interfaces == null) {
            return Collections.emptyList();
        }
        
       return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
     
        this.interfaces = interfaces;
    }
    
    public String getFilePath() {
        
        return filePath;
    }

    public void setFilePath(String filePath) {
     
        this.filePath = filePath;
    }

    public List<Method> getMethods() {
        
        if (methods == null) {
            return Collections.emptyList();
        }
        
        return methods;
    }

    public void addMethod(Method method) {
     
        this.methods.add(method);
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(" className: [").append(className).append("]");
        sb.append(" simpleClassName: [").append(simpleClassName).append("]");
        sb.append(" superClassName: [").append(superClass).append("]");
        sb.append(" superClass: [").append(superClass).append("]");        
        sb.append(" interfaces: [\n");
        for (String interfaceName: getInterfaces()) {
            sb.append(interfaceName).append("\n");
        }
        sb.append("]");
        sb.append(" imports: [\n");
        for (String imp: getImports()) {
            sb.append(imp).append("\n");
        }
        sb.append("]");
        sb.append(" methods: [\n");
        for (Method method: getMethods()) {
            sb.append(method).append("\n");
        }
        sb.append("]");
        sb.append(" filePath: [").append(filePath).append("]");
        return sb.toString();
    }    
}
