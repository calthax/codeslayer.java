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

import java.util.List;
import org.codeslayer.Command;
import org.codeslayer.Modifiers;

public class SearchCommandWrapper implements Command<Modifiers, String> {

    private final SearchCommand command;

    public SearchCommandWrapper(SearchCommand command) {
     
        this.command = command;
    }
    
    public String execute(Modifiers modifiers) {
        
        SearchInput input = getInput(modifiers);
        List<Search> searches = command.execute(input);
        return getOutput(searches);
    }
    
    private SearchInput getInput(Modifiers modifiers) {
        
        SearchInput input = new SearchInput();
        
        String indexesFolder = modifiers.getIndexesFolder();
        input.setIndexesFolder(indexesFolder);
        
        String name = modifiers.getName();
        input.setName(name);
        
        return input;
    }
    
    private String getOutput(List<Search> searches) {
        
        StringBuilder sb = new StringBuilder();
        
        for (Search completion : searches) {
            sb.append(completion.getSimpleClassName()).append("\t").append(completion.getClassName()).append("\t").append(completion.getFilePath()).append("\n");
        }
        
        return sb.toString();
    }
}
