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
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;
import java.io.File;
import java.util.*;

public class InputScanner extends AbstractScanner {
    
    private final Input input;

    public InputScanner(Input input) {
     
        this.input = input;
    }
    
    public MethodMatch scan() 
            throws Exception {
        
        MethodMatch methodMatch = new MethodMatch();

        try {
            JavacTask javacTask = getJavacTask(new File[]{input.getUsageFile()});
            SourcePositions sourcePositions = Trees.instance(javacTask).getSourcePositions();
            Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
                TreeScanner<ScopeTree, ScopeTree> scanner = new ClassScanner(compilationUnitTree, sourcePositions, methodMatch);
                ScopeTree scopeTree = new ScopeTree();
                scopeTree.setPackageDeclaration(getPackageName(compilationUnitTree));
                compilationUnitTree.accept(scanner, scopeTree);
            }            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        
        return methodMatch;
    }
        
    private class ClassScanner extends TreeScanner<ScopeTree, ScopeTree> {

        private final CompilationUnitTree compilationUnitTree;
        private final SourcePositions sourcePositions;
        private final MethodMatch methodMatch;

        private ClassScanner(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, MethodMatch methodMatch) {

            this.compilationUnitTree = compilationUnitTree;
            this.sourcePositions = sourcePositions;
            this.methodMatch = methodMatch;
        }

        @Override
        public ScopeTree visitImport(ImportTree importTree, ScopeTree scopeTree) {
        
            super.visitImport(importTree, scopeTree);
            
            String importName = importTree.getQualifiedIdentifier().toString();
//            System.out.println("importName " + importName);
            
            scopeTree.addImportName(importName);
            
            return scopeTree;
        }

        @Override
        public ScopeTree visitVariable(VariableTree variableTree, ScopeTree scopeTree) {
            
            super.visitVariable(variableTree, scopeTree);
            
            String variable = variableTree.getType().toString();
            String name = variableTree.getName().toString();
//            System.out.println("variable " + variable);
//            System.out.println("name " + name);
            scopeTree.addVariable(variable, name);
            
            return scopeTree;                    
        }

        @Override
        public ScopeTree visitMethod(MethodTree methodTree, ScopeTree scopeTree) {
            
            super.visitMethod(methodTree, scopeTree);
            
            int lineNumber = getLineNumber(compilationUnitTree, sourcePositions, methodTree);
            if (matchesLineNumber(methodTree, lineNumber)) {
                String packageName = getPackageName(compilationUnitTree);
                String className = getClassName(compilationUnitTree);

                methodMatch.setPackageName(packageName + "." + className);
                methodMatch.setClassName(className);

                methodMatch.setLineNumber(lineNumber);
                methodMatch.setName(methodTree.getName().toString());
                methodMatch.setParameters(getParameters(methodTree, scopeTree));
                methodMatch.setSourceFolders(input.getSourceFolders());
            }
            
            return scopeTree;
        }
        
        private List<Parameter> getParameters(MethodTree methodTree, ScopeTree scopeTree) {
            
            List<Parameter> results = new ArrayList<Parameter>(); 

            Iterator<? extends VariableTree> iterator = methodTree.getParameters().iterator();
            while (iterator.hasNext()) {
                VariableTree variableTree = iterator.next();
                
                String type = variableTree.getType().toString();
                String name = variableTree.getName().toString();
                String packageName = getPackageNameByType(type, scopeTree);
                
                Parameter parameter = new Parameter();
                parameter.setType(type);
                parameter.setName(name);
                parameter.setPackageName(packageName);
                
                if (packageName != null) {
                    System.out.printf("packageName %s\n", packageName);
                }
                
                results.add(parameter);
            }
            
            return results;
        }
        
        private String getPackageNameByType(String type, ScopeTree scopeTree) {
            
            List<String> importNames = scopeTree.getImportNames();
            for (String importName : importNames) {
                if (importName.endsWith("." + type)) {
                    return importName;
                }
            }
            
            return scopeTree.getPackageDeclaration() + "." + type;
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
