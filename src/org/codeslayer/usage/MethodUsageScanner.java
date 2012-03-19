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
import com.sun.source.util.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MethodUsageScanner extends AbstractScanner {
    
    private final MethodMatch methodMatch;

    public MethodUsageScanner(MethodMatch methodMatch) {
    
        this.methodMatch = methodMatch;
    }
    
    public List<Usage> scan() 
            throws Exception {
        
        List<Usage> usages = new ArrayList<Usage>();

        try {
            JavacTask javacTask = getJavacTask(methodMatch.getSourceFolders());
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<Void, Void> scanner = new ClassScanner(compilationUnitTree, sourcePositions, usages);
                compilationUnitTree.accept(scanner, null);
            }            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return usages;
    }
    
    private class ClassScanner extends TreeScanner<Void, Void> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final List<Usage> usages;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, List<Usage> usages) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.usages = usages;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void arg1) {

            if (methodMatch.getName().toString().equals(memberSelectTree.getIdentifier().toString())) {

                Result result = new Result();
                
                memberSelectTree.accept(new ExpressionScanner(), result);
                
                System.out.println("count " + result.count);

                String packageName = getPackageName(compilationUnitTree);
                String className = getClassName(compilationUnitTree);
                
                Usage usage = new Usage();
                usage.setPackageName(packageName + "." + className);
                usage.setClassName(className);
                usage.setMethodName(methodMatch.getName());
                usage.setExpression(memberSelectTree.getExpression().toString());
                usage.setFile(new File(compilationUnitTree.getSourceFile().toUri().toString()));
                usage.setLineNumber(getLineNumber(compilationUnitTree, sourcePositions, memberSelectTree));
                
                usages.add(usage);
            }
         
            return super.visitMemberSelect(memberSelectTree, arg1);
        }
    }
    
    private class ExpressionScanner extends SimpleTreeVisitor<Result, Result> {

        @Override
        public Result visitMemberSelect(MemberSelectTree mst, Result result) {
            
            System.out.println("member: " + mst.getIdentifier());
            
            result.count++;
            
            ExpressionTree expression = mst.getExpression();
            return expression.accept(new ExpressionScanner(), result);
        }

        @Override
        public Result visitMethodInvocation(MethodInvocationTree methodInvocationTree, Result result) {
            
            List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
            
            for (ExpressionTree expressionTree : arguments) {
                System.out.println("arg: " + expressionTree.toString());
            }
            
            result.count++;

            ExpressionTree methodSelect = methodInvocationTree.getMethodSelect();
            return methodSelect.accept(new ExpressionScanner(), result);
        }

        @Override
        public Result visitIdentifier(IdentifierTree identifierTree, Result result) {
            
            System.out.println("identifier: " + identifierTree.toString());
            result.count++;
            
            return result;
        }
    }
    
    private class Result {
        
        public int count;
    }
}
