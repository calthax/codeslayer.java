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

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.SimpleTreeVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codeslayer.usage.domain.SymbolType;
import org.codeslayer.usage.domain.SymbolManager;

public class SymbolScanner extends SimpleTreeVisitor<SymbolManager, SymbolManager> {
    
    @Override
    public SymbolManager visitIdentifier(IdentifierTree identifierTree, SymbolManager symbolManager) {

        symbolManager.add(SymbolType.IDENTIFIER, identifierTree.toString());

        return symbolManager;
    }

    @Override
    public SymbolManager visitMemberSelect(MemberSelectTree memberSelectTree, SymbolManager symbolManager) {

        symbolManager.add(SymbolType.MEMBER, memberSelectTree.getIdentifier().toString());

        ExpressionTree expression = memberSelectTree.getExpression();
        return expression.accept(new SymbolScanner(), symbolManager);
    }

    @Override
    public SymbolManager visitMethodInvocation(MethodInvocationTree methodInvocationTree, SymbolManager symbolManager) {

        List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();

        List<ExpressionTree> args = new ArrayList<ExpressionTree>(arguments);

        Collections.reverse(args);

        for (ExpressionTree arg : args) {
            symbolManager.add(SymbolType.ARG, arg.toString());
        }

        ExpressionTree methodSelect = methodInvocationTree.getMethodSelect();
        return methodSelect.accept(new SymbolScanner(), symbolManager);
    }    
}
