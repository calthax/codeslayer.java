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

import org.codeslayer.usage.domain.Usage;
import org.codeslayer.source.Method;
import org.codeslayer.usage.domain.Input;
import org.codeslayer.usage.scanner.InputScanner;
import org.codeslayer.usage.scanner.MethodUsageScanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.source.HierarchyManager;

public class Main {
    
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        
        try {
            String[] dummy = new String[] {"-sourcefolder", "/home/jeff/workspace/jmesa/src:/home/jeff/workspace/jmesaWeb/src", 
                                           "-indexesfolder", "/home/jeff/.codeslayer-dev/groups/java/indexes", 
                                           "-usagefile", "/home/jeff/workspace/jmesa/src/org/jmesa/worksheet/WorksheetCallbackHandler.java", 
                                           "-methodusage", "process", 
                                           "-linenumber", "25"};
            
            Modifiers modifiers = new Modifiers(dummy);
            
            Input input = getInput(modifiers);
            
            InputScanner inputScanner = new InputScanner(input);
            Method methodMatch = inputScanner.scan();
            
            if (methodMatch == null) {
                throw new IllegalStateException("the input scanner did not find the method");
            }
            
            File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
            HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);
            
            MethodUsageScanner methodUsageScanner = new MethodUsageScanner(hierarchyManager, methodMatch, input);
            List<Usage> usages = methodUsageScanner.scan();
            usages = UsageUtils.filterUsages(hierarchyManager, methodMatch, usages);
            
            if (logger.isDebugEnabled()) {
                logger.debug("************ Usage Search Results ************");
            }
            
            for (Usage usage : usages) {
                logger.debug(usage.getClassName() + ":" + usage.getLineNumber() + " " + usage.getFile());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }

        System.exit(1);
    }
    
    private static Input getInput(Modifiers modifiers) {
        
        Input intput = new Input();
        
        File[] sourceFiles = getSourceFiles(modifiers);
        intput.setSourceFolders(sourceFiles);
        
        String indexesFolder = modifiers.getIndexesFolder();
        if (indexesFolder != null) {
            intput.setIndexesFolder(new File(indexesFolder));
        }
        
        String usageFile = modifiers.getUsageFile();
        if (logger.isDebugEnabled()) {
            logger.debug("usageFile " + usageFile);
        }
        File file = new File(usageFile);
        intput.setUsageFile(file);
        
        String methodUsage = modifiers.getMethodUsage();
        if (logger.isDebugEnabled()) {
            logger.debug("methodUsage " + methodUsage);
        }
        intput.setMethodUsage(methodUsage);
        
        String classUsage = modifiers.getClassUsage();
        intput.setClassUsage(classUsage);
        
        String lineNumber = modifiers.getLineNumber();
        if (lineNumber != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("lineNumber " + lineNumber);
            }
            intput.setLineNumber(Integer.parseInt(lineNumber));
        }
        
        return intput;
    }
    
    private static File[] getSourceFiles(Modifiers modifiers) {
        
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
}
