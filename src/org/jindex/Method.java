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

public class Method {

    private String name;
    private String parameters;
    private String modifier;
    private String lineNumber;
    private String className;
    private String filePath;
    private String packageName;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getParameters() {

        return parameters;
    }

    public void setParameters(String parameters) {

        this.parameters = parameters;
    }

    public String getModifier() {

        return modifier;
    }

    public void setModifier(String modifier) {

        this.modifier = modifier;
    }

    public String getLineNumber() {

        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {

        this.lineNumber = lineNumber;
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {

        this.className = className;
    }

    public String getPackageName() {

        return packageName;
    }

    public void setPackageName(String packageName) {

        this.packageName = packageName;
    }

    public String getFilePath() {

        return filePath;
    }

    public void setFilePath(String filePath) {

        this.filePath = filePath;
    }
}
