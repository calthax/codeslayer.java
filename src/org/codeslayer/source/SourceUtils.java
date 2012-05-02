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
package org.codeslayer.source;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.tools.*;
import org.codeslayer.usage.UsageUtils;

public class SourceUtils {
    
    private SourceUtils() {}
    
    public static JavacTask getJavacTask(File[] files)
            throws Exception {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(files);
        return (JavacTask) compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
    }

    public static String getPackageName(CompilationUnitTree compilationUnitTree) {

        ExpressionTree expressionTree = compilationUnitTree.getPackageName();
        return expressionTree.toString();
    }

    public static String getClassName(CompilationUnitTree compilationUnitTree) {

        return getPackageName(compilationUnitTree) + "." + getSimpleClassName(compilationUnitTree);
    }

    public static String getSimpleClassName(CompilationUnitTree compilationUnitTree) {

        FileObject sourceFile = compilationUnitTree.getSourceFile();
        String className = sourceFile.getName().toString();
        return className.substring(0, className.length()-5);
    }
    
    public static List<String> getImports(CompilationUnitTree compilationUnitTree) {

        List<String> results = new ArrayList<String>();

        for (ImportTree importTree : compilationUnitTree.getImports()) {
            results.add(importTree.toString());
        }

        return results;
    }

    public static String getSuperClass(ClassTree classTree, ScopeTree scopeTree) {

        Tree tree = classTree.getExtendsClause();
        if (tree == null) {
            return "java.lang.Object";
        }

        return SourceUtils.getClassName(scopeTree, tree.toString());
    }

    public static List<String> getInterfaces(ClassTree classTree, ScopeTree scopeTree) {

        List<String> results = new ArrayList<String>();

        for (Tree implementsTree : classTree.getImplementsClause()) {
            results.add(SourceUtils.getClassName(scopeTree, implementsTree.toString()));
        }

        return results;
    }

    public static String getModifier(MethodTree methodTree) {

        ModifiersTree modifiersTree = methodTree.getModifiers();
        Set<Modifier> flags = modifiersTree.getFlags();
        for (Modifier modifier : flags) {
            return modifier.toString();
        }

        return "package";
    }

    public static List<Parameter> getParameters(MethodTree methodTree, ScopeTree scopeTree) {

        List<Parameter> parameters = new ArrayList<Parameter>();

        for (VariableTree variableTree : methodTree.getParameters()) {
            String simpleType = variableTree.getType().toString();
            String variable = variableTree.getName().toString();

            Parameter parameter = new Parameter();
            parameter.setVariable(variable);
            parameter.setSimpleType(simpleType);
            
            if (SourceUtils.isPrimative(simpleType)) {
                parameter.setType(simpleType);
            } else {
                parameter.setType(SourceUtils.getClassName(scopeTree, simpleType));
            }

            parameters.add(parameter);
        }

        return parameters;
    }

    public static String getSimpleReturnType(MethodTree methodTree) {

        Tree simpleReturnType = methodTree.getReturnType();
        if (simpleReturnType == null) {
            return "void";
        }

        return simpleReturnType.toString();
    }

    public static String getSourceFilePath(CompilationUnitTree compilationUnitTree) {

        FileObject sourceFile = compilationUnitTree.getSourceFile();
        return sourceFile.toUri().getPath();
    }

    public static File getSourceFile(CompilationUnitTree compilationUnitTree) {

        return new File(compilationUnitTree.getSourceFile().toUri().toString());
    }

    public static int getLineNumber(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Tree tree) {

        long startPosition = sourcePositions.getStartPosition(compilationUnitTree, tree);
        LineMap lineMap = compilationUnitTree.getLineMap();
        return (int)lineMap.getLineNumber(startPosition);
    }

    public static int getStartPosition(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Tree tree) {

        return (int)sourcePositions.getStartPosition(compilationUnitTree, tree);
    }
    
    public static int getEndPosition(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Tree tree) {

        return (int)sourcePositions.getEndPosition(compilationUnitTree, tree);
    }
    
    public static boolean isPrimative(String simpleType) {
        
        if (simpleType == null || simpleType.length() == 0) {
            return false;
        }
        
        return Character.isLowerCase(simpleType.toCharArray()[0]);
    }
    
    public static boolean isClass(String simpleType) {
        
        if (simpleType == null || simpleType.length() == 0) {
            return false;
        }
        
        return Character.isUpperCase(simpleType.toCharArray()[0]);
    }
    
    public static String getSimpleType(String type) {
        
        int index = type.lastIndexOf(".");
        if (index < 0) {
            return type;
        }
        
        return type.substring(index + 1);
    }
    
    public static String removeGenerics(String simpleType) {
        
        int index = simpleType.indexOf("<");
        if (index < 0) {
            return simpleType;
        }
        
        return simpleType.substring(0, index);
    }
    
    /*
     * This is flawed in that it will only find a class if it is in the imports.
     */
    public static String getClassName(ScopeTree scopeTree, String simpleType) {
        
        if (isPrimative(simpleType)) {
            return simpleType;
        }
        
        String name = removeGenerics(simpleType);
        
        if (name.equals("String")) {
            return "java.lang.String";
        } else if (name.equals("Object")) {
            return "java.lang.Object";
        } else if (name.equals("Collection")) {
            return "java.lang." + simpleType;
        }
        
        for (String importName : scopeTree.getImportNames()) {
            if (importName.endsWith("." + name)) {
                return importName;
            }
        }

        return scopeTree.getPackageName() + "." + simpleType;
    }
    
    public static Method findClassMethod(Klass klass, Method method) {
        
        for (Method klassMethod : klass.getMethods()) {
            if (methodsEqual(klassMethod, method)) {
                return klassMethod;
            }                        
        }

        throw new IllegalStateException("Class method not found for " + klass.getClassName() + "." + method.getName());
    }
    
    public static boolean hasMethodMatch(HierarchyManager hierarchyManager, Method methodMatch, String className) {
        
        for (Hierarchy hierarchy : hierarchyManager.getHierarchyList(className)) {
            
            List<Method> classMethods = UsageUtils.getClassMethodsByName(hierarchy.getFilePath(), methodMatch.getName());
            for (Method classMethod : classMethods) {
                
                if (!classMethod.getKlass().getClassName().equals(methodMatch.getKlass().getClassName())) {
                    continue;
                }                
                
                if (SourceUtils.methodsEqual(classMethod, methodMatch)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    public static boolean classesEqual(Klass klass1, Method method1, Klass klass2, Method method2) {
        
        if (!klass1.getClassName().equals(klass2.getClassName())) {
            return false;
        }
        
        return methodsEqual(method1, method2);
    }

    public static boolean methodsEqual(Method method1, Method method2) {
        
        if (!method1.getName().equals(method2.getName())) {
            return false;
        }
        
        return parametersEqual(method1.getParameters(), method2.getParameters());
    }
    
    public static boolean parametersEqual(List<Parameter> parameters1, List<Parameter> parameters2) {
        
        Iterator<Parameter> iteration1 = parameters1.iterator();
        Iterator<Parameter> iteration2 = parameters2.iterator();

        while (iteration1.hasNext() && iteration2.hasNext()) {
            Parameter usageParameter = iteration1.next();
            Parameter methodParameter = iteration2.next();
            String type1 = SourceUtils.removeGenerics(usageParameter.getType());
            String type2 = SourceUtils.removeGenerics(methodParameter.getType());
            
            if (!type1.equals(type2)) {
                return false;
            }
        }

        return true;
    }
}
