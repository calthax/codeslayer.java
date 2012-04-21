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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codeslayer.source.Klass;
import org.codeslayer.source.Method;
import org.codeslayer.source.Parameter;

public class IndexFactory {
    
    private static final String REGEX = "import\\s+(.*?);";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private final String indexesFolder;

    public IndexFactory(String indexesFolder) {
     
        this.indexesFolder = indexesFolder;
    }
    
    public List<Index> createIndexes(List<Klass> indexClasses) {
        
        List<Index> indexes = new ArrayList<Index>();
        
        Map<String, Klass> projectsLookup = getProjectsLookup(indexClasses);
        Map<String, String> libsLookup = getLibsLookup();
        
        for (Klass indexClass : projectsLookup.values()) {
            String className = indexClass.getClassName();
            String simpleClassName = indexClass.getSimpleClassName();
            
            createIndexesForClass(projectsLookup, libsLookup, indexes, indexClass, className, simpleClassName);
        }
        
        return indexes;
    } 

    private void createIndexesForClass(Map<String, Klass> projectsLookup, Map<String, String> libsLookup, 
                                       List<Index> indexes, Klass klass, String className, String simpleClassName) {
        
        Klass superClass = getSuperClassIndex(projectsLookup, libsLookup, klass);
        if (superClass != null) {
            createIndexesForClass(projectsLookup, libsLookup, indexes, superClass, className, simpleClassName);
        }
        
        for (Method method : klass.getMethods()) {
            Index index = new Index();
            index.setClassName(className);
            index.setSimpleClassName(simpleClassName);
            index.setMethodModifier(method.getModifier());
            index.setMethodName(method.getName());
            index.setMethodParameters( getMethodParameters(method));
            index.setMethodParametersVariables(getMethodParametersVariables(method));
            index.setMethodParametersTypes(getMethodParametersTypes(method));
            index.setMethodReturnType(method.getReturnType());
            index.setMethodSimpleReturnType(method.getSimpleReturnType());
            index.setFilePath(klass.getFilePath());
            index.setLineNumber(String.valueOf(method.getLineNumber()));
            indexes.add(index);
        }
    }
    
    private String getMethodParameters(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<Parameter> iterator = method.getParameters().iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            String simpleType = parameter.getSimpleType();
            sb.append(simpleType).append(" ").append(parameter.getVariable());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    private String getMethodParametersVariables(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<Parameter> iterator = method.getParameters().iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            sb.append(parameter.getVariable());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    
    private String getMethodParametersTypes(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<Parameter> iterator = method.getParameters().iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            sb.append(parameter.getType());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    private Klass getSuperClassIndex(Map<String, Klass> projectsLookup, Map<String, String> libsLookup, Klass indexClass) {
        
        String superClass = indexClass.getSuperClass();
        
        if (superClass == null) {
            return null;
        }
        
        for (String imp : indexClass.getImports()) {
            Matcher matcher = PATTERN.matcher(imp);
            
            if (!matcher.find()) {
                continue;
            }
            
            String className = matcher.group(1);
            
            if (!className.endsWith("." + superClass)) {
                continue;
            }
            
            Klass project = projectsLookup.get(className);
            if (project != null) {
                return project;
            }
            
            String lib = libsLookup.get(className);
            if (lib != null) {
                File file = new File(indexesFolder, "libs.indexes");
                return IndexerUtils.getIndexClass(file, className);
            }
        }
        
        return null;
    }
    
    private Map<String, Klass> getProjectsLookup(List<Klass> klasses) {
        
        Map<String, Klass> results = new HashMap<String, Klass>();

        for (Klass klass : klasses) {
            results.put(klass.getClassName(), klass);
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
                String simpleClassName = split[0];
                String className = split[1];
                results.put(className, simpleClassName);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("not able to load the libs.classes file.");
        }

        return results;
    }
}
