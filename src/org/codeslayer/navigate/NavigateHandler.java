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

import org.codeslayer.indexer.HierarchyManager;
import org.codeslayer.indexer.Hierarchy;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.completion.CompletionHandler;
import org.codeslayer.indexer.Index;
import org.codeslayer.indexer.IndexFactory;
import org.codeslayer.indexer.SourceIndexer;
import org.codeslayer.source.*;
import org.codeslayer.source.scanner.SymbolHandler;

public class NavigateHandler {
    
    private static Logger logger = Logger.getLogger(CompletionHandler.class);    
    
    private final NavigateInput input;
    private final HierarchyManager hierarchyManager;
    private final ScopeContext scopeContext;

    public NavigateHandler(NavigateInput input, HierarchyManager hierarchyManager, ScopeContext scopeContext) {
    
        this.input = input;
        this.hierarchyManager = hierarchyManager;
        this.scopeContext = scopeContext;
    }
    
    public Navigate getNavigate() {
        
        Symbol symbol = getSymbols();
        
        if (symbol.getNextSymbol() == null) {
            
            return getByClass();
        }
        
        return getByMethod(symbol);
    }

    public Navigate getByClass() {
        
        ScopeTree scopeTree = scopeContext.getScopeTree();
        
        String className = SourceUtils.getClassName(scopeTree, input.getExpression());
        Hierarchy hierarchy = hierarchyManager.getHierarchy(className);
        
        if (hierarchy == null) {
            return null;
        }

        String filePath = hierarchy.getFilePath();

        Navigate navigate = new Navigate();
        navigate.setFilePath(filePath);
        navigate.setLineNumber(0);
        
        return navigate;
    }
    
    public Navigate getByMethod(Symbol symbol) {
        
        ScopeTree scopeTree = scopeContext.getScopeTree();

        Symbol lastSymbol = SourceUtils.findLastSymbol(symbol);
        String methodName = lastSymbol.getValue();
        
        Symbol prevSymbol = lastSymbol.getPrevSymbol();  
        if (prevSymbol == null) {
            return null;
        }
        
        prevSymbol.setNextSymbol(null);

        SymbolHandler symbolHandler = new SymbolHandler(scopeContext.getCompilationUnitTree(), hierarchyManager);

        String className = symbolHandler.getType(symbol, scopeTree);

        if (className == null) {
            return null;
        }
        
        Method method = new Method();
        method.setName(methodName);
        Clazz clazz = new Clazz();
        clazz.setClassName(className);
        clazz.setSimpleClassName(SourceUtils.getSimpleType(className));
        clazz.addMethod(method);

        return createNavigate(method);
    }
    
    public Symbol getSymbols() {
        
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

    private Navigate createNavigate(Method method) {

        Navigate navigate = new Navigate();

        IndexFactory indexFactory = new IndexFactory();
        List<Hierarchy> hierarchyList = hierarchyManager.getHierarchyList(method.getClazz().getClassName());
        List<File> files = getHierarchyFiles(hierarchyList);

        List<String> suppressions = Collections.emptyList();
        SourceIndexer sourceIndexer = new SourceIndexer(files, indexFactory, suppressions);

        try {
            List<Index> Indexes = sourceIndexer.createIndexes();
            for (Index index : Indexes) {
                String methodName = index.getMethodName();
                if (method.getName().equals(methodName)) {
                    navigate.setFilePath(index.getFilePath());
                    navigate.setLineNumber(index.getLineNumber());
                    break;
                }                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return navigate;
    }

    private List<File> getHierarchyFiles(List<Hierarchy> hierarchyList) {

        List<File> files = new ArrayList<File>();

        for (Hierarchy hierarchy : hierarchyList) {
            files.add(new File(hierarchy.getFilePath()));
        }

        return files;
    }    
}
