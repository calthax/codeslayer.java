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
package org.codeslayer.indexer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codeslayer.indexer.domain.IndexClass;
import org.codeslayer.indexer.domain.IndexMethod;

public class IndexFactory {
    
    private static final String REGEX = "import\\s+(.*?);";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private final String indexesFolder;

    public IndexFactory(String indexesFolder) {
     
        this.indexesFolder = indexesFolder;
    }
    
    public List<Index> createIndexes(List<IndexClass> indexClasses) {
        
        List<Index> indexes = new ArrayList<Index>();
        
        Map<String, IndexClass> projectsLookup = getProjectsLookup(indexClasses);
        Map<String, String> libsLookup = getLibsLookup();
        
        for (IndexClass indexClass : projectsLookup.values()) {
            String packageName = indexClass.getPackageName();
            String className = indexClass.getClassName();
            
            createIndexesForClass(projectsLookup, libsLookup, indexes, indexClass, packageName, className);
        }
        
        return indexes;
    } 

    private void createIndexesForClass(Map<String, IndexClass> projectsLookup, Map<String, String> libsLookup, 
                                       List<Index> indexes, IndexClass indexClass, String packageName, String className) {
        
        IndexClass superClass = getSuperClassIndex(projectsLookup, libsLookup, indexClass);
        if (superClass != null) {
            createIndexesForClass(projectsLookup, libsLookup, indexes, superClass, packageName, className);
        }
        
        for (IndexMethod indexMethod : indexClass.getMethods()) {
            Index index = new Index();
            index.setPackageName(packageName);
            index.setClassName(className);
            index.setMethodModifier(indexMethod.getModifier());
            index.setMethodName(indexMethod.getName());
            index.setMethodParameters(indexMethod.getParameters());
            index.setMethodCompletion(indexMethod.getCompletion());
            index.setMethodReturnType(indexMethod.getReturnType());
            index.setFilePath(indexClass.getFilePath());
            index.setLineNumber(indexMethod.getLineNumber());                
            indexes.add(index);
        }
    }
    
    private IndexClass getSuperClassIndex(Map<String, IndexClass> projectsLookup, Map<String, String> libsLookup, IndexClass indexClass) {
        
        String superClass = indexClass.getSuperClass();
        
        if (superClass == null) {
            return null;
        }
        
//        if (!superClass.equals("Column")) {
//            return null;
//        }
        
        for (String imp : indexClass.getImports()) {
            Matcher matcher = PATTERN.matcher(imp);
            
            if (!matcher.find()) {
                continue;
            }
            
            String packageName = matcher.group(1);
            
            if (!packageName.endsWith("." + superClass)) {
                continue;
            }
            
//            System.out.println(packageName);
            
            IndexClass project = projectsLookup.get(packageName);
            if (project != null) {
                System.out.println("found the project");
                return project;
            }
            
            String lib = libsLookup.get(packageName);
            if (lib != null) {
                System.out.println("found the lib");
                return getLibIndexClass(packageName);
            }
        }
        
        return null;
    }
    
    private IndexClass getLibIndexClass(String packageName) {

        IndexClass indexClass = null;

        try{
            File file = new File(indexesFolder, "libs.indexes");
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine == null || strLine.trim().length() == 0) {
                    continue;
                }
                
                if (strLine.startsWith(packageName)) {
                    System.out.println("strLine " + strLine);
                    
                    String[] split = strLine.split("\\t");
                    
                    if (indexClass == null) {
                        indexClass = new IndexClass();
                        indexClass.setPackageName(split[0]);
                        indexClass.setClassName(split[1]);
                        indexClass.setFilePath(split[6]);
                    }
                    
                    IndexMethod indexMethod = new IndexMethod();
                    indexMethod.setModifier(split[2]);
                    indexMethod.setParameters(split[3]);
                    indexMethod.setCompletion(split[4]);
                    indexMethod.setReturnType(split[5]);
                    indexClass.addMethod(indexMethod);
                } else if (indexClass != null) {
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("not able to load the suppressions file.");
        }

        return indexClass;
    }
    
    private Map<String, IndexClass> getProjectsLookup(List<IndexClass> indexClasses) {
        
        Map<String, IndexClass> results = new HashMap<String, IndexClass>();

        for (IndexClass indexClass : indexClasses) {
            results.put(indexClass.getPackageName(), indexClass);
        }        
        
        return results;
    }
    
    private Map<String, String> getLibsLookup() {

        Map<String, String> results = new HashMap<String, String>();

        try{
            File file = new File(indexesFolder, "libs.classes");
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine == null || strLine.trim().length() == 0) {
                    continue;
                }
                
                String[] split = strLine.split("\\t");
                String className = split[0];
                String packageName = split[1];
                results.put(packageName, className);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("not able to load the suppressions file.");
        }

        return results;
    }
}
