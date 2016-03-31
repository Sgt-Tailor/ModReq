package modreq.commands;

import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import modreq.Comment;
import modreq.CommentType;
import modreq.ModReq;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.managers.TicketHandler;

public class GuiltyCommand extends SubCommandExecutor {

	private ModReq plugin;
	private TicketHandler tickets;

	public GuiltyCommand(ModReq instance) {
		plugin = instance;
	}

	@Override
	public void onInvalidCommand(CommandSender sender, String[] args, String command) {
		tickets = plugin.getTicketHandler();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("modreq.guilty")) {
				if (args.length > 0) {
					int id;
					try {
						id = Integer.parseInt(args[0]);
					} catch (Exception e) {
						p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.number"), "",
								args[0], ""));
						return;
					}
					if (tickets.getTicketCount() < id) {
						sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.ticket.exist"),
								"", args[0], ""));
					} else {
						String guilty = args[1];
						Ticket t = tickets.getTicketById(id);
						String staff = sender.getName();

						t.addComment(new Comment(sender.getName(), guilty, CommentType.GUILTY));
						t.setStaff(staff);

						try {
							t.update();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						sender.sendMessage(
								ModReq.format(ModReq.getInstance().Messages.getString("staff.executor.ticket.guilty"),
										guilty, "", ""));
					}
				}
			}
		}
	}
}
