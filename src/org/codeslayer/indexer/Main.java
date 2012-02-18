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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            Modifiers modifiers = new Modifiers(args);
            
            List<Index> indexes = new ArrayList<Index>();
            
            List<String> sourceFolders = modifiers.getSourceFolders();
            for (String sourceFolder : sourceFolders) {
                Indexer indexer = new SourceIndexer(IndexerUtils.getFiles(sourceFolder));
                indexes.addAll(indexer.createIndexes());
            }
            
            List<String> libFolders = modifiers.getLibFolders();
            for (String libFolder : libFolders) {
                Indexer indexer = new JarIndexer(IndexerUtils.getJarFiles(libFolder));
                indexes.addAll(indexer.createIndexes());
            }
            
            List<String> jarFiles = modifiers.getJarFiles();
            for (String jarFile : jarFiles) {
                Indexer indexer = new JarIndexer(IndexerUtils.getFiles(jarFile));
                indexes.addAll(indexer.createIndexes());
            }
            
            List<String> zipFiles = modifiers.getZipFiles();
            for (String zipFile : zipFiles) {
                String tmpFile = modifiers.getTmpFile();
                Indexer indexer = new SourceIndexer(IndexerUtils.getZipFiles(zipFile, tmpFile));
                indexes.addAll(indexer.createIndexes());
            }
            
            File file = new File(modifiers.getIndexFile());
            Formatter formatter = new Formatter();
            
            if (indexes != null && !indexes.isEmpty()) {
                String results = formatter.format(indexes);
                Writer out = new OutputStreamWriter(new FileOutputStream(file));
                try {
                    out.write(results);
                }
                finally {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }

        System.exit(1);
    }
}
