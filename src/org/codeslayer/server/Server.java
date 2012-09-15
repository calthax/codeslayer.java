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

import org.codeslayer.Command;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codeslayer.CodeSlayerUtils;
import org.codeslayer.Modifiers;

public class Server implements Runnable {
    
    private static Logger logger = Logger.getLogger(Server.class);
    
    private final Map<String, Command> programs = CodeSlayerUtils.getPrograms();

    private ServerSocket serverSocket;
    private boolean running = true;
    
    public void shutdown() {

        running = false;
        try {
            serverSocket.close();            
        } catch (Exception e) {
            logger.error("Could not close the server socket");
        }
    }

    @Override
    public void run() {
        
        logger.debug("Server starting up");
        
        while (running) {
            
            if (serverSocket == null) {
                try {
                    serverSocket = new ServerSocket(4444);
                } catch (IOException e) {
                    logger.error("Could not listen on port 4444");
                }
            }

            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                logger.error("Could not accept client socket");
            }
            
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                    
                    out.println(output);
                    out.flush();
                }
                
                out.close();
                in.close();
                clientSocket.close();
            } catch (Exception e) {
                logger.error("Could not read from the client socket", e);
            }
        }
        
        logger.info("server shutting down");
    }
}
