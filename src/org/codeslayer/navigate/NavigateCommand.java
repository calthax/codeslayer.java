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
import org.codeslayer.source.HierarchyManager;
import org.codeslayer.source.ScopeContext;
import org.codeslayer.source.scanner.ScopeContextScanner;

public class NavigateCommand implements Command<NavigateInput, Navigate> {
    
    private static Logger logger = Logger.getLogger(NavigateCommand.class);

    public Navigate execute(NavigateInput input) {
        
        try {
            File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
            HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);
            
            ScopeContextScanner scopeContextScanner = new ScopeContextScanner(input);
            ScopeContext scopeContext = scopeContextScanner.scan();

            NavigateHandler navigateHandler = new NavigateHandler(input, hierarchyManager, scopeContext);
            Navigate navigate = navigateHandler.getNavigate();
            return navigate;
        } catch (Exception e) {
            logger.error("Not able to execute navigate command", e);
        }
        
        return null;
    }
}
