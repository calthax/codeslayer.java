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
import org.codeslayer.source.*;
import org.codeslayer.source.scanner.SymbolHandler;

public class CompletionHandler {
    
    private static Logger logger = Logger.getLogger(CompletionHandler.class);    
    
    private final CompletionInput input;
    private final HierarchyManager hierarchyManager;
    private final ScopeContext scopeContext;

    public CompletionHandler(CompletionInput input, HierarchyManager hierarchyManager, ScopeContext scopeContext) {
    
        this.input = input;
        this.hierarchyManager = hierarchyManager;
        this.scopeContext = scopeContext;
    }
    
    public List<Completion> getCompletions() {
        
        String type = input.getType();
        
        if (type.equals("method")) {
            return getMethodCompletions();
        }

        return getClassCompletions();
    }
    
    public List<Completion> getClassCompletions() {
     
        List<Completion> completions = new ArrayList<Completion>();
        
        File file = new File(input.getIndexesFolder(), "projects.classes");
        
        String expression = input.getExpression();
        
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
                
                String simpleClassName = split[0];
                if (simpleClassName.startsWith(expression)) {
                    Completion completion = new Completion();
                    completion.setSimpleClassName(simpleClassName);
                    completions.add(completion);
                }
            }
            in.close();
        } catch (Exception e) {
            logger.debug("Not able to get the class completions", e);
        }

        return completions;
    }
    
    public List<Completion> getMethodCompletions() {
        
        ScopeTree scopeTree = scopeContext.getScopeTree();

        Symbol symbol = getMethodSymbols();

        SymbolHandler symbolHandler = new SymbolHandler(scopeContext.getCompilationUnitTree(), hierarchyManager);

        String className = symbolHandler.getType(symbol, scopeTree);

        if (className == null) {
            return Collections.emptyList();
        }

        File indexesFile = new File(input.getIndexesFolder(), "projects.indexes");
        List<Completion> completions = createMethodCompletions(indexesFile, className);

        return completions;
    }
    
    private Symbol getMethodSymbols() {
        
        String expression = input.getExpression();
        expression = ExpressionUtils.stripSpecialCharacters(expression);
        
        logger.debug("expression " + expression);
        
        String[] values = expression.split("\\.");
        
        Symbol symbol = new Symbol(values[0]);
        
        if (values.length > 1) {
            for (int i = 1; i < values.length; i++) {
                String value = values[i];
                symbol.setNextSymbol(new Symbol(value));
            }            
        }
        
        return symbol;
    }
    
    private List<Completion> createMethodCompletions(File file, String className) {
        
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
                        completion.setMethodReturnType(split[8]);
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
