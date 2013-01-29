package commands;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import korik.Utils;

import managers.TicketHandler;
import modreq.Status;
import modreq.modreq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModreqCommand implements CommandExecutor{
    private modreq plugin;
    private TicketHandler tickets;
    public ModreqCommand(modreq instance) {
	plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player){
		Player p = (Player) sender;
		if(p.hasPermission("modreq.request")){
			if(args.length == 0){
				p.sendMessage(ChatColor.RED + plugin.Messages.getString("no-message", "You have not typed a message, please do so"));
				return true;
			}
			else{
				int ticketsfromplayer;
				try {
					ticketsfromplayer = tickets.getTicketsFromPlayer(p, sender.getName(),Status.OPEN);
					if(plugin.getConfig().getInt("maximum-open-tickets") > ticketsfromplayer) {
						String message = Utils.join(args, " ", 0);
						savereq(message, sender, ((Player) sender).getLocation());
						sendMessageToAdmins(ChatColor.GREEN + sender.getName() + " " + ChatColor.AQUA + plugin.Messages.getString("submitted-mod", "submitted a moderator request"));
						p.sendMessage(ChatColor.GREEN + plugin.Messages.getString("submitted-player", "You successfully submitted a help ticket, a moderator will help you soon"));
						return true;
					}
					else {
						p.sendMessage(ChatColor.RED + plugin.Messages.getString("too-many-tickets", "You have too many open requests"));
						return true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				
				
			}
		}
	}
	return false;
    }
    public void sendMessageToAdmins(String message) {//sends a message to all online players with the modreq.check permission
	Player[] list = Bukkit.getServer().getOnlinePlayers();
	int l = list.length;
	int n = 0;
	while(n<l){
		Player op = list[n];
		if(op.hasPermission("modreq.check")){
			op.sendMessage(message);
		}
		n++;
	}
	
    }
    public void savereq(String message, CommandSender sender, Location loc) {//save a ticket to the database
	String timezone = plugin.getConfig().getString("timezone");
	DateFormat df = new SimpleDateFormat(plugin.getConfig().getString("timeformat","YY-MM-dd HH:mm:ss"));
	TimeZone tz = TimeZone.getTimeZone(timezone);


	Calendar cal = Calendar.getInstance(Calendar.getInstance().getTimeZone(),Locale.ENGLISH);     
	cal.add(Calendar.MILLISECOND,-(cal.getTimeZone().getRawOffset()));  
	cal.add(Calendar.MILLISECOND, tz.getRawOffset());       
	Date dt = new Date(cal.getTimeInMillis());  
	
	String call = df.format(dt) + " @" + timezone;
	String location =  loc.getWorld().getName()+" @ "+Math.round(loc.getX()) + " "+Math.round(loc.getY())+" "+Math.round(loc.getZ());
	
	try {
		tickets.addTicket( sender.getName(), message, call, Status.OPEN, location);
	} catch (SQLException e) {}
}

}
