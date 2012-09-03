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
package org.codeslayer;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codeslayer.completion.CompletionCommand;
import org.codeslayer.indexer.IndexerCommand;
import org.codeslayer.navigate.NavigateCommand;
import org.codeslayer.search.SearchCommand;
import org.codeslayer.usage.UsageCommand;

public class CodeSlayerUtils {
    
    private static Logger logger = Logger.getLogger(CodeSlayerUtils.class);

    public static Map<String, Command> getPrograms() {
        
        Map<String, Command> programs = new HashMap<String, Command>();
        
        programs.put("usage", new UsageCommand());
        programs.put("indexer", new IndexerCommand());
        programs.put("navigate", new NavigateCommand());
        programs.put("completion", new CompletionCommand());
        programs.put("search", new SearchCommand());
        
        return programs;
    }
    
    public static Command getCommand(String[] args, Map<String, Command> programs) {
        
        for (int i = 0; i < args.length; i++) {
            String input = args[i];            
            if (input.equals("-program")) {
                String cmd = args[i+1];
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Program command is '" + cmd + "'");
                }
                
                return programs.get(cmd);
            }
        }
        
        throw new IllegalArgumentException("There is not a program argument defined");
    }
}
