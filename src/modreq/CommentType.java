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
