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

import org.codeslayer.indexer.HierarchyManager;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.Hierarchy;
import org.codeslayer.indexer.IndexerUtils;
import org.codeslayer.source.*;
import org.codeslayer.source.scanner.SymbolHandler;

public class CompletionHandler {
    
    private static Logger logger = Logger.getLogger(CompletionHandler.class);    
    
    private final CompletionInput input;
    private final ScopeContext scopeContext;

    public CompletionHandler(CompletionInput input, ScopeContext scopeContext) {
    
        this.input = input;
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
            logger.error("Not able to get the class completions", e);
        }

        return completions;
    }
    
    public List<Completion> getMethodCompletions() {
        
        List<Completion> completions;
        
        ScopeTree scopeTree = scopeContext.getScopeTree();
        Symbol symbol = getMethodSymbols();
        
        completions = getProjectsCompletions(scopeTree, symbol);
        if (completions.isEmpty()) {
            completions = getLibsCompletions(scopeTree, symbol);            
        }
        
        Collections.sort(completions, new Comparator<Completion>() {
            @Override
            public int compare(Completion c1, Completion c2) {
                return c1.getMethodName().compareTo(c2.getMethodName());
            }
        });

        return completions;
    }
    
    private String getClassName(HierarchyManager hierarchyManager, ScopeTree scopeTree, Symbol symbol) {
        
        SymbolHandler symbolHandler = new SymbolHandler(scopeContext.getCompilationUnitTree(), hierarchyManager);

        return symbolHandler.getType(symbol, scopeTree);
    }
    
    private List<Completion> getProjectsCompletions(ScopeTree scopeTree, Symbol symbol) {
        
        File hierarchyFile = new File(input.getIndexesFolder(), "projects.hierarchy");
        HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);

        String className = getClassName(hierarchyManager, scopeTree, symbol);
        if (className == null) {
            return Collections.emptyList();
        }

        List<Completion> completions = new ArrayList<Completion>();
        
        if (completions.isEmpty()) {
            File projectsIndexesFile = new File(input.getIndexesFolder(), "projects.indexes");        
            for (Hierarchy hierarchy : hierarchyManager.getHierarchyList(className)) {
                completions.addAll(createMethodCompletions(projectsIndexesFile, hierarchy.getClassName()));
            }
        }
        
        return completions;
    }
    
    private List<Completion> getLibsCompletions(ScopeTree scopeTree, Symbol symbol) {
        
        List<Completion> completions = new ArrayList<Completion>();
        
        File hierarchyFile = new File(input.getIndexesFolder(), "libs.hierarchy");
        HierarchyManager hierarchyManager = IndexerUtils.loadHierarchyFile(hierarchyFile);

        String className = getClassName(hierarchyManager, scopeTree, symbol);
        if (className == null) {
            return Collections.emptyList();
        }

        if (completions.isEmpty()) {
            File projectsIndexesFile = new File(input.getIndexesFolder(), "libs.indexes");        
            for (Hierarchy hierarchy : hierarchyManager.getHierarchyList(className)) {
                completions.addAll(createMethodCompletions(projectsIndexesFile, hierarchy.getClassName()));
            }
        }
        
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
            logger.error("Not able to load the libs.indexes file", e);
        }
        
        return completions;
    }    
}
