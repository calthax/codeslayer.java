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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.indexer.Index;
import org.codeslayer.indexer.IndexFactory;
import org.codeslayer.indexer.SourceIndexer;
import org.codeslayer.source.scanner.PositionResult;
import org.codeslayer.source.*;
import org.codeslayer.source.Symbol;
import org.codeslayer.source.scanner.SymbolHandler;
import org.codeslayer.source.scanner.SymbolScanner;

public class NavigateHandler {
    
    private static Logger logger = Logger.getLogger(NavigateHandler.class);
    
    private final PositionResult positionResult;

    public NavigateHandler(PositionResult positionResult) {
     
        this.positionResult = positionResult;
    }

    public Navigate getNavigateResult() {
        
        Tree tree = positionResult.getTree();
        
        if (tree instanceof IdentifierTree) {
            return getByIdentifierTree((IdentifierTree)tree);
        } else if (tree instanceof MemberSelectTree) {
            return getByMemberSelectTree((MemberSelectTree)tree);            
        }
        
        return null;
    }

    private Navigate getByIdentifierTree(IdentifierTree identifierTree) {
        
        ScopeTree scopeTree = positionResult.getScopeTree();
        CompilationUnitTree compilationUnitTree = positionResult.getCompilationUnitTree();
        HierarchyManager hierarchyManager = positionResult.getHierarchyManager();
        
        if (SourceUtils.isClass(identifierTree.getName().toString())) {
            String className = SourceUtils.getClassName(scopeTree, identifierTree.getName().toString());
            Hierarchy hierarchy = positionResult.getHierarchyManager().getHierarchy(className);
            if (hierarchy == null) {
                return null;
            }
            String filePath = hierarchy.getFilePath();

            Navigate navigateOutput = new Navigate();
            navigateOutput.setFilePath(filePath);
            navigateOutput.setLineNumber(0);
            return navigateOutput;
        }
        
        Method staticMethod = SourceUtils.getStaticMethod(scopeTree, identifierTree.getName().toString());
        if (staticMethod != null) {
            createOutput(staticMethod);
        } 
        
        SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);

        Symbol firstSymbol = new Symbol(identifierTree.getName().toString());

        String className = symbolHandler.getType(firstSymbol, scopeTree);

        Method method = new Method();
        method.setName(identifierTree.getName().toString());
        Clazz clazz = new Clazz();
        clazz.setClassName(className);
        clazz.setSimpleClassName(SourceUtils.getSimpleType(className));
        clazz.addMethod(method);

        return createOutput(method);
    }
    
    private Navigate getByMemberSelectTree(MemberSelectTree memberSelectTree) {
        
        ScopeTree scopeTree = positionResult.getScopeTree();
        CompilationUnitTree compilationUnitTree = positionResult.getCompilationUnitTree();
        HierarchyManager hierarchyManager = positionResult.getHierarchyManager();

        Symbol symbol = memberSelectTree.getExpression().accept(new SymbolScanner(), null);
        if (symbol == null) {
            if (logger.isDebugEnabled()) {
                logger.error("symbol is null");
            }
            return null;
        }

        Symbol firstSymbol = SourceUtils.findFirstSymbol(symbol);

        SymbolHandler symbolHandler = new SymbolHandler(compilationUnitTree, hierarchyManager);                

        // assume this is a method of this class

        String className = symbolHandler.getType(firstSymbol, scopeTree);

        if (className == null) {
            return null;
        }

    //            if (!SourceUtils.hasMethodMatch(hierarchyManager, methodMatch, className)) {
    //                return scopeTree;
    //            }

        Method method = new Method();
        method.setName(memberSelectTree.getIdentifier().toString());
        Clazz clazz = new Clazz();
        clazz.setClassName(className);
        clazz.setSimpleClassName(SourceUtils.getSimpleType(className));
        clazz.addMethod(method);

        return createOutput(method);
    }
    
    private Navigate createOutput(Method method) {

        Navigate navigateOutput = new Navigate();

        IndexFactory indexFactory = new IndexFactory();
        List<Hierarchy> hierarchyList = positionResult.getHierarchyManager().getHierarchyList(method.getClazz().getClassName());
        List<File> files = getHierarchyFiles(hierarchyList);

        List<String> suppressions = Collections.emptyList();
        SourceIndexer sourceIndexer = new SourceIndexer(files, indexFactory, suppressions);

        try {
            List<Index> Indexes = sourceIndexer.createIndexes();
            for (Index index : Indexes) {
                String methodName = index.getMethodName();
                if (method.getName().equals(methodName)) {
                    navigateOutput.setFilePath(index.getFilePath());
                    navigateOutput.setLineNumber(index.getLineNumber());
                }                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return navigateOutput;
    }

    private List<File> getHierarchyFiles(List<Hierarchy> hierarchyList) {

        List<File> files = new ArrayList<File>();

        for (Hierarchy hierarchy : hierarchyList) {
            files.add(new File(hierarchy.getFilePath()));
        }

        return files;
    }
}
