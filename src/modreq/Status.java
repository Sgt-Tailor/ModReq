package modreq;


public enum Status {
	OPEN, CLAIMED, CLOSED;
	
	public String getStatusString() {
		switch(this) {
		case OPEN: return "open";
		case CLAIMED: return "claimed";
		case CLOSED: return "closed";
		}
		return null;
	}
	public static Status getByString(String string) {
		if(string.equals("open")) {
			return OPEN;
		}
		if(string.equals("claimed")) {
			return CLAIMED;
		}
		if(string.equals("closed")) {
			return CLOSED;
		}
		return null;
	}
}
