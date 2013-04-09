package modreq;

public class Comment {

    private String commenter;
    private String date;
    private String comment;

    /**
     * Used for the comment system
     * 
     * @param b
     *            Commenter name
     * @param c
     *            Comment
     * @param d
     *            Date
     */
    public Comment(String commenter, String comment, CommentType commenttype) {
	setCommenter(commenter);
	setComment(comment + " " + commenttype.getSuffix());
	setDate(ModReq.getTimeString());
    }

    public Comment(String commenter, String comment, String time) {
	setCommenter(commenter);
	setComment(comment);
	setDate(time);
    }

    public Comment() {

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

}
