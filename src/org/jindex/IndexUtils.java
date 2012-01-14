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
package org.jindex;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class IndexUtils {

    private static JavaFileFilter JAVA_FILE_FILTER = new JavaFileFilter();

    public static File[] getSourceFiles(String sourcePath) {

        List<File> files = new ArrayList<File>();

        String[] paths = sourcePath.split(",");
        for (String path : paths) {
            File file = new File(path);
            walkFileTree(file, files);
        }

        return files.toArray(new File[files.size()]);
    }

    private static void walkFileTree(File file, List<File> files) {

        if (file.isFile()) {
            files.add(file);
        }

        File[] children = file.listFiles(JAVA_FILE_FILTER);
        if (children != null) {
            for (File child : children) {
                walkFileTree(child, files);
            }
        }
    }

    private static class JavaFileFilter implements FileFilter {
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            if (file.isHidden()) {
                return false;
            }
            String name = file.getName();
            return name.endsWith(".java");
        }
    }
}
