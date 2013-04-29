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

public enum CommentType {
    CLAIM, CLOSE, REOPEN, TP, COMMENT;

    public String getDefaultComment() {
	switch (this) {
	case CLAIM:
	    return ModReq.getInstance().Messages.getString("log.claim.default");
	case CLOSE:
	    return ModReq.getInstance().Messages.getString("log.close.default");
	case REOPEN:
	    return ModReq.getInstance().Messages
		    .getString("log.re-open.default");
	case TP:
	    return ModReq.getInstance().Messages.getString("log.tp-id.default");
	case COMMENT:
	    return "";
	default:
	    return null;

	}
    }

    public String getSuffix() {
	switch (this) {
	case CLAIM:
	    return "";
	case CLOSE:
	    return ModReq.getInstance().Messages.getString("log.close.suffix");
	case COMMENT:
	    return "";
	case REOPEN:
	    return ModReq.getInstance().Messages
		    .getString("log.re-open.suffix");
	case TP:
	    return "";
	default:
	    return "";
	}
    }

}
