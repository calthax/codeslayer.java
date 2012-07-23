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
import static org.codeslayer.usage.domain.SymbolType.*;

/**
 * Walk a code path and identify what each part of the path contains. For instance given the path 
 * dao.getPresidents() it would identify that this has the IDENTIFIER dao and the MEMBER getPresidents.
 */
public class SymbolScanner extends SimpleTreeVisitor<Symbol, Void> {
    
    public Symbol visitNewClass(NewClassTree newClassTree, Void p) {
        
        super.visitNewClass(newClassTree, p);
        
        Symbol newClass = new Symbol(NEW_CLASS, newClassTree.getIdentifier().toString());
        
        addArgs(newClass, newClassTree.getArguments());
        
        return newClass;
    }
    
    @Override
    public Symbol visitIdentifier(IdentifierTree identifierTree, Void p) {
        
        super.visitIdentifier(identifierTree, p);
        
        Symbol result = new Symbol(IDENTIFIER, identifierTree.getName().toString());
        return result;
    }

    @Override
    public Symbol visitMemberSelect(MemberSelectTree memberSelectTree, Void p) {
        
        super.visitMemberSelect(memberSelectTree, p);

        Symbol member = new Symbol(MEMBER, memberSelectTree.getIdentifier().toString());
        
        ExpressionTree expression = memberSelectTree.getExpression();
        Symbol result = expression.accept(new SymbolScanner(), p);

        result.setNextSymbol(member);
        
        return member;
    }

    @Override
    public Symbol visitMethodInvocation(MethodInvocationTree methodInvocationTree, Void p) {

        super.visitMethodInvocation(methodInvocationTree, p);

        ExpressionTree methodSelect = methodInvocationTree.getMethodSelect();
        Symbol result = methodSelect.accept(new SymbolScanner(), p);

        addArgs(result, methodInvocationTree.getArguments());
        
        return result;
    }
    
    private void addArgs(Symbol symbol, List<? extends ExpressionTree> arguments) {
        
        List<ExpressionTree> expressionTrees = new ArrayList<ExpressionTree>(arguments);
        
        if (expressionTrees == null || expressionTrees.isEmpty()) {
            return;
        }

        Collections.reverse(expressionTrees);

        for (ExpressionTree expressionTree : expressionTrees) {
            addArg(symbol, expressionTree);
        }
    }
    
    private void addArg(Symbol symbol, ExpressionTree expressionTree) {
        
        Symbol result = expressionTree.accept(new SymbolScanner(), null);

        Arg arg = new Arg(result);
        symbol.addArg(arg);
    }
}
