/*
 Modreq Minecraft/Bukkit server ticket system
 Copyright (C) 2013 Sven Wiltink

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
package modreq;

public enum Status {

    OPEN(1, "open"),
    CLAIMED(2, "claimed"),
    CLOSED(3, "closed"),
    PENDING(4, "pending");
    String status;
    int type;

    private Status(int type, String status) {
        this.type = type;
        this.status = status;
    }

    public String getStatusString() {
        return status;
    }

    public int getType() {
        return type;
    }

    public static Status getByString(String string) {
        if (string.equals("open")) {
            return OPEN;
        }
        if (string.equals("claimed")) {
            return CLAIMED;
        }
        if (string.equals("closed")) {
            return CLOSED;
        }
        if (string.equals("pending")) {
            return PENDING;
        }
        return null;
    }

    public static Status getByID(int id) {
        if (id == 1) {
            return OPEN;
        }
        if (id == 2) {
            return CLAIMED;
        }
        if (id == 3) {
            return CLOSED;
        }
        if (id == 4) {
            return PENDING;
        }
        return null;
    }
}
