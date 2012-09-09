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
package org.codeslayer.navigate;

import java.io.File;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.Command;
import org.codeslayer.navigate.scanner.NavigateMethodScanner;
import org.codeslayer.navigate.scanner.NavigateScanner;
import org.codeslayer.source.HierarchyManager;
import org.codeslayer.source.ScopeContext;
import org.codeslayer.source.SourceUtils;

public class NavigateCommand implements Command<NavigateInput, Navigate> {
    
    private static Logger logger = Logger.getLogger(NavigateCommand.class);

    public Navigate execute(NavigateInput input) {
        
        try {
            File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
            HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);
            
            if (SourceUtils.isClass(input.getExpression())) {
                NavigateScanner navigateScanner = new NavigateScanner(input);
                ScopeContext scopeContext = navigateScanner.scan();                
                NavigateHandler navigateClassHandler = new NavigateHandler(input, hierarchyManager, scopeContext);
                Navigate navigate = navigateClassHandler.getNavigate();
                return navigate;
            }
            
            NavigateMethodScanner navigateScanner = new NavigateMethodScanner(hierarchyManager, input);
            Navigate navigate = navigateScanner.scan();
            return navigate;
        } catch (Exception e) {
            logger.error("Not able to execute navigate command", e);
        }
        
        return null;
    }
}
