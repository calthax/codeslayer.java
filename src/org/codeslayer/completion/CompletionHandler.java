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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import org.apache.log4j.Logger;
import org.codeslayer.source.*;
import org.codeslayer.source.scanner.PositionResult;
import org.codeslayer.source.Symbol;
import org.codeslayer.usage.Usage;
import org.codeslayer.source.scanner.SymbolHandler;
import org.codeslayer.source.scanner.SymbolScanner;

public class CompletionHandler {
 
    private static Logger logger = Logger.getLogger(CompletionHandler.class);
    
    private final PositionResult positionResult;

    public CompletionHandler(PositionResult positionResult) {
     
        this.positionResult = positionResult;
    }
    
    public Usage getUsage() {
        
        Tree tree = positionResult.getTree();
        
        if (tree instanceof MemberSelectTree) {
            return getUsageByMemberSelectTree((MemberSelectTree)tree);            
        }
        
        return null;
    }
    
    private Usage getUsageByMemberSelectTree(MemberSelectTree memberSelectTree) {
        
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

        return null;
    }    
}
