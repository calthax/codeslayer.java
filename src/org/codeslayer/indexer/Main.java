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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
//            String sourcePath = "/home/jeff/workspace/jmesa/src";
//            Indexer indexer = new SourceIndexer(IndexerUtils.getSourceFiles(sourcePath));
//            File file = new File("/home/jeff/.codeslayer-dev/groups/java/indexes/jmesa.xml");
//            XmlFormatter formatter = new XmlFormatter();
            
            Indexer indexer = new JarIndexer("/home/jeff/workspace/jmesa/lib/joda-time-1.6.jar");
            File file = new File("/home/jeff/indexes/dependencies.txt");
            Formatter formatter = new TabFormatter();
            
            List<Index> indexes = indexer.createIndexes();
            if (indexes != null && indexes.size() > 0) {
                String results = formatter.format(indexes);
//                System.out.println(results);

                Writer out = new OutputStreamWriter(new FileOutputStream(file));
                try {
                    out.write(results);
                }
                finally {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Not able to generate the index.");
        }

        System.exit(1);
    }
}
