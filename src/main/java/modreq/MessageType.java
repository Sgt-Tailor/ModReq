package modreq;

import org.bukkit.ChatColor;

import java.util.Map;


public enum MessageType {
    ERROR_PERMISSION(),
    ERROR_GENERIC(),
    ERROR_NUMBER(),
    ERROR_MESSAGE(),
    ERROR_NOMODS(),
    ERROR_COMMENT(),

    ERROR_CLAIM_PENDING(),
    ERROR_CLAIM_MULTIPLE(),

    ERROR_TICKET_EXIST(),
    ERROR_TICKET_YOUR(),
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
    STAFF_EXECUTOR_TICKET_TELEPORT(),
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

    private final String message;

    public String getMessage() {
        return message;
    }

    public String format(String PlayerName, String TicketNumber, String comment) {
        String formatted = message;
        formatted = formatted.replace("&player", PlayerName);
        formatted = formatted.replace("&number", TicketNumber);
        formatted = formatted.replace("&comment", comment);
        formatted = ChatColor.translateAlternateColorCodes('&', formatted);
        return formatted;
    }

    public String formatWithParameters(Map<String, String> parameters) {
        String formatted = message;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            formatted = formatted.replace("&" + entry.getKey(), entry.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', formatted);
    }

    MessageType() {
        String tmpMessage = this.name().toLowerCase().replace("_", ".");
        tmpMessage = ModReq.getInstance().Messages.getString(tmpMessage, ModReq.getInstance().getDefaultMessages().getString(tmpMessage));
        message = tmpMessage;
    }
}
