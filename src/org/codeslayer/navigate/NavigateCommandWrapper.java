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
package org.codeslayer.navigate;

import java.util.List;
import org.codeslayer.Command;
import org.codeslayer.Modifiers;

public class NavigateCommandWrapper implements Command<Modifiers, String> {
    
    private final NavigateCommand command;

    public NavigateCommandWrapper(NavigateCommand command) {
     
        this.command = command;
    }
    
    public String execute(Modifiers modifiers) {
        
        NavigateInput input = getInput(modifiers);
        Navigate navigate = command.execute(input);
        
        if (navigate == null) {
            return "no results found";
        }
        
        return getOutput(navigate);
    }
    
    private NavigateInput getInput(Modifiers modifiers) {
        
        NavigateInput input = new NavigateInput();
        
        List<String> sourceFolders = modifiers.getSourceFolders();
        input.setSourceFolders(sourceFolders);
        
        String indexesFolder = modifiers.getIndexesFolder();
        input.setIndexesFolder(indexesFolder);
        
        String sourceFile = modifiers.getSourceFile();
        input.setSourceFile(sourceFile);
        
        String lineNumber = modifiers.getLineNumber();
        input.setLineNumber(Integer.parseInt(lineNumber));
        
        String position = modifiers.getPosition();
        input.setPosition(Integer.parseInt(position));
        
        return input;
    }
    
    private String getOutput(Navigate navigate) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(navigate.getFilePath()).append("\t").append(navigate.getLineNumber()).append("\n");
        
        return sb.toString();
    }
}
