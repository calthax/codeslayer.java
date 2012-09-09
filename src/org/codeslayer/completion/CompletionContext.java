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
package org.codeslayer.completion;

import com.sun.source.tree.CompilationUnitTree;
import org.codeslayer.source.ScopeTree;

public class CompletionContext {
    
    private ScopeTree scopeTree;
    private CompilationUnitTree compilationUnitTree;

    public ScopeTree getScopeTree() {
        
        return scopeTree;
    }

    public void setScopeTree(ScopeTree scopeTree) {
     
        this.scopeTree = scopeTree;
    }

    public CompilationUnitTree getCompilationUnitTree() {
        
        return compilationUnitTree;
    }

    public void setCompilationUnitTree(CompilationUnitTree compilationUnitTree) {
        
        this.compilationUnitTree = compilationUnitTree;
    }
}
