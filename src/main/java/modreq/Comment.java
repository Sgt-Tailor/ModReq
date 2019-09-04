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

import java.time.Instant;
import java.util.UUID;

public class Comment {

    private int id;
    private String commenter;
    private UUID commenterUUID;
    private Instant date;
    private String comment;

    /**
     * Used for the comment system
     */
    public Comment(String commenter, UUID commenterUUID, String comment, CommentType commenttype) {
        this.id = 0;
        this.commenter = commenter;
        this.commenterUUID = commenterUUID;
        this.comment = comment + " " + commenttype.getSuffix();
        this.date = Instant.now();
    }

    public Comment(int id, String commenter, UUID commenterUUID, String comment, Instant date) {
        this.id = id;
        this.commenter = commenter;
        this.commenterUUID = commenterUUID;
        this.comment = comment;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getCommenter() {
        return commenter;
    }

    public Instant getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public UUID getCommenterUUID() {
        return commenterUUID;
    }
}
