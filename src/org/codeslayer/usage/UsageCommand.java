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
package org.codeslayer.usage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.Command;
import org.codeslayer.indexer.HierarchyManager;
import org.codeslayer.source.Method;
import org.codeslayer.source.Parameter;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.usage.scanner.UsageInputScanner;
import org.codeslayer.usage.scanner.MethodUsageScanner;

public class UsageCommand implements Command<UsageInput, List<Usage>> {
    
    private static Logger logger = Logger.getLogger(UsageCommand.class);

    public List<Usage> execute(UsageInput input) {
        
        try {
            UsageInputScanner usageInputScanner = new UsageInputScanner(input);
            Method methodMatch = usageInputScanner.scan();
            
            if (methodMatch == null) {
                throw new IllegalStateException("the input scanner did not find the method");
            }
            
            File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
            HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);
            
            MethodUsageScanner methodUsageScanner = new MethodUsageScanner(hierarchyManager, methodMatch, input);
            List<Usage> usages = methodUsageScanner.scan();
            usages = filterUsages(hierarchyManager, methodMatch, usages);
            
            if (logger.isDebugEnabled()) {
                logger.debug("************ Usage Search Results ************");
            }
            
            for (Usage usage : usages) {
                logger.debug(usage.getClassName() + ":" + usage.getLineNumber() + " " + usage.getFile());
            }
            
            return usages;
        } catch (Exception e) {
            logger.debug("Not able to execute usage command", e);
        }
        
        return Collections.emptyList();
    }

    private static List<Usage> filterUsages(HierarchyManager hierarchyManager, Method methodMatch, List<Usage> usages) {
        
        List<Usage> results = new ArrayList<Usage>();
        
        if (logger.isDebugEnabled()) {
            logger.debug("************ Filter Usages ************");
        }

        List<Parameter> methodParameters = methodMatch.getParameters();
        
        if (methodParameters == null || methodParameters.isEmpty()) {
            return usages;
        }

        for (Usage usage : usages) {
            List<Parameter> usageParameters = usage.getMethod().getParameters();
            
            if (logger.isDebugEnabled()) {
                logger.debug(usage.getClassName() + ":" + usage.getLineNumber() + " " + usageParameters);            
            }

            if (usageParameters.size() != methodParameters.size()) {
                continue;
            }
            
            if (SourceUtils.parametersEqual(hierarchyManager, usageParameters, methodParameters)) {
                results.add(usage);
            }            
        }            
        
        return results;
    }
}
