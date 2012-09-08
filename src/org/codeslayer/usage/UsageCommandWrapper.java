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
package org.codeslayer.usage;

import java.util.List;
import org.codeslayer.Command;
import org.codeslayer.Modifiers;

public class UsageCommandWrapper implements Command<Modifiers, String> {
    
    private final UsageCommand command;

    public UsageCommandWrapper(UsageCommand command) {
     
        this.command = command;
    }

    public String execute(Modifiers modifiers) {
        
       UsageInput input = getInput(modifiers);
       List<Usage> usages = command.execute(input);
       return getOutput(usages);
    }
    
    private UsageInput getInput(Modifiers modifiers) {
        
        UsageInput input = new UsageInput();
        
        List<String> sourceFolders = modifiers.getSourceFolders();
        input.setSourceFolders(sourceFolders);
        
        String indexesFolder = modifiers.getIndexesFolder();
        input.setIndexesFolder(indexesFolder);
        
        String sourceFile = modifiers.getSourceFile();
        input.setSourceFile(sourceFile);
        
        String lineNumber = modifiers.getLineNumber();
        input.setLineNumber(Integer.parseInt(lineNumber));
        
        String methodUsage = modifiers.getMethodUsage();
        input.setMethodUsage(methodUsage);
        
        return input;
    }
    
    private String getOutput(List<Usage> usages) {
        
        StringBuilder sb = new StringBuilder();
        
        for (Usage usage : usages) {
            sb.append(usage.getClassName()).append("\t").append(usage.getFile().getPath()).append("\t").append(usage.getLineNumber()).append("\n");
        }
        
        return sb.toString();
    }
}
