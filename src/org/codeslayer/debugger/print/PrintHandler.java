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
package org.codeslayer.debugger.print;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import org.codeslayer.debugger.command.InputCommand;
import org.codeslayer.debugger.command.OutputCommand;
import org.codeslayer.debugger.handler.CommandHandler;

public class PrintHandler {

    private final CommandHandler commandHandler;
    private final PrintFormatter printFormatter;

    public PrintHandler(CommandHandler commandHandler, PrintFormatter printFormatter) {

        this.commandHandler = commandHandler;
        this.printFormatter = printFormatter;
    }
    
    public void value(ThreadReference threadReference, InputCommand inputCommand)
            throws Exception {

        Iterator<String> variableNames = PrintUtils.getVariableNames(inputCommand.getVariableName());
        String variableName = variableNames.next();
        
        Value value = getValueByThreadReference(threadReference, variableName);
        Value findValue = PrintUtils.findValue(value, variableNames);

        List<PrintRow> printRows = null;
        if (value != null) {
            printRows = printFormatter.format(findValue, inputCommand.getModifiers());
        }

        if (printRows == null) {
            OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.INVALID_VARIABLE);
            commandHandler.sendCommand(outputCommand);
        } else {
            OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.PRINT_VALUE);
            outputCommand.setPrintRows(printRows);
            commandHandler.sendCommand(outputCommand);
        }
    }

    private Value getValueByThreadReference(ThreadReference threadReference, String variableName)
            throws Exception {

        StackFrame stackFrame = threadReference.frame(0);

        Matcher matcher = PrintUtils.PATTERN.matcher(variableName);
        if (!matcher.find()) {
            return null;
        }

        String name = matcher.group(1);
        
        LocalVariable localVariable = stackFrame.visibleVariableByName(name);
        if (localVariable != null) {
            return PrintUtils.getValueByType(matcher, stackFrame.getValue(localVariable));
        }

        return PrintUtils.getValueByObjectReference(stackFrame.thisObject(), variableName);
    }
}
