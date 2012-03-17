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
import com.sun.source.util.SourcePositions;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InputScanner extends AbstractScanner {
    
    private final Input input;
    private final MethodMatch methodMatch;

    public InputScanner(Input input, MethodMatch methodMatch) {
     
        this.input = input;
        this.methodMatch = methodMatch;
    }
    
    protected TreeScanner<Void, Void> getScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {
        
        return new ClassScanner(compilationUnitTree, sourcePositions);
    }
    
    protected File[] getSourceFiles() {
        
        return new File[]{input.getUsageFile()};
    }

    private class ClassScanner extends TreeScanner<Void, Void> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
        }

        @Override
        public Void visitClass(ClassTree classTree, Void arg1) {
            
            List<? extends Tree> members = classTree.getMembers();
            
            for (Tree memberTree : members) {
                if (!(memberTree instanceof MethodTree)) {
                    continue;
                }

                MethodTree methodTree = (MethodTree)memberTree;                    
                int lineNumber = getLineNumber(compilationUnitTree, sourcePositions, methodTree);
                
                if (!matchesLineNumber(methodTree, lineNumber)) {
                    continue;
                }
                
                String packageName = getPackageName(compilationUnitTree);
                String className = getClassName(compilationUnitTree);
                
                methodMatch.setPackageName(packageName + "." + className);
                methodMatch.setClassName(className);
                
                methodMatch.setLineNumber(lineNumber);
                methodMatch.setName(methodTree.getName().toString());
                methodMatch.setParameters(getParameters(methodTree));
                methodMatch.setSourceFolders(input.getSourceFolders());
            }

            return super.visitClass(classTree, arg1);
        }
        
        private Map<String, String> getParameters(MethodTree methodTree) {
            
            Map<String, String> results = new HashMap<String, String>(); 

            Iterator<? extends VariableTree> iterator = methodTree.getParameters().iterator();
            while (iterator.hasNext()) {
                VariableTree variableTree = iterator.next();
                results.put(variableTree.getType().toString(), variableTree.getName().toString());
            }
            
            return results;
        }

        private boolean matchesLineNumber(MethodTree methodTree, int lineNumber) {
            
            if (input.getLineNumber() == lineNumber && 
                    input.getMethodUsage().equals(methodTree.getName().toString())) {
                return true;
            }
            
            return false;
        }
    }
}
