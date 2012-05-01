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

import java.util.List;
import org.codeslayer.source.*;
import org.codeslayer.usage.UsageUtils;

public class ClassMatcher {
    
    private final HierarchyManager hierarchyManager;

    public ClassMatcher(HierarchyManager hierarchyManager) {
        
        this.hierarchyManager = hierarchyManager;
    }

    /**
     * Take the fully populated method and see if the className passed in has a match.
     */
    public boolean hasMatch(Method methodMatch, String className) {
        
        List<Hierarchy> hierarchyList1 = hierarchyManager.getHierarchyList(methodMatch.getKlass().getClassName());
        List<Hierarchy> hierarchyList2 = hierarchyManager.getHierarchyList(className);
        
        for (Hierarchy hierarchy2 : hierarchyList2) {
            
            List<Method> classMethods = UsageUtils.getClassMethodsByName(hierarchy2.getFilePath(), methodMatch.getName());
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
    
//    private boolean hasMatch(Klass klass1, Method method1, String className) {
//        
//        ClassScanner classScanner = new ClassScanner(hierarchyManager, className);
//        Klass klass = classScanner.scan();        
//        
//        List<Method> classMethods = UsageUtils.getClassMethodsByName(klass.getFilePath(), method.getName());
//        for (Method classMethod : classMethods) {
//            if (SourceUtils.methodsEqual(classMethod, method)) {
//                return true;
//            }
//        }
//        
//    }
}
