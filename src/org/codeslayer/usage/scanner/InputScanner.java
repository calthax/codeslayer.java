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
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.*;
import org.codeslayer.usage.domain.*;

public class InputScanner {
    
    private final Input input;

    public InputScanner(Input input) {
     
        this.input = input;
    }
    
    public MethodMatch scan() 
            throws Exception {
        
        try {
            JavacTask javacTask = ScannerUtils.getJavacTask(new File[]{input.getUsageFile()});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                List<MethodMatch> methodMatches = new ArrayList<MethodMatch>();
                MethodScanner methodScanner = new MethodScanner(compilationUnitTree, sourcePositions, input.getMethodUsage(), methodMatches);

                ScopeTree scopeTree = new ScopeTree();
                scopeTree.setPackageName(ScannerUtils.getPackageName(compilationUnitTree));
                compilationUnitTree.accept(methodScanner, scopeTree);
                
                for (MethodMatch methodMatch : methodMatches) {
                    if (methodMatch.getLineNumber() == input.getLineNumber()) {
                        return methodMatch;
                    }
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return null;
    }
}
