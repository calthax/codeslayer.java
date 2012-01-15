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

import java.util.List;

public class Formatter {

    public String format(List<Index> indexes) {

        StringBuilder xml = new StringBuilder();

        xml.append("<indexes>");

        for (Index index : indexes) {
            xml.append("<index ");
            xml.append(" name=\"").append(escape(index.getName())).append("\"");
            xml.append(" parameters=\"").append(escape(index.getParameters())).append("\"");
            xml.append(" modifier=\"").append(escape(index.getModifier())).append("\"");
            xml.append(" line_number=\"").append(escape(index.getLineNumber())).append("\"");
            xml.append(" file_path=\"").append(escape(index.getFilePath())).append("\"");
            xml.append(" class_name=\"").append(escape(index.getClassName())).append("\"");
            xml.append(" package_name=\"").append(escape(index.getPackageName())).append("\"");
            xml.append("/>");
        }

        xml.append("</indexes>");

        return xml.toString();
    }

    private String escape(String value) {

        value = value.replaceAll("\"", "&quot;");
        value = value.replaceAll("'", "&apos;");
        value = value.replaceAll("\"", "&quot;");
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");
        value = value.replaceAll("&", "&amp;");
        return value;
    }
}
