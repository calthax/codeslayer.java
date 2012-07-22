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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codeslayer.source.*;
import org.codeslayer.usage.domain.Symbol;
import org.codeslayer.usage.domain.Usage;
import org.codeslayer.usage.scanner.MethodScanner;

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
            
            if (SourceUtils.parametersEqual(usageParameters, methodParameters)) {
                results.add(usage);
            }            
        }            
        
        return results;
    }
    
    public static List<Method> getClassMethodsByName(String filePath, String methodName) {
        
        try {
            File file = new File(filePath);            
            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{file});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                MethodScanner methodScanner = new MethodScanner(compilationUnitTree, sourcePositions, methodName);                
                ScopeTreeFactory scopeTreeFactory = new ScopeTreeFactory(compilationUnitTree);
                ScopeTree scopeTree = scopeTreeFactory.createScopeTree();
                compilationUnitTree.accept(methodScanner, scopeTree);
                return methodScanner.getScanResults();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return Collections.emptyList();
    }
    
    public static Symbol findFirstSymbol(Symbol symbol) {
        
        Symbol prevSymbol = symbol.getPrevSymbol();
        if (prevSymbol != null) {
            return findFirstSymbol(prevSymbol);
        }
        
        return symbol;
    }
}
