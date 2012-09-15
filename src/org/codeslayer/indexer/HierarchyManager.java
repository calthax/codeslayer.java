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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HierarchyManager {
    
    private Map<String, Hierarchy> lookup = new HashMap<String, Hierarchy>();

    public List<Hierarchy> getHierarchyList(String className) {
        
        List<Hierarchy> list = new ArrayList<Hierarchy>();
        createHierarchy(list, className);
        return list;
    }
    
    public Hierarchy getHierarchy(String className) {
        
        return lookup.get(className);
    }
    
    private void createHierarchy(List<Hierarchy> list, String className) {
        
        if (className.equals("java.lang.Object")) {
            return;
        }
        
        Hierarchy hierarchy = lookup.get(className);
        
        if (hierarchy == null) {
            return;
        }
        
        list.add(hierarchy);

        createHierarchy(list, hierarchy.getSuperClass());        
    }

    public void addHierarchy(Hierarchy hierarchy) {
     
        this.lookup.put(hierarchy.getClassName(), hierarchy);
    }
}
