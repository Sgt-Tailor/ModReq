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
package modreq.commands;

import java.sql.SQLException;

import modreq.*;
import modreq.korik.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommentCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length <= 1) {
                sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.comment.empty"), "", "", ""));
                return true;
            }

            int id = Integer.parseInt(args[0]);

            Player p = (Player) sender;
            Ticket t = ModReq.getInstance().getTicketRepository().getTicketById(id);

            if (t == null) {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p, args[0]);
                return true;
            }
            if (p.hasPermission("modreq.check") || playerIsSubmitter(p, t)) {
                if (maxCommentIsExeeded(p, t)) {
                    sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.comment.toomany"), "", "", ""));
                    return true;
                }
                String commenter = p.getName();
                String comment = Utils.join(args, " ", 1);
                Comment c = new Comment(commenter, p.getUniqueId().toString(), comment, CommentType.COMMENT);

                t.addComment(c);
                sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.executor.ticket.comment"), "", "", ""));
                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (!op.getName().equals(sender.getName())) {//do not send the message to the commandsender
                        if (t.getSubmitter().equals(op.getName())) {//it us the submitter
                            op.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("player.comment"), sender.getName(), args[0], ""));
                        } else if (t.getStaff().equals(sender.getName())) {//it is the staff member
                            op.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.all.comment"), sender.getName(), args[0], ""));
                        } else if (t.getCommentsBy(op.getName()).isEmpty() == false) {//it is someone else that commented earlier
                            op.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.all.comment"), sender.getName(), args[0], ""));
                        }
                    }
                }
                try {
                    t.update();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
                }
            } else {
                p.sendMessage(ChatColor.RED + "You don't have permissions to to this");
            }
        }

        return true;
    }

    private boolean maxCommentIsExeeded(Player p, Ticket t) {
        if (p.hasPermission("modreq.overwrite.commentlimit")) {
            return false;
        }

        int maxCommentStreak = ModReq.getInstance().getConfig().getInt("comment-limit");
        int i = 1;

        for (Comment c : t.getComments()) {
            if (c.getCommenter().equals(p.getName())) {
                i++;
                if (i > maxCommentStreak) {
                    return true;
                }
            } else {
                i = 1;
            }
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
