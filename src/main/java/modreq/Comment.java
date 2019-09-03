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

public class Comment {

    private int id;
    private String commenter;
    private String commenterUUID;
    private String date;
    private String comment;

    /**
     * Used for the comment system
     */
    public Comment(String commenter, String commenterUUID, String comment, CommentType commenttype) {
        this.id = 0;
        this.commenter = commenter;
        this.commenterUUID = commenterUUID;
        this.comment = comment + " " + commenttype.getSuffix();
        this.date = ModReq.getTimeString();
    }

    public Comment(int id, String commenter, String commenterUUID, String comment, String date) {
        this.id = id;
        this.commenter = commenter;
        this.commenterUUID = commenterUUID;
        this.comment = comment;
        this.date = date;
    }

    public Comment() {
    }


    public int getId() {
        return id;
    }

    public String getCommenter() {
        return commenter;
    }

    private void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getDate() {
        return date;
    }

    private void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    public boolean equalsComment(Comment c) {
        if (c.getCommenter().equals(commenter)) {
            if (c.getComment().equals(comment)) {
                if (c.getDate().equals(date)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValid() {
        if (commenter == null) {
            return false;
        }
        if (comment == null) {
            return false;
        }
        if (date == null) {
            return false;
        }
        return true;

    }

    public String getCommenterUUID() {
        return commenterUUID;
    }
}
