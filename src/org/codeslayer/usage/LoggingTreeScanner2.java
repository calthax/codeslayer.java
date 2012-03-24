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

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import java.util.List;

public class LoggingTreeScanner2 extends TreeScanner<Void, Void> {

    @Override
    public Void visitAnnotation(AnnotationTree at, Void p) {
        
        super.visitAnnotation(at, p);
        
        System.out.println("visitAnnotation " + at.toString());
        return p;
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree aat, Void p) {
        
        super.visitArrayAccess(aat, p);
        System.out.println("visitArrayAccess " + aat.toString());
        return p;
    }

    @Override
    public Void visitArrayType(ArrayTypeTree att, Void p) {
        
        super.visitArrayType(att, p);
        System.out.println("visitArrayType " + att.toString());
        return p;
    }

    @Override
    public Void visitAssert(AssertTree at, Void p) {
        
        super.visitAssert(at, p);
        System.out.println("visitAssert " + at.toString());
        return p;
    }

    @Override
    public Void visitAssignment(AssignmentTree at, Void p) {
        
        super.visitAssignment(at, p);
        System.out.println("visitAssignment " + at.toString());
        return p;
    }

    @Override
    public Void visitBinary(BinaryTree bt, Void p) {
        
        super.visitBinary(bt, p);
        System.out.println("visitBinary " + bt.toString());
        return p;
    }

    @Override
    public Void visitBlock(BlockTree bt, Void p) {
        
        super.visitBlock(bt, p);
        System.out.println("visitBlock " + bt.toString());
        return p;
    }

    @Override
    public Void visitBreak(BreakTree bt, Void p) {
        
        super.visitBreak(bt, p);
        System.out.println("visitBreak " + bt.toString());
        return p;
    }

    @Override
    public Void visitCase(CaseTree ct, Void p) {
        
        super.visitCase(ct, p);
        System.out.println("visitCase " + ct.toString());
        return p;
    }

    @Override
    public Void visitCatch(CatchTree ct, Void p) {
       
        super.visitCatch(ct, p);
        System.out.println("visitCatch " + ct.toString());
        return p;
    }

    @Override
    public Void visitClass(ClassTree ct, Void p) {
        
        super.visitClass(ct, p);
        System.out.println("visitClass " + ct.toString());
        return p;
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree cut, Void p) {
        
        super.visitCompilationUnit(cut, p);
        System.out.println("visitCompilationUnit " + cut.toString());
        return p;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree cat, Void p) {
        
        super.visitCompoundAssignment(cat, p);
        System.out.println("visitCompoundAssignment " + cat.toString());
        return p;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree cet, Void p) {
        
        super.visitConditionalExpression(cet, p);
        System.out.println("visitConditionalExpression " + cet.toString());
        return p;
    }

    @Override
    public Void visitContinue(ContinueTree ct, Void p) {
        
        super.visitContinue(ct, p);
        System.out.println("visitContinue " + ct.toString());
        return p;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree dwlt, Void p) {
        
        super.visitDoWhileLoop(dwlt, p);
        System.out.println("visitDoWhileLoop " + dwlt.toString());
        return p;
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree est, Void p) {
       
        super.visitEmptyStatement(est, p);
        System.out.println("visitEmptyStatement " + est.toString());
        return p;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree eflt, Void p) {
        
        super.visitEnhancedForLoop(eflt, p);
        System.out.println("visitEnhancedForLoop " + eflt.toString());
        return p;
    }

    @Override
    public Void visitErroneous(ErroneousTree et, Void p) {
       
        super.visitErroneous(et, p);
        System.out.println("visitErroneous " + et.toString());
        return p;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree est, Void p) {
        
        super.visitExpressionStatement(est, p);
        System.out.println("visitExpressionStatement " + est.toString());
        return p;
    }

    @Override
    public Void visitForLoop(ForLoopTree flt, Void p) {
       
        super.visitForLoop(flt, p);
        System.out.println("visitForLoop " + flt.toString());
        return p;
    }

    @Override
    public Void visitIdentifier(IdentifierTree it, Void p) {
        
        super.visitIdentifier(it, p);
        System.out.println("visitIdentifier " + it.toString());
        return p;
    }

    @Override
    public Void visitIf(IfTree iftree, Void p) {
       
        super.visitIf(iftree, p);
        System.out.println("visitIf " + iftree.toString());
        return p;
    }

