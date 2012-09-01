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

import java.util.*;
import org.codeslayer.source.Clazz;
import org.codeslayer.source.Method;

public class IndexFactory {
    
    public List<Index> createIndexes(List<Clazz> klasses) {
        
        List<Index> indexes = new ArrayList<Index>();
        for (Clazz klass : klasses) {
            createIndexesForClass(indexes, klass);
        }
        return indexes;
    } 

    private void createIndexesForClass(List<Index> indexes, Clazz klass) {

        String interfaces = getInterfaces(klass);
        
        List<Method> methods = klass.getMethods();
        
        if (methods == null || methods.isEmpty()) { // probably an interface
            Index index = new Index();
            index.setClassName(klass.getClassName());
            index.setSimpleClassName(klass.getSimpleClassName());
            index.setSuperClass(klass.getSuperClass());
            index.setInterfaces(interfaces);
            index.setFilePath(klass.getFilePath());
            index.setLineNumber(0);
            indexes.add(index);
        } else {
            for (Method method : methods) {
                Index index = new Index();
                index.setClassName(klass.getClassName());
                index.setSimpleClassName(klass.getSimpleClassName());
                index.setSuperClass(klass.getSuperClass());
                index.setInterfaces(interfaces);
                index.setMethodName(method.getName());
                index.setFilePath(klass.getFilePath());
                index.setLineNumber(method.getLineNumber());
                indexes.add(index);
            }
        }
    }
    
    private String getInterfaces(Clazz klass) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<String> iterator = klass.getInterfaces().iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
}
