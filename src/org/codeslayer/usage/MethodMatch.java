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

import java.util.HashMap;
import java.util.Map;

public class MethodMatch extends Match {
    
    private String name;
    private Map<String, String> parameters = new HashMap<String, String>();

    public String getName() {
        
        return name;
    }

    public void setName(String name) {
     
        this.name = name;
    }
    
    public Map<String, String> getParameters() {
        
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
     
        this.parameters = parameters;
    }
}
