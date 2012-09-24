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
package org.codeslayer.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codeslayer.CodeSlayerUtils;
import org.codeslayer.Command;
import org.codeslayer.Modifiers;

public class Client implements Runnable {
    
    private static Logger logger = Logger.getLogger(Client.class);

    private final Socket socket;
    private final Map<String, Command> programs;

    public Client(Socket socket, Map<String, Command> programs) {
     
        this.socket = socket;
        this.programs = programs;
    }
    
    @Override
    public void run() {
        
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;

            while ((input = in.readLine()) != null) {
                if (input.equals("quit")) {
                    break;
                }

                String[] args = input.split("\\s");
                Command<Modifiers, String> command =  CodeSlayerUtils.getCommand(args, programs);
                Modifiers modifiers = new Modifiers(args);

                String output;
                try {
                    output = command.execute(modifiers);
                } catch (Exception e) {
                    logger.debug("Program threw an exception", e);
                    output = "Program threw an exception.";
                }

                if (output == null) {
                    output = "Program did not return any results.";
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("output: " + output);
                }

                out.println(output);
                out.flush();
            }

            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            logger.error("Could not read from the client socket", e);
        }
    }    
}
