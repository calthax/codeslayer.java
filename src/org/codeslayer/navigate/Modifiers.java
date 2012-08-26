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
package org.codeslayer.navigate;

import java.util.*;

public class Modifiers {

    private Map<Modifier, String> modifiers = new HashMap<Modifier, String>();

    public Modifiers(String args[]) {

        Modifier lastModifier = null;

        Iterator<String> iterator = Arrays.asList(args).iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();

            Modifier modifier = Modifier.getModifierByKey(arg);
            if (modifier != null) {
                lastModifier = modifier;
            }

            String text = modifiers.get(lastModifier);
            if (text == null) {
                modifiers.put(lastModifier, "");
            } else {
                String value = text + " " + arg;
                modifiers.put(lastModifier, value.trim());
            }
        }
    }

    public List<String> getSourceFolders() {

        String sourceFolder = modifiers.get(Modifier.SOURCEFOLDER);
        if (sourceFolder != null) {
            String[] split = sourceFolder.split(":");
            return Arrays.asList(split);
        }

        return Collections.emptyList();
    }

    public String getIndexesFolder() {

        return modifiers.get(Modifier.INDEXESFOLDER);
    }

    public String getSourceFile() {

        return modifiers.get(Modifier.SOURCEFILE);
    }

    public String getSymbol() {

        return modifiers.get(Modifier.SYMBOL);
    }

    public String getLineNumber() {

        return modifiers.get(Modifier.LINENUMBER);
    }

    @Override
    public String toString() {

        return modifiers.toString();
    }

    private enum Modifier {

        PROGRAM("-program"),
        SOURCEFOLDER("-sourcefolder"),
        INDEXESFOLDER("-indexesfolder"),
        LINENUMBER("-linenumber"),
        SOURCEFILE("-sourcefile"),
        SYMBOL("-symbol");

        private final String key;

        private Modifier(String key) {

            this.key = key;
        }

        public static Modifier getModifierByKey(String key) {

            for (Modifier value : values()) {
                if (value.key.equals(key)) {
                    return value;
                }
            }

            return null;
        }
    }
}
