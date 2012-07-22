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
package org.codeslayer.usage.scanner;

import com.sun.source.tree.*;
import com.sun.source.util.SimpleTreeVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codeslayer.usage.domain.*;

/**
 * Walk a code path and identify what each part of the path contains. For instance given the path 
 * dao.getPresidents() it would identify that this has the IDENTIFIER dao and the MEMBER getPresidents.
 */
public class SymbolScanner extends SimpleTreeVisitor<SymbolManager, SymbolManager> {
    
    public SymbolManager visitNewClass(NewClassTree newClassTree, SymbolManager symbolManager) {
        
        addArgs(symbolManager, newClassTree.getArguments());

        symbolManager.addNewClass(newClassTree.getIdentifier().toString());
        return symbolManager;
    }
    
    @Override
    public SymbolManager visitIdentifier(IdentifierTree identifierTree, SymbolManager symbolManager) {
        
        symbolManager.addIdentifier(identifierTree.toString());
        return symbolManager;
    }

    @Override
    public SymbolManager visitMemberSelect(MemberSelectTree memberSelectTree, SymbolManager symbolManager) {
        
        symbolManager.addMember(memberSelectTree.getIdentifier().toString());
        
        ExpressionTree expression = memberSelectTree.getExpression();
        return expression.accept(new SymbolScanner(), symbolManager);
    }

    @Override
    public SymbolManager visitMethodInvocation(MethodInvocationTree methodInvocationTree, SymbolManager symbolManager) {

        addArgs(symbolManager, methodInvocationTree.getArguments());

        ExpressionTree methodSelect = methodInvocationTree.getMethodSelect();
        return methodSelect.accept(new SymbolScanner(), symbolManager);
    }
    
    private void addArgs(SymbolManager symbolManager, List<? extends ExpressionTree> arguments) {
        
        List<ExpressionTree> expressionTrees = new ArrayList<ExpressionTree>(arguments);
        
        if (expressionTrees == null || expressionTrees.isEmpty()) {
            return;
        }

        Collections.reverse(expressionTrees);

        for (ExpressionTree expressionTree : expressionTrees) {
            addArg(symbolManager, expressionTree);
        }
    }
    
    private void addArg(SymbolManager symbolManager, ExpressionTree expressionTree) {
        
        SymbolManager argsSymbolManager = new SymbolManager();
        expressionTree.accept(new SymbolScanner(), argsSymbolManager);
        Symbol symbolTree = argsSymbolManager.getSymbolTree();
        Arg arg = new Arg(symbolTree);
        symbolManager.addArg(arg);
    }
}
