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
package org.codeslayer.source.scanner;

import com.sun.source.tree.*;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.SourcePositions;
import org.apache.log4j.Logger;
import org.codeslayer.source.SourceUtils;

public class DebugScanner extends SimpleTreeVisitor<Void, Void> {
    
    private static Logger logger = Logger.getLogger(DebugScanner.class);
    
    private final CompilationUnitTree compilationUnitTree;
    private final SourcePositions sourcePositions;

    public DebugScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {
     
        this.compilationUnitTree = compilationUnitTree;
        this.sourcePositions = sourcePositions;
    }

    @Override
    public Void visitAnnotation(AnnotationTree tree, Void p) {
        
        super.visitAnnotation(tree, p);
        print(tree, "visitAnnotation", tree.toString());
        return p;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, Void p) {
        
        super.visitMethodInvocation(tree, p);
        print(tree, "visitMethodInvocation", tree.toString());
        return p;
    }

    @Override
    public Void visitAssert(AssertTree tree, Void p) {
        
        super.visitAssert(tree, p);
        print(tree, "visitAssert", tree.toString());
        return p;
    }

    @Override
    public Void visitAssignment(AssignmentTree tree, Void p) {
        
        super.visitAssignment(tree, p);
        print(tree, "visitAssignment", tree.toString());
        return p;
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree tree, Void p) {
        
        super.visitCompoundAssignment(tree, p);
        print(tree, "visitCompoundAssignment", tree.toString());
        return p;
    }

    @Override
    public Void visitBinary(BinaryTree tree, Void p) {
        
        super.visitBinary(tree, p);
        print(tree, "visitBinary", tree.toString());
        return p;
    }

    @Override
    public Void visitBlock(BlockTree tree, Void p) {
        
        super.visitBlock(tree, p);
        print(tree, "visitBlock", tree.toString());
        return p;
    }

    @Override
    public Void visitBreak(BreakTree tree, Void p) {
        
        super.visitBreak(tree, p);
        print(tree, "visitBreak", tree.toString());
        return p;
    }

    @Override
    public Void visitCase(CaseTree tree, Void p) {
        
        super.visitCase(tree, p);
        print(tree, "visitCase", tree.toString());
        return p;
    }

    @Override
    public Void visitCatch(CatchTree tree, Void p) {
        
        super.visitCatch(tree, p);
        print(tree, "visitCatch", tree.toString());
        return p;
    }

    @Override
    public Void visitClass(ClassTree tree, Void p) {
        
        super.visitClass(tree, p);
        print(tree, "visitClass", tree.toString());
        return p;
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree tree, Void p) {
        
        super.visitConditionalExpression(tree, p);
        print(tree, "visitConditionalExpression", tree.toString());
        return p;
    }

    @Override
    public Void visitContinue(ContinueTree tree, Void p) {
        
        super.visitContinue(tree, p);
        print(tree, "visitContinue", tree.toString());
        return p;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree tree, Void p) {
        
        super.visitDoWhileLoop(tree, p);
        print(tree, "visitDoWhileLoop", tree.toString());
        return p;
    }

    @Override
    public Void visitErroneous(ErroneousTree tree, Void p) {
        
        super.visitErroneous(tree, p);
        print(tree, "visitErroneous", tree.toString());
        return p;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree tree, Void p) {
        
        super.visitExpressionStatement(tree, p);
        print(tree, "visitExpressionStatement", tree.toString());
        return p;
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree tree, Void p) {
        
        super.visitEnhancedForLoop(tree, p);
        print(tree, "visitEnhancedForLoop", tree.toString());
        return p;
    }

    @Override
    public Void visitForLoop(ForLoopTree tree, Void p) {
        
        super.visitForLoop(tree, p);
        print(tree, "visitForLoop", tree.toString());
        return p;
    }

    @Override
    public Void visitIdentifier(IdentifierTree tree, Void p) {
        
        super.visitIdentifier(tree, p);
        print(tree, "visitIdentifier", tree.toString());
        return p;
    }

    @Override
    public Void visitIf(IfTree tree, Void p) {
        
        super.visitIf(tree, p);
        print(tree, "visitIf", tree.toString());
        return p;
    }

    @Override
    public Void visitImport(ImportTree tree, Void p) {
        
        super.visitImport(tree, p);
        print(tree, "visitImport", tree.toString());
        return p;
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree tree, Void p) {
        
        super.visitArrayAccess(tree, p);
        print(tree, "visitArrayAccess", tree.toString());
        return p;
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree tree, Void p) {
        
        super.visitLabeledStatement(tree, p);
        print(tree, "visitLabeledStatement", tree.toString());
        return p;
    }

    @Override
    public Void visitLiteral(LiteralTree tree, Void p) {
        
        super.visitLiteral(tree, p);
        print(tree, "visitLiteral", tree.toString());
        return p;
    }

