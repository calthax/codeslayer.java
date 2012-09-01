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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.codeslayer.source.*;

public class IndexerUtils {

    private static JavaFileFilter JAVA_FILE_FILTER = new JavaFileFilter();

    public static List<String> getSuppressions(String path) {

        if (path == null || path.trim().length() == 0) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<String>();

        try{
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine == null || strLine.trim().length() == 0) {
                    continue;
                }
                results.add(strLine);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("not able to load the suppressions file.");
        }

        return results;
    }
    
    public static boolean includePackage(List<String> suppressions, String packageName) {
        
        for (String suppression : suppressions) {
            if (packageName.startsWith(suppression)) {
                return false;
            }
        }
        
        return true;
    }

    public static File[] getFiles(String path) {

        List<File> files = new ArrayList<File>();

        File file = new File(path);
        walkFileTree(file, files);

        return files.toArray(new File[files.size()]);
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
    
    public static File[] getJarFiles(String path, JarFilter jarFilter) {
        
        List<File> files = new ArrayList<File>();

        File file = new File(path);
        
        for (File f : file.listFiles()) {
            if (jarFilter.accept(f)) {
                files.add(f);
            }
        }

        return files.toArray(new File[files.size()]);
    }

    public static File[] getZipFiles(String path, String tmpPath) {
        
        List<File> files = new ArrayList<File>();
        
        File tmpFile = new File(tmpPath);
        if (tmpFile.exists()) {
            deleteTmpFolder(tmpFile);
        }
        tmpFile.mkdir();

        try {
            ZipFile zipFile = new ZipFile(path);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                File file = new File(tmpPath, zipEntry.getName());
                
                if (zipEntry.isDirectory()) {
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    continue;
                }
                
                if (!zipEntry.getName().endsWith(".java")) {
                    continue;
                }

                file.createNewFile();

                InputStream inputStream = zipFile.getInputStream(zipEntry);
                OutputStream out = new FileOutputStream(file);

                int len;
                byte buf[] = new byte[1024];
                while ((len = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.close();
                inputStream.close();

                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return files.toArray(new File[files.size()]);
    }
    
    private static void deleteTmpFolder(File file) {
        
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File child : listFiles) {
                deleteTmpFolder(child);
            }
        }        
        file.delete();
    }
    
    public static Clazz findClassByIndexFile(File file, String className) {

        Clazz clazz = null;

        try{
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine == null || strLine.trim().length() == 0) {
                    continue;
                }
                
                if (strLine.startsWith(className)) {
                    String[] split = strLine.split("\\t");

                    if (clazz == null) {
                        clazz = new Clazz();
                        clazz.setClassName(split[0]);
                        clazz.setSimpleClassName(split[1]);
                    }
                    
                    Method method = new Method();
                    method.setModifier(split[2]);
                    method.setName(split[3]);
                    method.setParameters(getParameters(split));
                    method.setReturnType(split[7]);
                    method.setSimpleReturnType(split[8]);
                    
                    clazz.addMethod(method);
                } else if (clazz != null) {
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("not able to load the libs.indexes file.");
        }

        return clazz;
    }
    
    public static HierarchyManager loadHierarchyFile(File file) {

        HierarchyManager hierarchyManager = new HierarchyManager();

        try{
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine == null || strLine.trim().length() == 0) {
                    continue;
                }

                String[] split = strLine.split("\\t");
                Hierarchy hierarchy = new Hierarchy();
                hierarchy.setClassName(split[0]);
                hierarchy.setSuperClass(split[1]);
                hierarchy.setInterfaces(getInterfaces(split[2]));
                hierarchy.setFilePath(split[3]);
                hierarchyManager.addHierarchy(hierarchy);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("not able to load the libs.indexes file.");
        }

        return hierarchyManager;
    }
    
    private static List<String> getInterfaces(String interfaces) {
        
        List<String> results = new ArrayList<String>();
        
        if (interfaces == null || interfaces.isEmpty()) {
            return results;
        }
        
        for (String interfaceName : interfaces.split(",")) {
            results.add(interfaceName.trim());
        }
        
        return results;
    }
    
    private static List<Parameter> getParameters(String[] split) {
        
        List<Parameter> results = new ArrayList<Parameter>();
        
        if (split[4] == null || split[4].length() == 0) {
            return results;
        }
        
        String[] parameters = split[4].split(",");
        String[] parametersTypes = split[6].split(",");
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter result = new Parameter();
            
            String parameter = parameters[i];
            String[] args = parameter.split("\\s");
            
            String simpleType;
            String variable;

            if (args.length == 1) {
                simpleType = args[0];
                variable = args[0];
            } else {
                simpleType = args[0];
                variable = args[1];                
            }            
            
            result.setVariable(variable);
            result.setSimpleType(simpleType);
            
            if (SourceUtils.isPrimative(simpleType)) {
                result.setType(simpleType);
            } else {
                result.setType(parametersTypes[i]);
            }
        }
        
        return results;
    }
}
