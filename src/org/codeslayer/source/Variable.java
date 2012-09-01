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

public class Variable {
    
    private final String name;
    private final String type;
    private final String simpleType;

    public Variable(String name, String type, String simpleType) {
     
        this.name = name;
        this.type = type;
        this.simpleType = simpleType;
    }

    public String getName() {
        
        return name;
    }

    public String getType() {
     
        return type;
    }

    public String getSimpleType() {
     
        return simpleType;
    }
}
