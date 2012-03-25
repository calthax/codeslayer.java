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
import com.sun.source.util.TreeScanner;

public class LoggingTreeScanner extends TreeScanner<Void, Void> {

    @Override
    public Void visitAnnotation(AnnotationTree at, Void p) {
        
        System.out.println("visitAnnotation " + at.toString());
        return super.visitAnnotation(at, p);
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree aat, Void p) {
        
        System.out.println("visitArrayAccess " + aat.toString());
        return super.visitArrayAccess(aat, p);
    }

    @Override
    public Void visitArrayType(ArrayTypeTree att, Void p) {
        
        System.out.println("visitArrayType " + att.toString());
        return super.visitArrayType(att, p);
    }

    @Override
    public Void visitAssert(AssertTree at, Void p) {
        
        System.out.println("visitAssert " + at.toString());
        return super.visitAssert(at, p);
    }

    @Override
    public Void visitAssignment(AssignmentTree at, Void p) {
        
        System.out.println("visitAssignment " + at.toString());
        return super.visitAssignment(at, p);
    }

    @Override
    public Void visitBinary(BinaryTree bt, Void p) {
        
        System.out.println("visitBinary " + bt.toString());
        return super.visitBinary(bt, p);
    }

    @Override
    public Void visitBlock(BlockTree bt, Void p) {
        
        System.out.println("visitBlock " + bt.toString());
        return super.visitBlock(bt, p);
    }

    @Override
    public Void visitBreak(BreakTree bt, Void p) {
        
        System.out.println("visitBreak " + bt.toString());
        return super.visitBreak(bt, p);
    }

    @Override
    public Void visitCase(CaseTree ct, Void p) {
        
        System.out.println("visitCase " + ct.toString());
        return super.visitCase(ct, p);
    }

    @Override
    public Void visitCatch(CatchTree ct, Void p) {
       
        System.out.println("visitCatch " + ct.toString());
        return super.visitCatch(ct, p);
    }

    @Override
    public Void visitClass(ClassTree ct, Void p) {
        
        System.out.println("visitClass " + ct.toString());
        return super.visitClass(ct, p);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree cut, Void p) {
        
        System.out.println("visitCompilationUnit " + cut.toString());
        return super.visitCompilationUnit(cut, p);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree cat, Void p) {
        
        System.out.println("visitCompoundAssignment " + cat.toString());
        return super.visitCompoundAssignment(cat, p);
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree cet, Void p) {
        
        System.out.println("visitConditionalExpression " + cet.toString());
        return super.visitConditionalExpression(cet, p);
    }

    @Override
    public Void visitContinue(ContinueTree ct, Void p) {
        
        System.out.println("visitContinue " + ct.toString());
        return super.visitContinue(ct, p);
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree dwlt, Void p) {
        
        System.out.println("visitDoWhileLoop " + dwlt.toString());
        return super.visitDoWhileLoop(dwlt, p);
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree est, Void p) {
       
        System.out.println("visitEmptyStatement " + est.toString());
        return super.visitEmptyStatement(est, p);
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree eflt, Void p) {
        
        System.out.println("visitEnhancedForLoop " + eflt.toString());
        return super.visitEnhancedForLoop(eflt, p);
    }

    @Override
    public Void visitErroneous(ErroneousTree et, Void p) {
       
        System.out.println("visitErroneous " + et.toString());
        return super.visitErroneous(et, p);
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree est, Void p) {
        
        System.out.println("visitExpressionStatement " + est.toString());
        return super.visitExpressionStatement(est, p);
    }

    @Override
    public Void visitForLoop(ForLoopTree flt, Void p) {
       
        System.out.println("visitForLoop " + flt.toString());
        return super.visitForLoop(flt, p);
    }

    @Override
    public Void visitIdentifier(IdentifierTree it, Void p) {
        
        System.out.println("visitIdentifier " + it.toString());
        return super.visitIdentifier(it, p);
    }

    @Override
    public Void visitIf(IfTree iftree, Void p) {
       
        System.out.println("visitIf " + iftree.toString());
        return super.visitIf(iftree, p);
    }

