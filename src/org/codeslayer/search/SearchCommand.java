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

import java.io.File;
import java.util.List;
import org.codeslayer.Command;

public class SearchCommand implements Command {
    
    public String execute(String[] args) {
        
        try {
            SearchModifiers modifiers = new SearchModifiers(args);
            SearchInput input = getInput(modifiers);
            SearchHandler classHandler = new SearchHandler(input);
            List<Search> completions = classHandler.getSearchResults();
            return getOutput(completions);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return "";
    }
    
    private static SearchInput getInput(SearchModifiers modifiers) {
        
        SearchInput intput = new SearchInput();
        
        String indexesFolder = modifiers.getIndexesFolder();
        if (indexesFolder != null) {
            intput.setIndexesFolder(new File(indexesFolder));
        }
        
        String className = modifiers.getName();
        if (className != null) {
            intput.setName(className);
        }
        
        return intput;
    }
    
    private String getOutput(List<Search> completions) {
        
        StringBuilder sb = new StringBuilder();
        
        for (Search completion : completions) {
            sb.append(completion.getSimpleClassName()).append("\t").append(completion.getClassName()).append("\t").append(completion.getFilePath()).append("\n");
        }
        
        return sb.toString();
    }
}
