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
package org.codeslayer.navigate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.Command;
import org.codeslayer.source.scanner.PositionResult;
import org.codeslayer.source.scanner.PositionScanner;
import org.codeslayer.source.HierarchyManager;
import org.codeslayer.usage.UsageUtils;

public class NavigateCommand implements Command {
    
    private static Logger logger = Logger.getLogger(NavigateCommand.class);

    public String execute(String[] args) {
        
        try {
            NavigateModifiers modifiers = new NavigateModifiers(args);
            
            NavigateInput input = getInput(modifiers);
            
            File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
            HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);
            
            PositionScanner positionScanner = new PositionScanner(hierarchyManager, input);
            PositionResult positionResult = positionScanner.scan();
            
            NavigateHandler handler = new NavigateHandler(positionResult);
            NavigateOutput output = handler.getOutput();
            
            if (output != null) {
                logger.debug(output.getFilePath() + ":" + output.getLineNumber());
                return getOutput(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return "";
    }
    
    private static NavigateInput getInput(NavigateModifiers modifiers) {
        
        NavigateInput intput = new NavigateInput();
        
        File[] sourceFiles = getSourceFiles(modifiers);
        intput.setSourceFolders(sourceFiles);
        
        String indexesFolder = modifiers.getIndexesFolder();
        if (indexesFolder != null) {
            intput.setIndexesFolder(new File(indexesFolder));
        }
        
        String sourceFile = modifiers.getSourceFile();
        if (logger.isDebugEnabled()) {
            logger.debug("sourceFile " + sourceFile);
        }
        File file = new File(sourceFile);
        intput.setSourceFile(file);
        
        String position = modifiers.getPosition();
        if (logger.isDebugEnabled()) {
            logger.debug("position " + position);
        }
        if (position != null) {
            intput.setPosition(Integer.parseInt(position));
        }
        
        String lineNumber = modifiers.getLineNumber();
        if (lineNumber != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("lineNumber " + lineNumber);
            }
            intput.setLineNumber(Integer.parseInt(lineNumber));
        }
        
        return intput;
    }
    
    private static File[] getSourceFiles(NavigateModifiers modifiers) {
        
        List<File> results = new ArrayList<File>();

        List<String> sourceFolders = modifiers.getSourceFolders();
        for (String sourceFolder : sourceFolders) {
            if (logger.isDebugEnabled()) {
                logger.debug("sourceFolder " + sourceFolder);
            }
            List<File> files = UsageUtils.getFiles(sourceFolder);
            results.addAll(files);
        }
        
        return results.toArray(new File[results.size()]);
    }
    
    private String getOutput(NavigateOutput output) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(output.getFilePath()).append("\t").append(output.getLineNumber()).append("\n");
        
        return sb.toString();
    }
}
