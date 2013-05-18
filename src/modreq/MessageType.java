package modreq;

public enum MessageType {
    ERROR_PERMISSION(ModReq.getInstance().getConfig().getString("error.permission",		"&cYou do not have permissions to do this")),
    ERROR_NUMBER(ModReq.getInstance().getConfig().getString("error.number",			"&c&number is not a number")),		
    ERROR_MESSAGE(ModReq.getInstance().getConfig().getString("error.message",			"&cYou have not typed a message, please do so")),	
    ERROR_NOMODS(ModReq.getInstance().getConfig().getString("error.nomods", 			"&7There are no mods online.")),
    ERROR_COMMENT(ModReq.getInstance().getConfig().getString("error.comment", 			"&c You may not submit that many comments in a row")),
    ERROR_TICKET_EXIST(ModReq.getInstance().getConfig().getString("error.ticket.exist", 	"&cThat ticket does not exist")),
    ERROR_TICKET_YOURS(ModReq.getInstance().getConfig().getString("error.ticket.your", 		"&cThat is not your ticket")),
    ERROR_TICKET_CLOSE(ModReq.getInstance().getConfig().getString("error.ticket.close",		"&cYou may not close that ticket")),
    ERROR_TICKET_CLAIM(ModReq.getInstance().getConfig().getString("error.ticket.claim", 	"&cYou may not claim that ticket")),
    ERROR_TICKET_TOOMANY(ModReq.getInstance().getConfig().getString("error.ticket.toomany", 	"&aYou have too many open open tickets")),
    
    PLAYER_SUBMIT(ModReq.getInstance().getConfig().getString("player.submit",			"&aYou have succesfully submitted a ticket, a staff member will help you soon")),
    PLAYER_TELEPORT(ModReq.getInstance().getConfig().getString("player.teleport",		"&a&player just teleported to the location of your ticket (&6#&number&a)")),
    PLAYER_CLAIM(ModReq.getInstance().getConfig().getString("player.claim",			"&b&player &ajust claimed your ticket (&6#&number&a)")),
    PLAYER_PENDING(ModReq.getInstance().getConfig().getString("player.pending",			"&b&player &ajust set your ticket to pending (&6#&number&a), please be patient")),
    PLAYER_REOPEN(ModReq.getInstance().getConfig().getString("player.reopen",					"&b&player &ajust reopened your ticket (&6#&number&a)")),
    PLAYER_CLOSE_WITHCOMMENT(ModReq.getInstance().getConfig().getString("player.close.withcomment",		"&b&player &ajust closed your ticket (&6#&number&a) &awith the comment: &7&comment")),
    PLAYER_CLOSE_WITHOUTCOMMENT(ModReq.getInstance().getConfig().getString("player.close.withoutcomment",	"&b&player &ajust closed your ticket (&6#&number&a)")),
    PLAYER_COMMENT(ModReq.getInstance().getConfig().getString("player.comment",			"&b&player&ajust commented on your ticket (&6#&number&a)")),
    
    STAFF_EXECUTOR_TICKET_CLOSED(ModReq.getInstance().getConfig().getString("staff.executor.closed",	"&aTicket closed")),
    STAFF_EXECUTOR_TICKET_CLAIMED(ModReq.getInstance().getConfig().getString("staff.executor.claimed",	"&aTicket claimed")),
    STAFF_EXECUTOR_TICKET_PENDING(ModReq.getInstance().getConfig().getString("staff.executor.pending",	"&aTicket set to pending")),
    STAFF_EXECUTOR_TICKET_REOPENED(ModReq.getInstance().getConfig().getString("staff.executor.re-opened","&aTicket reopened")),
    STAFF_EXECUTOR_TICKET_TELEPORTED(ModReq.getInstance().getConfig().getString("staff.executor.teleport","&aYou have been teleported")),
    STAFF_EXECUTOR_TICKET_COMMENT(ModReq.getInstance().getConfig().getString("staff.executor.comment",	"&aComment added")),
    
    STAFF_ALL_COMMENT(ModReq.getInstance().getConfig().getString("staff.all.comment",			"&b&player &ajust commented on ticket (&6#&number&a)")),
    STAFF_ALL_NOTIFICATION(ModReq.getInstance().getConfig().getString("staff.all.notification",		"&b&number &aopen tickets are waiting for you!")),
    STAFF_ALL_TICKETSUBMITTED(ModReq.getInstance().getConfig().getString("staff.all.ticket-submitted",	"&b&player &ajust submitted ticket &6#&number.")),
    
    STATUS_HEADER(ModReq.getInstance().getConfig().getString("headers-footers.status.header", 	"&6----List-of-your-last-5-tickets----")),
    STATUS_FOOTER(ModReq.getInstance().getConfig().getString("headers-footers.status.footer", 	"&6---do /status <id> for more info---")),
    MODS_HEADER(ModReq.getInstance().getConfig().getString("headers-footers.mods.header", 	"&6----List-of-online-staffmembers----")),
    CHECK_HEADER(ModReq.getInstance().getConfig().getString("headers-footers.check.header", 	"&6--List-of-[status]-tickets--")),
    CHECK_FOOTER(ModReq.getInstance().getConfig().getString("headers-footers.check.footer", 	"&6Do /check <page> for more tickets")),
    TICKET_HEADER(ModReq.getInstance().getConfig().getString("headers-footers.ticket.header", 	"&6---Info-about-ticket-#&number---")),
    
    LOG_PENDING_DEFAULT(ModReq.getInstance().getConfig().getString("log.pending.default",	"I set the status to pending")),
    LOG_CLAIM_DEFAULT(ModReq.getInstance().getConfig().getString("log.claim.default",		"I claimed this ticket.")),
    LOG_CLOSE_DEFAULT(ModReq.getInstance().getConfig().getString("log.close.default",		"I closed this ticket.")),
    LOG_CLOSE_SUFFIX(ModReq.getInstance().getConfig().getString("log.close.suffix",		"&c[Ticket closed]")),
    LOG_REOPEN_DEFAULT(ModReq.getInstance().getConfig().getString("log.re-open.default",	"I reopened this ticket.")),
    LOG_REOPEN_SUFFIX(ModReq.getInstance().getConfig().getString("log.re-open.suffix",		"&c[Ticket reopened]")),
    LOG_TPID_DEFAULT(ModReq.getInstance().getConfig().getString("log.tp-id.default",		"I teleported to this ticket.")),
    private String message;
    private MessageType(String message) {
	this.message = message;
    }

}
