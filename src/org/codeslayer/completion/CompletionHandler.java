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
package org.codeslayer.completion;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.source.ExpressionUtils;
import org.codeslayer.source.HierarchyManager;
import org.codeslayer.source.ScopeTree;
import org.codeslayer.source.SourceUtils;
import org.codeslayer.source.Symbol;
import org.codeslayer.source.scanner.SymbolHandler;

public class CompletionHandler {
    
    private static Logger logger = Logger.getLogger(CompletionHandler.class);    
    
    private final CompletionInput input;
    private final HierarchyManager hierarchyManager;
    private final CompletionContext completionContext;

    public CompletionHandler(CompletionInput input, HierarchyManager hierarchyManager, CompletionContext completionContext) {
    
        this.input = input;
        this.hierarchyManager = hierarchyManager;
        this.completionContext = completionContext;
    }
    
    public List<Completion> getCompletions() {
        
        String expression = input.getExpression();
        
        expression = ExpressionUtils.stripEnds(expression);
        
        logger.debug("expression " + expression);
        
        ScopeTree scopeTree = completionContext.getScopeTree();
        
        String simpleType = scopeTree.getSimpleType(expression);
        Symbol symbol = new Symbol(simpleType);

        SymbolHandler symbolHandler = new SymbolHandler(completionContext.getCompilationUnitTree(), hierarchyManager);

        String className = symbolHandler.getType(symbol, scopeTree);

        if (className == null) {
            return Collections.emptyList();
        }

        File indexesFile = new File(input.getIndexesFolder(), "projects.indexes");
        List<Completion> completions = createCompletions(indexesFile, className);

        return completions;
    }
    
    private List<Completion> createCompletions(File file, String className) {
        
        List<Completion> completions = new ArrayList<Completion>();

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

                    if (split[0].equals(className)) {
                        Completion completion = new Completion();
                        completion.setMethodName(split[3]);
                        completion.setMethodParameters(split[4]);
                        completion.setMethodParameterVariables(split[5]);
                        completion.setMethodReturnType(split[7]);
                        completions.add(completion);
                    }
                } else if (!completions.isEmpty()) {
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("not able to load the libs.indexes file.");
        }
        
        return completions;
    }    
}
