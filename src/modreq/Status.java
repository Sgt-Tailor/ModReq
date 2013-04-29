package modreq;

public enum Status {
    OPEN("open"), 
    CLAIMED("claimed"), 
    CLOSED("claimed"),
    PENDING("pending");

    String status;

    private Status(String status) {
	this.status = status;
    }

    public String getStatusString() {
	return status;
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
	if(string.equals("pending")) {
	    return PENDING;
	}
	return null;
    }
}
