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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeTree {
    
    private String packageName;
    private final Map<String, String> variables = new HashMap<String, String>();
    private final List<String> importNames = new ArrayList<String>();

    public String getPackageName() {
        
        return packageName;
    }

    public void setPackageName(String packageName) {
     
        this.packageName = packageName;
    }

    public String getVariable(String name) {
        
        return variables.get(name);
    }
    
    public void addVariable(String name, String variable) {
        
        variables.put(name, variable);
    }

    public List<String> getImportNames() {
     
        return importNames;
    }

    public void addImportName(String importName) {
     
        importNames.add(importName);
    }
    
    public String getClassName(String variable) {
        
        // check to see if this is a primative
        if (Character.isLowerCase(variable.toCharArray()[0])) {
            return variable;
        }
        
        if (variable.equals("String")) {
            return "java.lang.String";
        } else if (variable.equals("Object")) {
            return "java.lang.Object";
        }
        
        for (String importName : importNames) {
            if (importName.endsWith("." + variable)) {
                return importName;
            }
        }

        return packageName + "." + variable;
    }
}
