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

import java.util.ArrayList;
import java.util.List;

public class UsageManager {
    
    private List<Usage> usages = new ArrayList<Usage>();

    public List<Usage> getUsages() {
     
        return usages;
    }
    
    public void addUsage(Usage usage) {

        for (Usage u : usages) {
            if (u.getClassName().equals(usage.getClassName()) &&
                    u.getLineNumber() == usage.getLineNumber() && 
                    u.getStartPosition() == usage.getStartPosition() &&
                    u.getEndPosition() == usage.getEndPosition()) {
                return;
            }
        }
        
        usages.add(usage);
    }
}
