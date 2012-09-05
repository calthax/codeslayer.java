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
import org.apache.log4j.Logger;

public class SearchHandler {
 
    private static Logger logger = Logger.getLogger(SearchHandler.class);

    private final SearchInput input;
    private final SearchCache cache;
    
    public SearchHandler(SearchInput input, SearchCache cache) {
     
        this.input = input;
        this.cache = cache;
    }
    
    public List<Search> getSearchResults() {
        
        if (cache.hasValues(input.getName())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using cached search results");
            }
            return getSearchResultsFromCache();
        }

        return getSearchResultsFromFile();
    }

    private List<Search> getSearchResultsFromCache() {
        
        List<Search> results = new ArrayList<Search>();
        
        for (Search search : cache.getValues()) {
            if (search.getSimpleClassName().startsWith(input.getName())) {
                results.add(search);
            }
        }
        
        return results;
    }

    private List<Search> getSearchResultsFromFile() {
        
        List<Search> results = new ArrayList<Search>();
        
        File file = new File(input.getIndexesFolder(), "projects.classes");
        
        String name = input.getName();
        
        cache.reset(name);
        
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
                if (simpleClassName.startsWith(name)) {
                    Search search = new Search();
                    search.setSimpleClassName(simpleClassName);
                    search.setClassName(split[1]);
                    search.setFilePath(split[2]);
                    results.add(search);
                    
                    cache.addValue(search);
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