    @Override
    public Void visitMethod(MethodTree tree, Void p) {
        
        super.visitMethod(tree, p);
        print(tree, "visitMethod", tree.toString());
        return p;
    }

    @Override
    public Void visitModifiers(ModifiersTree tree, Void p) {
        
        super.visitModifiers(tree, p);
        print(tree, "visitModifiers", tree.toString());
        return p;
    }

    @Override
    public Void visitNewArray(NewArrayTree tree, Void p) {
        
        super.visitNewArray(tree, p);
        print(tree, "visitNewArray", tree.toString());
        return p;
    }

    @Override
    public Void visitNewClass(NewClassTree tree, Void p) {
        
        super.visitNewClass(tree, p);
        print(tree, "visitNewClass", tree.toString());
        return p;
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree tree, Void p) {
        
        super.visitParenthesized(tree, p);
        print(tree, "visitParenthesized", tree.toString());
        return p;
    }

    @Override
    public Void visitReturn(ReturnTree tree, Void p) {
        
        super.visitReturn(tree, p);
        print(tree, "visitReturn", tree.toString());
        return p;
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree tree, Void p) {
        
        super.visitMemberSelect(tree, p);
        print(tree, "visitMemberSelect", tree.toString());
        return p;
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree tree, Void p) {
        
        super.visitEmptyStatement(tree, p);
        print(tree, "visitEmptyStatement", tree.toString());
        return p;
    }

    @Override
    public Void visitSwitch(SwitchTree tree, Void p) {
        
        super.visitSwitch(tree, p);
        print(tree, "visitSwitch", tree.toString());
        return p;
    }

    @Override
    public Void visitSynchronized(SynchronizedTree tree, Void p) {
        
        super.visitSynchronized(tree, p);
        print(tree, "visitSynchronized", tree.toString());
        return p;
    }

    @Override
    public Void visitThrow(ThrowTree tree, Void p) {
        
        super.visitThrow(tree, p);
        print(tree, "visitThrow", tree.toString());
        return p;
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree tree, Void p) {
        
        super.visitCompilationUnit(tree, p);
        print(tree, "visitCompilationUnit", tree.toString());
        return p;
    }

    @Override
    public Void visitTry(TryTree tree, Void p) {
        
        super.visitTry(tree, p);
        print(tree, "visitTry", tree.toString());
        return p;
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree tree, Void p) {
        
        super.visitParameterizedType(tree, p);
        print(tree, "visitParameterizedType", tree.toString());
        return p;
    }

    @Override
    public Void visitArrayType(ArrayTypeTree tree, Void p) {
        
        super.visitArrayType(tree, p);
        print(tree, "visitArrayType", tree.toString());
        return p;
    }

    @Override
    public Void visitTypeCast(TypeCastTree tree, Void p) {
        
        super.visitTypeCast(tree, p);
        print(tree, "visitTypeCast", tree.toString());
        return p;
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree tree, Void p) {
        
        super.visitPrimitiveType(tree, p);
        print(tree, "visitPrimitiveType", tree.toString());
        return p;
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree tree, Void p) {
        
        super.visitTypeParameter(tree, p);
        print(tree, "visitTypeParameter", tree.toString());
        return p;
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree tree, Void p) {
        
        super.visitInstanceOf(tree, p);
        print(tree, "visitInstanceOf", tree.toString());
        return p;
    }

    @Override
    public Void visitUnary(UnaryTree tree, Void p) {
        
        super.visitUnary(tree, p);
        print(tree, "visitUnary", tree.toString());
        return p;
    }

    @Override
    public Void visitVariable(VariableTree tree, Void p) {
        
        super.visitVariable(tree, p);
        print(tree, "visitVariable", tree.toString());
        return p;
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree tree, Void p) {
        
        super.visitWhileLoop(tree, p);
        print(tree, "visitWhileLoop", tree.toString());
        return p;
    }

    @Override
    public Void visitWildcard(WildcardTree tree, Void p) {
        
        super.visitWildcard(tree, p);
        print(tree, "visitWildcard", tree.toString());
        return p;
    }

    @Override
    public Void visitOther(Tree tree, Void p) {
        
        super.visitOther(tree, p);
        print(tree, "visitOther", tree.toString());
        return p;
    }
    
    private void print(Tree tree, String name, String value) {

        int lineNumber = SourceUtils.getLineNumber(compilationUnitTree, sourcePositions, tree);
        int startPosition = SourceUtils.getStartPosition(compilationUnitTree, sourcePositions, tree);
        int endPosition = SourceUtils.getEndPosition(compilationUnitTree, sourcePositions, tree);

        logger.debug("** " + name  + " - " + value + " **");
        logger.debug("lineNumber: " + lineNumber);
        logger.debug("startPosition: " + startPosition);
        logger.debug("endPosition: " + endPosition);
    }
    
}
