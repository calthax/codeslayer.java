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
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.util.List;

public class MethodUsageScanner extends AbstractScanner {
    
    private final MethodMatch methodMatch;
    private final List<Usage> usages;

    public MethodUsageScanner(MethodMatch methodMatch, List<Usage> usages) {
    
        this.methodMatch = methodMatch;
        this.usages = usages;
    }
    
    protected TreeScanner<Void, Void> getScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {

        return new ClassScanner(compilationUnitTree, sourcePositions);
    }

    protected File[] getSourceFiles() {
        
        return methodMatch.getSourceFolders();
    }
    
    private class ClassScanner extends TreeScanner<Void, Void> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void arg1) {

            if (methodMatch.getName().toString().equals(memberSelectTree.getIdentifier().toString())) {

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
}
