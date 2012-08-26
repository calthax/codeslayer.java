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

import java.io.IOException;
import java.util.Map;

public class Executor {
    
    public static void main(String[] args) 
            throws IOException {
        
        Map<String, Command> programs = CodeSlayerUtils.getPrograms();

        String[] dummy = {"-program", "navigate", 
                            "-sourcefolder", "/home/jeff/workspace/jmesa/src:/home/jeff/workspace/jmesaWeb/src", 
                            "-indexesfolder", "/home/jeff/.codeslayer-dev/groups/java/indexes",
                            "-sourcefile", "/home/jeff/workspace/jmesa/src/org/jmesa/limit/LimitActionFactory.java",
                            "-symbol", "Filter",
                            "-linenumber", "107"};

        Command command = CodeSlayerUtils.getCommand(dummy, programs);
        
        String output = command.execute(dummy);
        
        System.out.println(output);
        
        System.exit(1);
    }    
}