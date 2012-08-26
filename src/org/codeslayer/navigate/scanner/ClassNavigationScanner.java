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
package org.codeslayer.navigate.scanner;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.navigate.Input;
import org.codeslayer.source.*;
import org.codeslayer.usage.domain.Usage;
import org.codeslayer.usage.domain.Variable;

public class ClassNavigationScanner {
 
    private static Logger logger = Logger.getLogger(ClassNavigationScanner.class);
    
    private final HierarchyManager hierarchyManager;
    private final Input input;

    public ClassNavigationScanner(HierarchyManager hierarchyManager, Input input) {
    
        this.hierarchyManager = hierarchyManager;
        this.input = input;
    }
    
    public Usage scan() 
            throws Exception {

        try {
            
            File file = input.getSourceFile();
            
            List<Variable> variables = new ArrayList<Variable>();

            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{file});
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                InternalScanner internalScanner = new InternalScanner();
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(internalScanner, scopeTree);
                
                String className = SourceUtils.getClassName(scopeTree, input.getSymbol());
                Hierarchy hierarchy = hierarchyManager.getHierarchy(className);
                if (hierarchy == null) {
                    return null;
                }
                
                String filePath = hierarchy.getFilePath();
        
                Usage usage = new Usage();
                usage.setClassName(className);
                usage.setFile(new File(filePath));
                usage.setLineNumber(0);
                
                return usage;
            }            
        } catch (Exception e) {
            logger.error("class variable scan error", e);
        }
        
        return null;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        @Override
        public ScopeTree visitImport(ImportTree importTree, ScopeTree scopeTree) {

            super.visitImport(importTree, scopeTree);

            String importName = importTree.getQualifiedIdentifier().toString();
            Import impt = new Import(importName, importTree.isStatic());
            scopeTree.addImport(impt);

            return scopeTree;
        }
    }
}
