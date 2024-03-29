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

        Command<Modifiers, String> command = CodeSlayerUtils.getCommand(args, programs);
        
        Modifiers modifiers = new Modifiers(args);
        
        String output = command.execute(modifiers);
        
        System.out.println(output);
    }
}
