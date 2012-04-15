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

public class Main {

    public static void main(String[] args) {
        
        System.out.println("usage file");
        
        try {
            String[] dummy = new String[] {"-sourcefolder", "/home/jeff/workspace/jmesa/src:/home/jeff/workspace/jmesaWeb/src", "-indexesfolder", "/home/jeff/.codeslayer-dev/groups/java/indexes", "-usagefile", "/home/jeff/workspace/jmesa/src/org/jmesa/model/TableModel.java", "-methodusage", "setItems", "-linenumber", "144"};
            
            Modifiers modifiers = new Modifiers(dummy);
            
            Input input = getInput(modifiers);
            
            InputScanner inputScanner = new InputScanner(input);
            Method methodMatch = inputScanner.scan();
            
            MethodUsageScanner methodUsageScanner = new MethodUsageScanner(methodMatch, input);
            List<Usage> usages = methodUsageScanner.scan();

            for (Usage usage : usages) {
                System.out.println(usage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }

        System.exit(1);
    }
    
    private static Input getInput(Modifiers modifiers) {
        
        Input result = new Input();
        
        File[] sourceFiles = getSourceFiles(modifiers);
        result.setSourceFolders(sourceFiles);
        
        String indexesFolder = modifiers.getIndexesFolder();
        if (indexesFolder != null) {
            result.setIndexesFile(new File(indexesFolder, "projects.indexes"));
        }
        
        String usageFile = modifiers.getUsageFile();
        System.out.println("usageFile " + usageFile);
        File file = new File(usageFile);
        result.setUsageFile(file);
        
        String methodUsage = modifiers.getMethodUsage();
        System.out.println("methodUsage " + methodUsage);
        result.setMethodUsage(methodUsage);
        
        String classUsage = modifiers.getClassUsage();
        result.setClassUsage(classUsage);
        
        String lineNumber = modifiers.getLineNumber();
        System.out.println("lineNumber " + lineNumber);
        if (lineNumber != null) {
            result.setLineNumber(Integer.parseInt(lineNumber));
        }
        
        return result;
    }
    
    private static File[] getSourceFiles(Modifiers modifiers) {
        
        List<File> results = new ArrayList<File>();

        List<String> sourceFolders = modifiers.getSourceFolders();
        for (String sourceFolder : sourceFolders) {
            System.out.println("sourceFolder " + sourceFolder);
            List<File> files = UsageUtils.getFiles(sourceFolder);
            results.addAll(files);
        }
        
        return results.toArray(new File[results.size()]);
    }
}
