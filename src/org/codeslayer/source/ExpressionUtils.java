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

public class ExpressionUtils {
    
    public static String stripPathParameters(String path) {
        
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }
    
    public static String stripComments(String expression) {
        
        return expression.replaceAll("\"([^\"\\\\]*(\\.[^\"\\\\]*)*)\"", "");
    }
    
    public static String stripEnds(String expression) {
        
        if (expression.endsWith(".")) {
            return expression.substring(0, expression.length()-1);
        }
        
        return expression;
    }
    
//static gchar*
//strip_path_parameters (gchar *path)
//{
//  gchar *result;
//  GString *string;
//  int brace = 0;
//  
//  string = g_string_new ("");
//
//  for (; *path != '\0'; ++path)
//    {
//      if (*path == ')')
//        {
//          brace++;
//          if (brace == 1)
//            string = g_string_append_c (string, *path);
//          continue;
//        }
//      if (*path == '(')
//        {
//          brace--;
//          if (brace == 0)
//            string = g_string_append_c (string, *path);
//          continue;
//        }
//      
//      if (brace == 0)
//        string = g_string_append_c (string, *path);
//    }
//  
//  result = g_string_free (string, FALSE);
//  g_strstrip(result);
//    
//  return result;
//}
    
}
