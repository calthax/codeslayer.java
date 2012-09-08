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
import org.codeslayer.Command;
import org.codeslayer.Modifiers;

public class IndexerCommandWrapper implements Command<Modifiers, String> {
    
    private final IndexerCommand command;

    public IndexerCommandWrapper(IndexerCommand command) {
     
        this.command = command;
    }

    @Override
    public String execute(Modifiers modifiers) {
        
        IndexerInput input = command.getInput(modifiers);
        command.execute(input);
        return "success";
    }
    
    public IndexerInput getInput(Modifiers modifiers) {
        
        IndexerInput input = new IndexerInput();
        
        List<String> sourceFolders = modifiers.getSourceFolders();
        input.setSourceFolders(sourceFolders);
        
        List<String> libFolders = modifiers.getLibFolders();
        input.setLibFolders(libFolders);
        
        List<String> jarFiles = modifiers.getJarFiles();
        input.setJarFiles(jarFiles);
        
        List<String> zipFiles = modifiers.getZipFiles();
        input.setZipFiles(zipFiles);
        
        String indexesFolder = modifiers.getIndexesFolder();
        input.setIndexesFolder(indexesFolder);
        
        String suppressionsFile = modifiers.getSuppressionsFile();
        input.setSuppressionsFile(suppressionsFile);
        
        String tmpFolder = modifiers.getTmpFolder();
        input.setTmpFolder(tmpFolder);
        
        String type = modifiers.getType();
        input.setType(type);
        
        return input;
    }
}
