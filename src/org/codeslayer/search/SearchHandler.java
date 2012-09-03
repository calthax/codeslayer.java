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
package org.codeslayer.search;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
 
    private final SearchInput input;

    public SearchHandler(SearchInput input) {
     
        this.input = input;
    }
    
    public List<Search> getSearchResults() {
        
        List<Search> results = new ArrayList<Search>();
        
        File file = new File(input.getIndexesFolder(), "projects.classes");
        
        String search = input.getName();
        
        try{
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine == null || strLine.trim().length() == 0) {
                    continue;
                }

                String[] split = strLine.split("\\t");
                
                String simpleClassName = split[0];
                if (simpleClassName.startsWith(search)) {
                    Search completion = new Search();
                    completion.setSimpleClassName(simpleClassName);
                    completion.setClassName(split[1]);
                    completion.setFilePath(split[2]);
                    results.add(completion);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("not able to load the libs.indexes file.");
        }

        return results;
    }
}
