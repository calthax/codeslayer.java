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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HierarchyManager {
    
    private Map<String, Hierarchy> lookup = new HashMap<String, Hierarchy>();

    public List<Klass> getKlassHierarchy(String className) {
        
        List<Klass> klasses = new ArrayList<Klass>();
        createHierarchy(klasses, className);
        return klasses;
    }
    
    private void createHierarchy(List<Klass> klasses, String className) {
        
        Hierarchy hierarchy = lookup.get(className);
        
        if (hierarchy == null) {
            return;
        }
        
        Klass klass = new Klass();
        klass.setClassName(className);
        klass.setFilePath(hierarchy.getFilePath());
        
        String superClass = hierarchy.getSuperClass();
        klass.setSuperClass(superClass);
        
        klasses.add(klass);

        createHierarchy(klasses, superClass);        
    }

    public void addHierarchy(Hierarchy hierarchy) {
     
        this.lookup.put(hierarchy.getClassName(), hierarchy);
    }
}
