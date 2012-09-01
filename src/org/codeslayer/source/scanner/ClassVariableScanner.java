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
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.codeslayer.source.*;
import org.codeslayer.source.Variable;

public class ClassVariableScanner {
    
    private static Logger logger = Logger.getLogger(ClassVariableScanner.class);

    private final HierarchyManager hierarchyManager;
    private final String className;

    public ClassVariableScanner(HierarchyManager hierarchyManager, String className) {
     
        this.hierarchyManager = hierarchyManager;
        this.className = className;
    }
    
    public List<Variable> scan() {
        
        try {
            Hierarchy hierarchy = hierarchyManager.getHierarchy(className);
            if (hierarchy == null) {
                return null;
            }
            
            File file = new File(hierarchy.getFilePath());
            
            List<Variable> variables = new ArrayList<Variable>();

            JavacTask javacTask = SourceUtils.getJavacTask(new File[]{file});
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                InternalScanner internalScanner = new InternalScanner(variables);
                ScopeTree scopeTree = ScopeTree.newScopeTree(compilationUnitTree);
                compilationUnitTree.accept(internalScanner, scopeTree);
                return variables;
            }            
        } catch (Exception e) {
            logger.error("class variable scan error", e);
        }
        
        return null;
    }
    
    private class InternalScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final List<Variable> variables;

        public InternalScanner(List<Variable> variables) {

            this.variables = variables;
        }

        @Override
        public ScopeTree visitImport(ImportTree importTree, ScopeTree scopeTree) {

            super.visitImport(importTree, scopeTree);

            String importName = importTree.getQualifiedIdentifier().toString();
            Import impt = new Import(importName, importTree.isStatic());
            scopeTree.addImport(impt);

            return scopeTree;
        }

        @Override
        public ScopeTree visitVariable(VariableTree variableTree, ScopeTree scopeTree) {

            super.visitVariable(variableTree, scopeTree);

            String simpleType = variableTree.getType().toString();
            String variable = variableTree.getName().toString();
            scopeTree.addSimpleType(variable, simpleType);
            
            String type = SourceUtils.getClassName(scopeTree, simpleType);
            
            variables.add(new Variable(variable, type, simpleType));

            return scopeTree;                    
        }
    }
}
