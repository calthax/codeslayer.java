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
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codeslayer.CodeSlayerUtils;

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

            try {
                Client client = new Client(serverSocket.accept(), programs);
                Thread thread = new Thread(client);
                thread.start();                
            } catch (IOException e) {
                logger.error("Could not accept client socket");
            }            
        }
        
        logger.info("Server shutting down");
    }
}
