package modreq.commands;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import modreq.Comment;
import modreq.ModReq;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommentCommand extends SubCommandExecutor{
    
    @command
    public void Integer(CommandSender sender, String[] args) {
	if(sender instanceof Player) {
	    if(Integer.parseInt(args[0]) > ModReq.getInstance().getTicketHandler().getTicketCount()) {
		sender.sendMessage(ChatColor.RED + ModReq.getInstance().Messages.getString("no-ticket"));
		return;
	    }
	    Player p = (Player) sender;
	    Ticket t = ModReq.getInstance().getTicketHandler().getTicketById(Integer.parseInt(args[0]));
	    
	    String commenter = p.getName();
	    String comment = Utils.join(args, " ", 1);
	    String date = getTimeString();
	    
	    Comment c = new Comment(commenter, comment, date);
	    
	    t.addComment(c);
	    
	    try {
		t.update();
	    } catch (SQLException e) {//does not happen
	    }
	}
    }
    
    
    private String getTimeString() {
	String timezone = ModReq.getInstance().getConfig().getString("timezone");
	DateFormat df = new SimpleDateFormat(ModReq.getInstance().getConfig().getString("timeformat","YY-MM-dd HH:mm:ss"));
	TimeZone tz = TimeZone.getTimeZone(timezone);


	Calendar cal = Calendar.getInstance(Calendar.getInstance().getTimeZone(),Locale.ENGLISH);     
	cal.add(Calendar.MILLISECOND,-(cal.getTimeZone().getRawOffset()));  
	cal.add(Calendar.MILLISECOND, tz.getRawOffset());       
	Date dt = new Date(cal.getTimeInMillis());  
	
	return df.format(dt) + " @" + timezone;
    }

}
