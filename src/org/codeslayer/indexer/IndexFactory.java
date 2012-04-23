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

import java.util.*;
import org.codeslayer.source.Klass;
import org.codeslayer.source.Method;
import org.codeslayer.source.Parameter;

public class IndexFactory {
    
    public List<Index> createIndexes(List<Klass> klasses) {
        
        List<Index> indexes = new ArrayList<Index>();
        for (Klass klass : klasses) {
            createIndexesForClass(indexes, klass);
        }
        return indexes;
    } 

    private void createIndexesForClass(List<Index> indexes, Klass klass) {

        for (Method method : klass.getMethods()) {
            Index index = new Index();
            index.setClassName(klass.getClassName());
            index.setSimpleClassName(klass.getSimpleClassName());
            index.setSuperClassName(klass.getSuperClassName());
            index.setMethodModifier(method.getModifier());
            index.setMethodName(method.getName());
            index.setMethodParameters( getMethodParameters(method));
            index.setMethodParametersVariables(getMethodParametersVariables(method));
            index.setMethodParametersTypes(getMethodParametersTypes(method));
            index.setMethodReturnType(method.getReturnType());
            index.setMethodSimpleReturnType(method.getSimpleReturnType());
            index.setFilePath(klass.getFilePath());
            index.setLineNumber(String.valueOf(method.getLineNumber()));
            indexes.add(index);
        }
    }
    
    private String getMethodParameters(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<Parameter> iterator = method.getParameters().iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            String simpleType = parameter.getSimpleType();
            sb.append(simpleType).append(" ").append(parameter.getVariable());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    private String getMethodParametersVariables(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<Parameter> iterator = method.getParameters().iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            sb.append(parameter.getVariable());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    
    private String getMethodParametersTypes(Method method) {
        
        StringBuilder sb = new StringBuilder();
        
        Iterator<Parameter> iterator = method.getParameters().iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            sb.append(parameter.getType());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
}
