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

import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.Command;
import org.codeslayer.source.ScopeContext;
import org.codeslayer.source.scanner.ScopeContextScanner;

public class CompletionCommand implements Command<CompletionInput, List<Completion>> {
    
    private static Logger logger = Logger.getLogger(CompletionCommand.class);
    
    @Override
    public List<Completion> execute(CompletionInput input) {
        
        try {
            ScopeContextScanner scopeContextScanner = new ScopeContextScanner(input);
            ScopeContext scopeContext = scopeContextScanner.scan();
             
            CompletionHandler completionHandler = new CompletionHandler(input, scopeContext);
            List<Completion> completions = completionHandler.getCompletions();
            
            return completions;
        } catch (Exception e) {
            logger.error("Not able to execute completions command", e);
        }
        
        return Collections.emptyList();
    }
}
