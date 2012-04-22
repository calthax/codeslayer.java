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
import org.codeslayer.source.Method;
import org.codeslayer.source.Parameter;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.usage.domain.Usage;

public class UsageUtils {

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
    
    public static List<Usage> filterUsages(List<Usage> usages, Method method) {
        
        List<Usage> results = new ArrayList<Usage>();
        
        List<Parameter> methodParameters = method.getParameters();
        
        System.out.println("methodParameters: " + methodParameters);
        
        if (methodParameters == null || methodParameters.isEmpty()) {
            return usages;
        }

        for (Usage usage : usages) {
            List<Parameter> usageParameters = usage.getMethod().getParameters();
            
            System.out.println("usageParameters: " + usageParameters);

            if (usageParameters.size() != methodParameters.size()) {
                continue;
            }
            
            if (SourceUtils.isParametersEqual(usageParameters, methodParameters)) {
                results.add(usage);
            }            
        }            
        
        return results;
    }
}
