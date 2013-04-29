/*
 KorikUtils - Minecraft/Bukkit utilities to make config and commands quicker
 Copyright (C) 2013 Korikisulda

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package modreq.korik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Utils {

    public static String join(String[] a, String delimiter, Integer startIndex) {
        try {
            Collection<String> s = Arrays.asList(a);
            StringBuilder buffer = new StringBuilder();
            Iterator<String> iter = s.iterator();

            while (iter.hasNext()) {
                if (startIndex == 0) {
                    buffer.append(iter.next());
                    if (iter.hasNext()) {
                        buffer.append(delimiter);
                    }
                } else {
                    startIndex--;
                    iter.next();
                }
            }

            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String[] shorten(String[] a, int from, int to) {
        if (to >= a.length) {
            to = a.length - 1;
        }
        String[] b = new String[to - from];
        while (from <= to) {
            b[from] = a[from];
            from++;
        }
        return b;
    }

    public static String[] addInFront(String[] a, String b) {
        String[] buffer = new String[a.length + 1];
        buffer[0] = b;
        int i = 0;
        while (i < a.length) {
            buffer[i + 1] = a[i];
            i++;
        }

        return buffer;
    }

    public static String[] add(String[] a, String b) {
        a[a.length] = b;
        return a;
    }

    public static String[] stripArray(String[] array) {
        ArrayList<String> arguments = new ArrayList<String>();
        try {
            boolean b = false;
            for (String s : array) {
                if (!b) {
                    b = true;
                    continue;
                }
                arguments.add(s);
            }
        } catch (Exception e) {
        }

        return arguments.toArray(new String[arguments.size()]);
    }

    public static String sit(String iStr, char delimiter, int part) {
        if (part == 0) {
            if (!iStr.contains(String.valueOf(delimiter))) {
                return iStr;
            }
        } else {
            if (!iStr.contains(String.valueOf(delimiter))) {
                return "";
            }
        }
        if (part == 0) {
            return iStr.substring(0, (iStr.indexOf(delimiter, 0)));
        }
        return iStr.substring(iStr.indexOf(delimiter, 0) + 1, iStr.length());
    }
}