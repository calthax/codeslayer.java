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
package org.codeslayer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codeslayer.completion.CompletionCommand;
import org.codeslayer.completion.CompletionCommandWrapper;
import org.codeslayer.indexer.IndexerCommand;
import org.codeslayer.indexer.IndexerCommandWrapper;
import org.codeslayer.navigate.NavigateCommand;
import org.codeslayer.navigate.NavigateCommandWrapper;
import org.codeslayer.search.SearchCommand;
import org.codeslayer.search.SearchCommandWrapper;
import org.codeslayer.usage.UsageCommand;
import org.codeslayer.usage.UsageCommandWrapper;

public class CodeSlayerUtils {
    
    private static Logger logger = Logger.getLogger(CodeSlayerUtils.class);

    private static JavaFileFilter JAVA_FILE_FILTER = new JavaFileFilter();

    public static Map<String, Command> getPrograms() {
        
        Map<String, Command> programs = new HashMap<String, Command>();
        
        programs.put("usage", new UsageCommandWrapper(new UsageCommand()));
        programs.put("indexer", new IndexerCommandWrapper(new IndexerCommand()));
        programs.put("navigate", new NavigateCommandWrapper(new NavigateCommand()));
        programs.put("completion", new CompletionCommandWrapper(new CompletionCommand()));
        programs.put("search", new SearchCommandWrapper(new SearchCommand()));
        
        return programs;
    }
    
    public static Command getCommand(String[] args, Map<String, Command> programs) {
        
        for (int i = 0; i < args.length; i++) {
            String input = args[i];            
            if (input.equals("-program")) {
                String cmd = args[i+1];
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Program command is '" + cmd + "'");
                }
                
                return programs.get(cmd);
            }
        }
        
        throw new IllegalArgumentException("There is not a program argument defined");
    }
    
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
}
