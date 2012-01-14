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
package org.codeslayer.debugger;

import com.sun.jdi.VMDisconnectedException;
import java.util.List;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ClassPrepareRequest;
import org.codeslayer.debugger.command.OutputCommand;
import org.codeslayer.debugger.command.XmlOutputFormatter;
import org.codeslayer.debugger.connector.LaunchConnector;
import org.codeslayer.debugger.connector.SocketConnector;
import org.codeslayer.debugger.connector.VirtualMachineConnector;
import org.codeslayer.debugger.handler.BreakpointHandler;
import org.codeslayer.debugger.handler.CommandHandler;
import org.codeslayer.debugger.handler.ConsoleCommandHandler;
import org.codeslayer.debugger.handler.EventHandler;
import org.codeslayer.debugger.handler.InteractiveCommandHandler;
import org.codeslayer.debugger.handler.SourceHandler;

public class Main {

    public static void main(String args[]) {

        Modifiers modifiers = new Modifiers(args);

        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = createVirtualMachine(modifiers);
        } catch (Exception e) {
            System.err.println("Not able to connect to the VM. Did you define a -port or a -launch?");
            System.exit(1);
        }

        BreakpointHandler breakpointHandler = new BreakpointHandler(virtualMachine);
        CommandHandler commandHandler = createCommandHandler(modifiers, virtualMachine, breakpointHandler);
        Thread commandHandlerThread = new Thread(commandHandler);

        List<String> sourcePaths = modifiers.getSourcepath();
        if (sourcePaths == null) {
            System.err.println("You need to define the -sourcepath.");
            System.exit(1);
        }

        SourceHandler sourceHandler = new SourceHandler(sourcePaths);
        EventHandler eventHandler = new EventHandler(virtualMachine, commandHandler, breakpointHandler, sourceHandler);
        Thread eventHandlerThread = new Thread(eventHandler);

        commandHandlerThread.start();
        eventHandlerThread.start();

        ClassPrepareRequest classPrepareRequest = virtualMachine.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.setEnabled(true);

        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.READY);
        outputCommand.setText("ready");
        commandHandler.sendCommand(outputCommand);

        do {
            // keep running while threads are still alive
        } while (commandHandlerThread.isAlive() && eventHandlerThread.isAlive());

        virtualMachine.resume();
        try {
            virtualMachine.dispose();
        } catch (VMDisconnectedException e) {
            // disconnected
        }

        System.exit(1);
    }

    private static VirtualMachine createVirtualMachine(Modifiers modifiers)
            throws Exception {

        VirtualMachineConnector virtualMachineConnector = null;

        Integer port = modifiers.getPort();

        if (port != null) {
            virtualMachineConnector = new SocketConnector(port);
        } else {
            String exec = modifiers.getLaunch();
            if (exec != null) {
                String classpath = modifiers.getClasspath();
                virtualMachineConnector = new LaunchConnector(exec, classpath);
            }
        }
        
        return virtualMachineConnector.connect();
    }

    private static CommandHandler createCommandHandler(Modifiers modifiers, VirtualMachine virtualMachine, BreakpointHandler breakpointHandler) {

        if (modifiers.isInteractive()) {
            return new InteractiveCommandHandler(virtualMachine, breakpointHandler, new XmlOutputFormatter());
        }

        return new ConsoleCommandHandler(virtualMachine, breakpointHandler);
    }
}
