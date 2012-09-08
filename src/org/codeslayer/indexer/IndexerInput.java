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

import java.util.List;

public class IndexerInput {
    
    private List<String> sourceFolders;
    private List<String> libFolders;
    private List<String> jarFiles;
    private List<String> zipFiles;
    private String suppressionsFile;
    private String tmpFolder;
    private String type;
    private String indexesFolder;

    public List<String> getSourceFolders() {
        
        return sourceFolders;
    }

    public void setSourceFolders(List<String> sourceFolders) {

        this.sourceFolders = sourceFolders;
    }

    public List<String> getLibFolders() {
        
        return libFolders;
    }

    public void setLibFolders(List<String> libFolders) {
     
        this.libFolders = libFolders;
    }

    public List<String> getJarFiles() {
        
        return jarFiles;
    }

    public void setJarFiles(List<String> jarFiles) {
     
        this.jarFiles = jarFiles;
    }

    public List<String> getZipFiles() {
        
        return zipFiles;
    }

    public void setZipFiles(List<String> zipFiles) {
     
        this.zipFiles = zipFiles;
    }

    public String getSuppressionsFile() {
        
        return suppressionsFile;
    }

    public void setSuppressionsFile(String suppressionsFile) {
        
        this.suppressionsFile = suppressionsFile;
    }

    public String getTmpFolder() {
        
        return tmpFolder;
    }

    public void setTmpFolder(String tmpFolder) {
     
        this.tmpFolder = tmpFolder;
    }
    
    public String getIndexesFolder() {
        
        return indexesFolder;
    }

    public void setIndexesFolder(String indexesFolder) {
     
        this.indexesFolder = indexesFolder;
    }
    
    public String getType() {
        
        return type;
    }

    public void setType(String type) {
     
        this.type = type;
    }
}