    @Override
    public Void visitImport(ImportTree it, Void p) {
       
        super.visitImport(it, p);
        System.out.println("visitImport " + it.toString());
        return p;
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree iot, Void p) {
        
        super.visitInstanceOf(iot, p);
        System.out.println("visitInstanceOf " + iot.toString());
        return p;
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree lst, Void p) {
        
        super.visitLabeledStatement(lst, p);
        System.out.println("visitLabeledStatement " + lst.toString());
        return p;
    }

    @Override
    public Void visitLiteral(LiteralTree lt, Void p) {
       
        super.visitLiteral(lt, p);
        System.out.println("visitLiteral " + lt.toString());
        return p;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree mst, Void p) {
       
        super.visitMemberSelect(mst, p);
        System.out.println("visitMemberSelect " + mst.toString());
        return p;
    }

    @Override
    public Void visitMethod(MethodTree mt, Void p) {
        
        super.visitMethod(mt, p);
        System.out.println("visitMethod " + mt.toString());
        return p;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree mit, Void p) {
        
        super.visitMethodInvocation(mit, p);
        System.out.println("visitMethodInvocation " + mit.toString());
        
        List<? extends ExpressionTree> arguments = mit.getArguments();
        for (ExpressionTree expressionTree : arguments) {
            System.out.println("arg " + expressionTree.toString());
        }
        
        return p;
    }

    @Override
    public Void visitModifiers(ModifiersTree mt, Void p) {
        
        super.visitModifiers(mt, p);
        System.out.println("visitModifiers " + mt.toString());
        return p;
    }

    @Override
    public Void visitNewArray(NewArrayTree nat, Void p) {
        
        super.visitNewArray(nat, p);
        System.out.println("visitNewArray " + nat.toString());
        return p;
    }

    @Override
    public Void visitNewClass(NewClassTree nct, Void p) {
        
        super.visitNewClass(nct, p);
        System.out.println("visitNewClass " + nct.toString());
        return p;
    }

    @Override
    public Void visitOther(Tree tree, Void p) {
        
        super.visitOther(tree, p);
        System.out.println("visitOther " + tree.toString());
        return p;
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree ptt, Void p) {
        
        super.visitParameterizedType(ptt, p);
        System.out.println("visitParameterizedType " + ptt.toString());
        return p;
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree pt, Void p) {
        
        super.visitParenthesized(pt, p);
        System.out.println("visitParenthesized " + pt.toString());
        return p;
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree ptt, Void p) {
        
        super.visitPrimitiveType(ptt, p);
        System.out.println("visitPrimitiveType " + ptt.toString());
        return p;
    }

    @Override
    public Void visitReturn(ReturnTree rt, Void p) {
        
        super.visitReturn(rt, p);
        System.out.println("visitReturn " + rt.toString());
        return p;
    }

    @Override
    public Void visitSwitch(SwitchTree st, Void p) {
       
        super.visitSwitch(st, p);
        System.out.println("visitSwitch " + st.toString());
        return p;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree st, Void p) {
        
        super.visitSynchronized(st, p);
        System.out.println("visitSynchronized " + st.toString());
        return p;
    }

    @Override
    public Void visitThrow(ThrowTree tt, Void p) {
        
        super.visitThrow(tt, p);
        System.out.println("visitThrow " + tt.toString());
        return p;
    }

    @Override
    public Void visitTry(TryTree tt, Void p) {
        
        super.visitTry(tt, p);
        System.out.println("visitTry " + tt.toString());
        return p;
    }

    @Override
    public Void visitTypeCast(TypeCastTree tct, Void p) {
        
        super.visitTypeCast(tct, p);
        System.out.println("visitTypeCast " + tct.toString());
        return p;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree tpt, Void p) {
        
        super.visitTypeParameter(tpt, p);
        System.out.println("visitTypeParameter " + tpt.toString());
        return p;
    }

    @Override
    public Void visitUnary(UnaryTree ut, Void p) {
        
        super.visitUnary(ut, p);
        System.out.println("visitUnary " + ut.toString());
        return p;
    }

    @Override
    public Void visitVariable(VariableTree vt, Void p) {
        
        super.visitVariable(vt, p);
        System.out.println("visitVariable " + vt.toString());
        return p;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree wlt, Void p) {
        
        super.visitWhileLoop(wlt, p);
        System.out.println("visitWhileLoop " + wlt.toString());
        return p;
    }

    @Override
    public Void visitWildcard(WildcardTree wt, Void p) {
     
        super.visitWildcard(wt, p);
        System.out.println("visitWildcard " + wt.toString());
        return p;
    }
}
