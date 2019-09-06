package modreq.commands;

import modreq.*;
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;
import modreq.repository.TicketRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TicketCommand extends SubCommandExecutor {

    private final ModReq plugin;
    private final TicketRepository ticketRepository;

    public TicketCommand(ModReq instance, TicketRepository ticketRepository) {
        super();
        this.plugin = instance;
        this.ticketRepository = ticketRepository;
    }

    @command(
            minimumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.create",
            usage = "/ticket create <message>")
    public void create(CommandSender sender, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player p = (Player) sender;
            try {
                int ticketsfromplayer = ticketRepository.getTicketCountBySubmitter(p, Status.OPEN);
                if (ticketsfromplayer >= plugin.getConfig().getInt("maximum-open-tickets")) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_TOOMANY, p);
                    return;
                }
                String message = Utils.join(args, " ", 0);
                Location loc = p.getLocation();
                String location = loc.getWorld().getName() + " @ "
                        + Math.round(loc.getX()) + " " + Math.round(loc.getY()) + " "
                        + Math.round(loc.getZ());

                Ticket t = new Ticket(0, p.getName(), p.getUniqueId(), message, Instant.now(), Status.OPEN, location, null, null);
                int ticketId = ticketRepository.addTicket(t);
                String idString = Integer.toString(ticketId);

                Message.sendToAdmins(MessageType.STAFF_ALL_TICKETSUBMITTED, new HashMap<String, String>() {{
                    put("player", sender.getName());
                    put("number", idString);
                }});

                Message.sendToPlayer(MessageType.PLAYER_SUBMIT, p);
            } catch (SQLException e) {
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
                e.printStackTrace();
            }
        });
    }

    @command(
            minimumArgsLength = 1,
            maximumArgsLength = 1,
            playerOnly = true,
            usage = "/ticket show <id>")
    public void show(CommandSender sender, String[] args) {
        int id;
        Player player = (Player) sender;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, player, args[0]);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Ticket t;
            try {
                t = ticketRepository.getTicketById(id);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
                return;
            }

            if (t == null) {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, player, args[0]);
                return;
            }

            if (t.getSubmitterUUID().equals(player.getUniqueId()) || player.hasPermission("modreq.show")) {
                t.sendMessageToPlayer(player);
            } else {
                Message.sendToPlayer(MessageType.ERROR_PERMISSION, player);
            }
        });
    }

    @command(
            minimumArgsLength = 1,
            maximumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.claim",
            usage = "/ticket claim <id>")
    public void claim(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        int ticketId;
        try {
            ticketId = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, args[0]);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = ticketRepository.getTicketById(ticketId);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
                    return;
                }

                Status currentstatus = t.getStatus();
                Status status = Status.CLAIMED;
                String staff = sender.getName();

                if (currentstatus.equals(Status.CLAIMED) && !sender.hasPermission("modreq.overwrite.claim")) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_CLAIM, (Player) sender, args[0]);
                    return;
                }

                if (currentstatus.equals(Status.PENDING) && !sender.hasPermission("modreq.claim.pending")) {
                    Message.sendToPlayer(MessageType.ERROR_CLAIM_PENDING, (Player) sender, args[0]);
                    return;
                }

                if (!plugin.getConfig().getBoolean("may-claim-multiple", false)) {
                    if (ticketRepository.playerHasClaimedTicket(p)) {
                        Message.sendToPlayer(MessageType.ERROR_CLAIM_MULTIPLE, (Player) sender, args[0]);
                        return;
                    }

                }

                t.setStaff(staff);
                t.setStaffUUID(p.getUniqueId());
                t.setStatus(status);
                t.addDefaultComment(p, CommentType.CLAIM);
                t.update();

                Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_CLAIMED, (Player) sender, args[0]);
                t.sendMessageToSubmitter(MessageType.PLAYER_CLAIM.format(p.getName(), args[0], ""));
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            }
        });
    }

    @command(
            minimumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.close",
            usage = "/ticket close <id> (message)")
    public void close(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int id;
        String idString = args[0];
        try {
            id = Integer.parseInt(idString);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, idString);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = ticketRepository.getTicketById(id);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
                    return;
                }

                String comment = Utils.join(args, " ", 1);
                String staff = sender.getName();

                String currenstatus = t.getStatus().getStatusString();
                String currentstaff = t.getStaff();

                if (!currenstatus.equals(Status.OPEN.getStatusString())
                        && !currentstaff.equals(staff)
                        && !sender.hasPermission("modreq.overwrite.close")) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_CLOSE, p);
                    return;
                }

                t.addComment(new Comment(p.getName(), p.getUniqueId(), comment, CommentType.CLOSE));

                t.setStaff(staff);
                t.setStaffUUID(p.getUniqueId());
                t.setStatus(Status.CLOSED);

                t.update();

                Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_CLOSED, p, idString);
                if (comment.equals("")) {
                    t.sendMessageToSubmitter(MessageType.PLAYER_CLOSE_WITHOUTCOMMENT.format(p.getName(), idString, ""));
                } else {
                    t.sendMessageToSubmitter(MessageType.PLAYER_CLOSE_WITHCOMMENT.format(p.getName(), idString, comment));
                }
            } catch (SQLException e) {
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
                e.printStackTrace();
            }
        });
    }

    @command(
            minimumArgsLength = 1,
            maximumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.escalate",
            usage = "/ticket escalate <id>")
    public void escalate(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.number"), "", args[0], ""));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = ticketRepository.getTicketById(id);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
                    return;
                }

                t.setStatus(Status.PENDING);
                t.addDefaultComment(p, CommentType.PENDING);
                t.setStaff(null);
                t.setStaffUUID(null);
                t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.pending"), sender.getName(), Integer.toString(id), ""));

                Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_PENDING, p);

                t.update();
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            }
        });
    }

    @command(
            minimumArgsLength = 1,
            maximumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.reopen",
            usage = "/ticket reopen <id>")
    public void reopen(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, args[0]);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = ticketRepository.getTicketById(id);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p, args[0]);
                    return;
                }

                String comment = Utils.join(args, " ", 1);
                Status status = Status.OPEN;

                t.addComment(new Comment(sender.getName(), p.getUniqueId(), comment, CommentType.REOPEN));
                t.setStaff(null);
                t.setStaffUUID(null);
                t.setStatus(status);
                t.update();

                Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_REOPENED, p, id, comment);
                t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.reopen"), sender.getName(), args[0], ""));

            } catch (SQLException e) {
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
                e.printStackTrace();
            }
        });
    }

    @command(
            minimumArgsLength = 1,
            playerOnly = true,
            usage = "/ticket comment <id> <comment>")
    public void comment(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int id;

        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = ModReq.getInstance().getTicketRepository().getTicketById(id);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p, args[0]);
                    return;
                }
                if (!p.hasPermission("modreq.comment-all") && !p.getUniqueId().equals(t.getSubmitterUUID())) {
                    Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
                    return;
                }

                if (maxCommentIsExeeded(p, t)) {
                    sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.comment.toomany"), "", "", ""));
                    return;
                }
                String commenter = p.getName();
                String comment = Utils.join(args, " ", 1);
                Comment c = new Comment(commenter, p.getUniqueId(), comment, CommentType.COMMENT);

                t.addComment(c);
                sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.executor.ticket.comment"), "", "", ""));
                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (!op.getName().equals(sender.getName())) {//do not send the message to the commandsender
                        if (t.getSubmitter().equals(op.getName())) {//it us the submitter
                            op.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("player.comment"), sender.getName(), args[0], ""));
                        } else if (t.getStaff().equals(sender.getName())) {//it is the staff member
                            op.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.all.comment"), sender.getName(), args[0], ""));
                        } else if (!t.getCommentsBy(op.getName()).isEmpty()) {//it is someone else that commented earlier
                            op.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.all.comment"), sender.getName(), args[0], ""));
                        }
                    }
                }

                t.update();

            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            }
        });
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

    @command(
            minimumArgsLength = 1,
            maximumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.tp",
            usage = "/ticket tp <id>")
    public void tp(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, args[0]);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = ticketRepository.getTicketById(id);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
                    return;
                }

                t.addDefaultComment(p, CommentType.TP);
                t.update();

                // teleport has to be done on the main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Location loc = t.getLocation();
                    p.teleport(loc);

                    Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_TELEPORT, p);
                    t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.teleport"), sender.getName(), args[0], ""));
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            }
        });
    }

    @command(
            maximumArgsLength = 1,
            playerOnly = true,
            permissions = "modreq.status",
            usage = "/ticket status (page)")
    public void status(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page <= 0) {
                    page = 1;
                }
            } catch (Exception e) {
                Message.sendToPlayer(MessageType.ERROR_NUMBER, player, args[0]);
                return;
            }
        }
        int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ArrayList<Ticket> tickets = ticketRepository.getTicketsBySubmitter(player, finalPage);
                Message.sendToPlayer(MessageType.STATUS_HEADER, player);
                for (Ticket ticket : tickets) {
                    ticket.sendStatus(player);
                }
                Message.sendToPlayer(MessageType.STATUS_FOOTER, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }

    @command(
            maximumArgsLength = 2,
            playerOnly = true,
            permissions = "modreq.show",
            usage = "/ticket list (status) (page)")
    public void list(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Status status = Status.OPEN;
        int page = 1;

        if (args.length == 1) {
            status = Status.getByString(args[0].toLowerCase());
            if (status == null) {
                status = Status.OPEN;
                try {
                    page = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    Message.sendToPlayer(MessageType.ERROR_NUMBER, player);
                    return;
                }
            }
        } else if (args.length == 2) {
            status = Status.getByString(args[0].toLowerCase());
            if (status == null) {
                // TODO send a message
                return;
            }

            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception e) {
                Message.sendToPlayer(MessageType.ERROR_NUMBER, player);
                return;
            }
        }

        Status finalStatus = status;
        int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ticketRepository.sendPlayerPage(finalPage, finalStatus, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = super.onTabComplete(sender, command, alias, args);

        if (args.length == 2 && args[0].equals("list")) {
            for (Status status : Status.values()) {
                String statusString = status.getStatusString().toLowerCase();
                if (statusString.contains(args[1].toLowerCase())) {
                    result.add(statusString);
                }
            }
        }

        return result;
    }
}
