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

public class Main {
    
    public static void main(String[] args) {
        
        String[] dummy = new String[] {"-sourcefolder", "/home/jeff/workspace/jmesa/src:/home/jeff/workspace/jmesaWeb/src", 
                                       "-indexesfolder", "/home/jeff/.codeslayer-dev/groups/java/indexes", 
                                       "-usagefile", "/home/jeff/workspace/jmesa/src/org/jmesa/worksheet/WorksheetCallbackHandler.java", 
                                       "-methodusage", "process", 
                                       "-linenumber", "25"};

        UsageCommand usageCommand = new UsageCommand();
        usageCommand.execute(dummy);

        System.exit(1);
    }
}
