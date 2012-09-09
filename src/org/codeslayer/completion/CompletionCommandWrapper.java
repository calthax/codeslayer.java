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
package org.codeslayer.completion;

import java.util.List;
import org.codeslayer.Command;
import org.codeslayer.Modifiers;

public class CompletionCommandWrapper implements Command<Modifiers, String> {
    
    private final CompletionCommand command;

    public CompletionCommandWrapper(CompletionCommand command) {
     
        this.command = command;
    }
    
    @Override
    public String execute(Modifiers modifiers) {
        
        CompletionInput input = getInput(modifiers);
        List<Completion> completions = command.execute(input);
        
        if (completions.isEmpty()) {
            return "no results found";
        }
        
        return getOutput(completions);
    }
    
    private CompletionInput getInput(Modifiers modifiers) {
        
        CompletionInput input = new CompletionInput();
        
        String indexesFolder = modifiers.getIndexesFolder();
        if (indexesFolder != null) {
            input.setIndexesFolder(indexesFolder);
        }
        
        String sourceFile = modifiers.getSourceFile();
        input.setSourceFile(sourceFile);
        
        String lineNumber = modifiers.getLineNumber();
        input.setLineNumber(Integer.parseInt(lineNumber));
        
        String expression = modifiers.getExpression();
        input.setExpression(expression);
        
        return input;
    }
    
    private String getOutput(List<Completion> completions) {
        
        StringBuilder sb = new StringBuilder();
        
        for (Completion completion : completions) {
            sb.append(completion.getMethodName()).append("\t").
               append(completion.getMethodParameters()).append("\t").
               append(completion.getMethodParameterVariables()).append("\t").
               append(completion.getMethodReturnType()).append("\n");
        }
        
        return sb.toString();
    }
}
