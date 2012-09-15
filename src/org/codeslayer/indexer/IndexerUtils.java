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

    public static List<File> getJarFiles(String path, JarFilter jarFilter) {
        
        List<File> files = new ArrayList<File>();

        File file = new File(path);
        
        for (File f : file.listFiles()) {
            if (jarFilter.accept(f)) {
                files.add(f);
            }
        }

        return files;
    }

    public static List<File> getZipFiles(String path, String tmpPath) {
        
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
        
        return files;
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
                
                int length = split.length;
                
                Hierarchy hierarchy = new Hierarchy();
                hierarchy.setClassName(split[0]);
                hierarchy.setSuperClass(split[1]);
                
                if (length > 2) {
                    hierarchy.setInterfaces(getInterfaces(split[2]));                    
                }

                if (length > 3) {
                    hierarchy.setFilePath(split[3]);                    
                }
                
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
}
