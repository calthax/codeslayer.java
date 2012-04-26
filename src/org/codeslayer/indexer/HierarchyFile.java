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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HierarchyFile {
    
    private final String folder;
    private final String type;

    public HierarchyFile(String folder, String type) {
     
        this.folder = folder;
        this.type = type;
    }

    public void write(List<Index> indexes) 
            throws Exception {
        
        StringBuilder sb = new StringBuilder();
        
        for (Index index : filter(indexes)) {
            
            String simpleClassName = index.getSimpleClassName();
            if (simpleClassName == null || simpleClassName.length() == 0) {
                continue;
            }

            sb.append(index.getClassName());
            sb.append("\t");
            
            sb.append(index.getSuperClass());
            sb.append("\t");
            
            String interfaces = index.getInterfaces();
            if (interfaces != null) {
                sb.append(interfaces);
            }
            sb.append("\t");

            String filePath = index.getFilePath();
            if (filePath != null) {
                sb.append(filePath);
            }

            sb.append("\n");
        }
        
        File file = new File(folder, type + ".hierarchy");
        Writer out = new OutputStreamWriter(new FileOutputStream(file));
        try {
            out.write(sb.toString());
        }
        finally {
            out.close();
        }
    }
    
    private List<Index> filter(List<Index> indexes) {
        
        List<Index> results = new ArrayList<Index>();
        
        String lastClassName = "";
        
        for (Index index : indexes) {
            String className = index.getClassName();
            if (className.equals(lastClassName)) {
                continue;
            }
            lastClassName = className;
            results.add(index);
        }
        
        Collections.sort(results, new Comparator<Index>() {
            public int compare(Index index1, Index index2) {
                return index1.getSimpleClassName().compareTo(index2.getSimpleClassName());
            }
        });
        
        return results;
    }
}
