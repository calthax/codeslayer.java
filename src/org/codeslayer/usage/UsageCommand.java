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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.Command;
import org.codeslayer.source.HierarchyManager;
import org.codeslayer.source.Method;
import org.codeslayer.source.Parameter;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.usage.domain.Usage;
import org.codeslayer.usage.scanner.UsageInputScanner;
import org.codeslayer.usage.scanner.MethodUsageScanner;

public class UsageCommand implements Command {
    
    private static Logger logger = Logger.getLogger(UsageCommand.class);

    public String execute(String[] args) {
        
        try {
            UsageModifiers modifiers = new UsageModifiers(args);
            
            UsageInput input = getInput(modifiers);
            
            UsageInputScanner usageInputScanner = new UsageInputScanner(input);
            Method methodMatch = usageInputScanner.scan();
            
            if (methodMatch == null) {
                throw new IllegalStateException("the input scanner did not find the method");
            }
            
            File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
            HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);
            
            MethodUsageScanner methodUsageScanner = new MethodUsageScanner(hierarchyManager, methodMatch, input);
            List<Usage> usages = methodUsageScanner.scan();
            usages = filterUsages(hierarchyManager, methodMatch, usages);
            
            if (logger.isDebugEnabled()) {
                logger.debug("************ Usage Search Results ************");
            }
            
            for (Usage usage : usages) {
                logger.debug(usage.getClassName() + ":" + usage.getLineNumber() + " " + usage.getFile());
            }
            
            return getOutput(usages);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return "";
    }
    
    private static UsageInput getInput(UsageModifiers modifiers) {
        
        UsageInput intput = new UsageInput();
        
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
        
        String methodUsage = modifiers.getMethodUsage();
        if (logger.isDebugEnabled()) {
            logger.debug("methodUsage " + methodUsage);
        }
        intput.setMethodUsage(methodUsage);
        
        String lineNumber = modifiers.getLineNumber();
        if (lineNumber != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("lineNumber " + lineNumber);
            }
            intput.setLineNumber(Integer.parseInt(lineNumber));
        }
        
        return intput;
    }
    
    private static File[] getSourceFiles(UsageModifiers modifiers) {
        
        List<File> results = new ArrayList<File>();

        List<String> sourceFolders = modifiers.getSourceFolders();
        for (String sourceFolder : sourceFolders) {
            if (logger.isDebugEnabled()) {
                logger.debug("sourceFolder " + sourceFolder);
            }
            List<File> files = SourceUtils.getFiles(sourceFolder);
            results.addAll(files);
        }
        
        return results.toArray(new File[results.size()]);
    }
    
    public static List<Usage> filterUsages(HierarchyManager hierarchyManager, Method methodMatch, List<Usage> usages) {
        
        List<Usage> results = new ArrayList<Usage>();
        
        if (logger.isDebugEnabled()) {
            logger.debug("************ Filter Usages ************");
        }

        List<Parameter> methodParameters = methodMatch.getParameters();
        
        if (methodParameters == null || methodParameters.isEmpty()) {
            return usages;
        }

        for (Usage usage : usages) {
            List<Parameter> usageParameters = usage.getMethod().getParameters();
            
            if (logger.isDebugEnabled()) {
                logger.debug(usage.getClassName() + ":" + usage.getLineNumber() + " " + usageParameters);            
            }

            if (usageParameters.size() != methodParameters.size()) {
                continue;
            }
            
            if (SourceUtils.parametersEqual(hierarchyManager, usageParameters, methodParameters)) {
                results.add(usage);
            }            
        }            
        
        return results;
    }
    
    private String getOutput(List<Usage> usages) {
        
        StringBuilder sb = new StringBuilder();
        
        for (Usage usage : usages) {
            sb.append(usage.getClassName()).append("\t").append(usage.getFile().getPath()).append("\t").append(usage.getLineNumber()).append("\n");
        }
        
        return sb.toString();
    }
}