    @Override
    public Void visitImport(ImportTree it, Void p) {
       
        System.out.println("visitImport " + it.toString());
        return super.visitImport(it, p);
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree iot, Void p) {
        
        System.out.println("visitInstanceOf " + iot.toString());
        return super.visitInstanceOf(iot, p);
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree lst, Void p) {
        
        System.out.println("visitLabeledStatement " + lst.toString());
        return super.visitLabeledStatement(lst, p);
    }

    @Override
    public Void visitLiteral(LiteralTree lt, Void p) {
       
        System.out.println("visitLiteral " + lt.toString());
        return super.visitLiteral(lt, p);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree mst, Void p) {
       
        System.out.println("visitMemberSelect " + mst.toString());
        return super.visitMemberSelect(mst, p);
    }

    @Override
    public Void visitMethod(MethodTree mt, Void p) {
        
        System.out.println("visitMethod " + mt.toString());
        return super.visitMethod(mt, p);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree mit, Void p) {
        
        System.out.println("visitMethodInvocation " + mit.toString());
        return super.visitMethodInvocation(mit, p);
    }

    @Override
    public Void visitModifiers(ModifiersTree mt, Void p) {
        
        System.out.println("visitModifiers " + mt.toString());
        return super.visitModifiers(mt, p);
    }

    @Override
    public Void visitNewArray(NewArrayTree nat, Void p) {
        
        System.out.println("visitNewArray " + nat.toString());
        return super.visitNewArray(nat, p);
    }

    @Override
    public Void visitNewClass(NewClassTree nct, Void p) {
        
        System.out.println("visitNewClass " + nct.toString());
        return super.visitNewClass(nct, p);
    }

    @Override
    public Void visitOther(Tree tree, Void p) {
        
        System.out.println("visitOther " + tree.toString());
        return super.visitOther(tree, p);
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree ptt, Void p) {
        
        System.out.println("visitParameterizedType " + ptt.toString());
        return super.visitParameterizedType(ptt, p);
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree pt, Void p) {
        
        System.out.println("visitParenthesized " + pt.toString());
        return super.visitParenthesized(pt, p);
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree ptt, Void p) {
        
        System.out.println("visitPrimitiveType " + ptt.toString());
        return super.visitPrimitiveType(ptt, p);
    }

    @Override
    public Void visitReturn(ReturnTree rt, Void p) {
        
        System.out.println("visitReturn " + rt.toString());
        return super.visitReturn(rt, p);
    }

    @Override
    public Void visitSwitch(SwitchTree st, Void p) {
       
        System.out.println("visitSwitch " + st.toString());
        return super.visitSwitch(st, p);
    }

    @Override
    public Void visitSynchronized(SynchronizedTree st, Void p) {
        
        System.out.println("visitSynchronized " + st.toString());
        return super.visitSynchronized(st, p);
    }

    @Override
    public Void visitThrow(ThrowTree tt, Void p) {
        
        System.out.println("visitThrow " + tt.toString());
        return super.visitThrow(tt, p);
    }

    @Override
    public Void visitTry(TryTree tt, Void p) {
        
        System.out.println("visitTry " + tt.toString());
        return super.visitTry(tt, p);
    }

    @Override
    public Void visitTypeCast(TypeCastTree tct, Void p) {
        
        System.out.println("visitTypeCast " + tct.toString());
        return super.visitTypeCast(tct, p);
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree tpt, Void p) {
        
        System.out.println("visitTypeParameter " + tpt.toString());
        return super.visitTypeParameter(tpt, p);
    }

    @Override
    public Void visitUnary(UnaryTree ut, Void p) {
        
        System.out.println("visitUnary " + ut.toString());
        return super.visitUnary(ut, p);
    }

    @Override
    public Void visitVariable(VariableTree vt, Void p) {
        
        System.out.println("visitVariable " + vt.toString());
        return super.visitVariable(vt, p);
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree wlt, Void p) {
        
        System.out.println("visitWhileLoop " + wlt.toString());
        return super.visitWhileLoop(wlt, p);
    }

    @Override
    public Void visitWildcard(WildcardTree wt, Void p) {
     
        System.out.println("visitWildcard " + wt.toString());
        return super.visitWildcard(wt, p);
    }
}
