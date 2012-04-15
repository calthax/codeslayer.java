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
import java.util.List;

public class IndexesFile {
    
    private final String folder;
    private final String type;

    public IndexesFile(String folder, String type) {
     
        this.folder = folder;
        this.type = type;
    }

    public void write(List<Index> indexes) 
            throws Exception {
        
        StringBuilder sb = new StringBuilder();
        
        for (Index index : indexes) {
            sb.append(index.getClassName());
            sb.append("\t");
            sb.append(index.getSimpleClassName());
            sb.append("\t");
            sb.append(index.getMethodModifier());
            sb.append("\t");
            sb.append(index.getMethodName());
            sb.append("\t");
            sb.append(index.getMethodParameters());
            sb.append("\t");
            sb.append(index.getMethodParametersVariables());
            sb.append("\t");
            sb.append(index.getMethodParametersTypes());
            sb.append("\t");
            sb.append(index.getMethodReturnType());
            
            String filePath = index.getFilePath();
            if (filePath != null) {
                sb.append("\t");
                sb.append(filePath);
            }
            
            String lineNumber = index.getLineNumber();
            if (lineNumber != null) {
                sb.append("\t");
                sb.append(lineNumber);                
            }
            
            sb.append("\n");
        }
        
        File file = new File(folder, type+".indexes");
        Writer out = new OutputStreamWriter(new FileOutputStream(file));
        try {
            out.write(sb.toString());
        }
        finally {
            out.close();
        }
    }
}
