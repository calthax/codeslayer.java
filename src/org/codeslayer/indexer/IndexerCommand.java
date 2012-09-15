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

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.CodeSlayerUtils;
import org.codeslayer.Command;

public class IndexerCommand implements Command<IndexerInput, Boolean> {
    
    private static Logger logger = Logger.getLogger(IndexerCommand.class);

    @Override
    public Boolean execute(IndexerInput input) {
        
        try {
            List<String> suppressions = IndexerUtils.getSuppressions(input.getSuppressionsFile());
            IndexFactory indexFactory = new IndexFactory();
            
            List<Index> indexes = new ArrayList<Index>();
            
            List<String> sourceFolders = input.getSourceFolders();
            for (String sourceFolder : sourceFolders) {
                Indexer indexer = new SourceIndexer(CodeSlayerUtils.getFiles(sourceFolder), indexFactory, suppressions);
                indexes.addAll(indexer.createIndexes());
            }
            
            JarFilter jarFilter = new JarFilter();
            List<String> libFolders = input.getLibFolders();
            for (String libFolder : libFolders) {
                Indexer indexer = new JarIndexer(IndexerUtils.getJarFiles(libFolder, jarFilter), suppressions);
                indexes.addAll(indexer.createIndexes());
            }
            
            List<String> jarFiles = input.getJarFiles();
            for (String jarFile : jarFiles) {
                Indexer indexer = new JarIndexer(CodeSlayerUtils.getFiles(jarFile), suppressions);
                indexes.addAll(indexer.createIndexes());
            }
            
            List<String> zipFiles = input.getZipFiles();
            for (String zipFile : zipFiles) {
                String tmpFile = input.getTmpFolder();
                Indexer indexer = new SourceIndexer(IndexerUtils.getZipFiles(zipFile, tmpFile), indexFactory, suppressions);
                indexes.addAll(indexer.createIndexes());
            }
            
            if (indexes != null && !indexes.isEmpty()) {
                new IndexesFile(input.getIndexesFolder(), input.getType()).write(indexes);
                new ClassesFile(input.getIndexesFolder(), input.getType()).write(indexes);
                new HierarchyFile(input.getIndexesFolder(), input.getType()).write(indexes);
            }
        } catch (Exception e) {
            logger.error("Not able to execute indexer command", e);
        }
        
        return Boolean.TRUE;
    }
}
