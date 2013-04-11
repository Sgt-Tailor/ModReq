package modreq.commands;

import java.sql.SQLException;

import modreq.Comment;
import modreq.CommentType;
import modreq.ModReq;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommentCommand extends SubCommandExecutor {

    @command
    public void Integer(CommandSender sender, String[] args) {
	if (sender instanceof Player) {
	    int id = Integer.parseInt(args[0]);

	    if (id > ModReq.getInstance().getTicketHandler().getTicketCount()) {
		sender.sendMessage(ChatColor.RED
			+ ModReq.getInstance().Messages.getString("no-ticket"));
		return;
	    }
	    Player p = (Player) sender;
	    Ticket t = ModReq.getInstance().getTicketHandler()
		    .getTicketById(Integer.parseInt(args[0]));
	    if (p.hasPermission("modreq.check") || playerIsSubmitter(p, t)) {
		if (maxCommentIsExeeded(p, t)) {
		    p.sendMessage(ChatColor.RED
			    + ModReq.getInstance().Messages
				    .getString("too-many-comments"));
		    return;
		}
		String commenter = p.getName();
		String comment = Utils.join(args, " ", 1);
		Comment c = new Comment(commenter, comment, CommentType.COMMENT);

		t.addComment(c);
		sender.sendMessage(ChatColor.GREEN
			+ ModReq.getInstance().Messages.getString("comment"));
		if (t.getStaff().equals(sender.getName())) {
		    String notifyString = ModReq.getInstance().Messages
			    .getString("comment-notify-submitter");
		    t.sendMessageToSubmitter(ChatColor.AQUA + sender.getName()
			    + ChatColor.GREEN + " " + notifyString);
		}
		// else {
		String notifyString = ModReq.getInstance().Messages
			.getString("comment-notify-staff");
		t.notifyStaff(ChatColor.AQUA + sender.getName()
			+ ChatColor.GREEN + " " + notifyString + " "
			+ Integer.toString(t.getId()));
		// }
		try {
		    t.update();
		} catch (SQLException e) {// does not happen
		}
	    } else {
		p.sendMessage(ChatColor.RED
			+ "You don't have permissions to to this");
	    }

	}
    }

    private boolean maxCommentIsExeeded(Player p, Ticket t) {
	if (p.hasPermission("modreq.overwrite.commentlimit")) {
	    return false;
	}
	int i = 1;
	for (Comment c : t.getComments()) {
	    if (c.getCommenter().equals(p.getName())) {
		i++;
	    } else {
		i = 1;
	    }
	}
	if (i > ModReq.getInstance().getConfig().getInt("comment-limit")) {
	    return true;
	}
	return false;
    }

    private boolean playerIsSubmitter(Player p, Ticket t) {
	String sub = t.getSubmitter();
	String com = p.getName();
	if (sub.equals(com)) {
	    return true;
	}
	return false;
    }
}
