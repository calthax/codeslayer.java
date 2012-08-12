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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.source.*;
import org.codeslayer.usage.domain.Usage;

public class UsageUtils {
    
    private static Logger logger = Logger.getLogger(UsageUtils.class);

    private static JavaFileFilter JAVA_FILE_FILTER = new JavaFileFilter();

    public static List<File> getFiles(String path) {

        List<File> files = new ArrayList<File>();

        File file = new File(path);
        walkFileTree(file, files);

        return files;
    }

    private static void walkFileTree(File file, List<File> files) {

        if (file.isFile()) {
            files.add(file);
        }

        File[] children = file.listFiles(JAVA_FILE_FILTER);
        if (children != null) {
            for (File child : children) {
                walkFileTree(child, files);
            }
        }
    }

    private static class JavaFileFilter implements FileFilter {

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            
            if (file.isHidden()) {
                return false;
            }

            try {
                if (!(file.getAbsolutePath().equals(file.getCanonicalPath()))) {
                    return false;
                }
            } catch (Exception e) {
                // cannot do anything
            }
            
            String name = file.getName();
            return name.endsWith(".java");
        }
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
}
