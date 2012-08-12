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

import com.sun.source.tree.CompilationUnitTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeTree {
    
    private String packageName;
    private Map<String, String> simpleTypes = new HashMap<String, String>();
    private List<Import> imports = new ArrayList<Import>();

    public static ScopeTree newScopeTree(CompilationUnitTree compilationUnitTree) {
        
        ScopeTree scopeTree = new ScopeTree();
        scopeTree.setPackageName(SourceUtils.getPackageName(compilationUnitTree));
        return scopeTree;
    }  

    public String getPackageName() {
        
        return packageName;
    }

    public void setPackageName(String packageName) {
     
        this.packageName = packageName;
    }

    public String getSimpleType(String variable) {
        
        return simpleTypes.get(variable);
    }
    
    public void addSimpleType(String variable, String simpleType) {
        
        simpleTypes.put(variable, simpleType);
    }

    public List<Import> getImports() {
     
        return imports;
    }

    public void addImport(Import impt) {
     
        imports.add(impt);
    }
}
