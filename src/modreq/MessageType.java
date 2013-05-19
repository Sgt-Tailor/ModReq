package modreq;

import org.bukkit.Bukkit;

public enum MessageType {
    ERROR_PERMISSION(),
    ERROR_NUMBER(),		
    ERROR_MESSAGE(),	
    ERROR_NOMODS(),
    ERROR_COMMENT(),
    ERROR_TICKET_EXIST(),
    ERROR_TICKET_YOURS(),
    ERROR_TICKET_CLOSE(),
    ERROR_TICKET_CLAIM(),
    ERROR_TICKET_TOOMANY(),
    
    PLAYER_SUBMIT(),
    PLAYER_TELEPORT(),
    PLAYER_CLAIM(),
    PLAYER_PENDING(),
    PLAYER_REOPEN(),
    PLAYER_CLOSE_WITHCOMMENT(),
    PLAYER_CLOSE_WITHOUTCOMMENT(),
    PLAYER_COMMENT(),
    
    STAFF_EXECUTOR_TICKET_CLOSED(),
    STAFF_EXECUTOR_TICKET_CLAIMED(),
    STAFF_EXECUTOR_TICKET_PENDING(),
    STAFF_EXECUTOR_TICKET_REOPENED(),
    STAFF_EXECUTOR_TICKET_TELEPORTED(),
    STAFF_EXECUTOR_TICKET_COMMENT(),
    
    STAFF_ALL_COMMENT(),
    STAFF_ALL_NOTIFICATION(),
    STAFF_ALL_TICKETSUBMITTED(),
    
    STATUS_HEADER(),
    STATUS_FOOTER(),
    MODS_HEADER(),
    CHECK_HEADER(),
    CHECK_FOOTER(),
    TICKET_HEADER(),
    
    LOG_PENDING_DEFAULT(),
    LOG_CLAIM_DEFAULT(),
    LOG_CLOSE_DEFAULT(),
    LOG_CLOSE_SUFFIX(),
    LOG_REOPEN_DEFAULT(),
    LOG_REOPEN_SUFFIX(),
    LOG_TPID_DEFAULT(),
    
    INFO_MODEQ(),
    INFO_PENDING(),
    INFO_CHECK(),
    INFO_TPID(),
    INFO_CLAIM(),
    INFO_REOPEN(),
    INFO_STATUS(),
    INFO_DONE(),
    INFO_MODS(),
    INFO_MODHELP(),
    INFO_COMMENT(),
    INFO_TICKET(),
    
    TICKET_SUBMITTER(),
    TICKET_DATE(),
    TICKET_LOCATION(),
    TICKET_STATUS(),
    TICKET_COMMENT(),
    TICKET_STAFF(),
    TICKET_REQUEST();
    private String message;
    private MessageType(String message) {
	this.message = message;
    }
    public String getMessage() {
	return message;
    }
    private MessageType() {
	String a = this.name().toLowerCase().replace("_", ".");
	String b = ModReq.getInstance().getConfig().getString(a, ModReq.getInstance().getDefaultMessages().getString(a));
	message = b;
	Bukkit.broadcastMessage(b);
    }

}
