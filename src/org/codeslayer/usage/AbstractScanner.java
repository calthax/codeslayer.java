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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import java.io.File;
import javax.tools.*;

public abstract class AbstractScanner {
    
    protected JavacTask getJavacTask(File[] files)
            throws Exception {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(files);
        return (JavacTask) compiler.getTask(null, fileManager, diagnosticsCollector, null, null, fileObjects);
    }
    
    protected String getPackageName(CompilationUnitTree compilationUnitTree) {

        ExpressionTree expressionTree = compilationUnitTree.getPackageName();
        return expressionTree.toString();
    }

    protected String getClassName(CompilationUnitTree compilationUnitTree) {

        FileObject sourceFile = compilationUnitTree.getSourceFile();
        String className = sourceFile.getName().toString();
        return className.substring(0, className.length()-5);
    }

    protected int getLineNumber(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Tree tree) {

        long startPosition = sourcePositions.getStartPosition(compilationUnitTree, tree);
        LineMap lineMap = compilationUnitTree.getLineMap();
        return (int)lineMap.getLineNumber(startPosition);
    }

    protected int getStartPosition(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Tree tree) {

        return (int)sourcePositions.getStartPosition(compilationUnitTree, tree);
    }
    
    protected int getEndPosition(CompilationUnitTree compilationUnitTree, SourcePositions sourcePositions, Tree tree) {

        return (int)sourcePositions.getEndPosition(compilationUnitTree, tree);
    }
}
