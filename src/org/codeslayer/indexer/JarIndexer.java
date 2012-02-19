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
package org.codeslayer.indexer;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarIndexer implements Indexer {

    private final File[] files;

    public JarIndexer(File[] jarFiles) {

        this.files = jarFiles;
    }

    public List<Index> createIndexes()
            throws Exception {
        
        List<Index> results = new ArrayList<Index>();
        
        for (File file : files) {
            String jarPath = file.getAbsolutePath();
            
            System.out.println(jarPath);
            
            JarFile jarFile = new JarFile(jarPath);

            URLClassLoader jarClassLoader = new URLClassLoader(new URL[]{new URL("jar:file:"+jarPath+"!/")}, this.getClass().getClassLoader());

            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
                    className = className.substring(0, className.length() - ".class".length());
                    try {
                        Class clazz = Class.forName(className.replace('/', '.'), true, jarClassLoader);
                        results.addAll(reflectOnClass(clazz));
                    } catch (Throwable t) {
                        t.printStackTrace();
                        System.err.println(t);
                    }
                }
            }
        }

        return results;
    }
    
    private List<Index> reflectOnClass(Class clazz) {
        
        List<Index> results = new ArrayList<Index>();
        
        for (Method method : clazz.getDeclaredMethods()) {

            Index index = new Index();

            index.setMethodName(method.getName());
            index.setMethodModifier(getModifier(method));
            
            String parameters = getParameters(method);
            index.setMethodParameters(parameters);
            index.setMethodCompletion(parameters);
            
            index.setMethodReturnType(method.getReturnType().getSimpleName());

            Class<?> declaringClass = method.getDeclaringClass();
            index.setPackageName(declaringClass.getPackage().getName() + "." + declaringClass.getSimpleName());
            index.setClassName(declaringClass.getSimpleName());

            results.add(index);
        }
        
        return results;
    }
    
    private String getParameters(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("(");
        
        Class<?>[] parameters = method.getParameterTypes();
        int length = parameters.length;
        
        for (int i = 0; i < length; i++) {
            Class parameter = parameters[i];
            sb.append(parameter.getSimpleName());
            
            if (i+1 < length) {
                sb.append(", ");
            }
        }

        sb.append(")");

        return sb.toString();
    }
    
    private String getModifier(Method method) {
        
        int modifiers = method.getModifiers();
        
        if (Modifier.isPublic(modifiers)) {
            return "public";
        }
        
        if (Modifier.isProtected(modifiers)) {
            return "protected";
        }
        
        return "package";
    }
}
