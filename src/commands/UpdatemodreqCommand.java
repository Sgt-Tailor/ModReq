package commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import modreq.modreq;
import korik.SubCommandExecutor;

public class UpdatemodreqCommand extends SubCommandExecutor{
    private modreq plugin;
    
    public UpdatemodreqCommand(modreq instance) {
	plugin = instance;
    }
    
    @command
    public void Null(CommandSender sender, String[] args) {
	if(sender instanceof Player) {
		if(sender.hasPermission("modreq.update")) {	
			if(plugin.getConfig().getBoolean("check-updates", true)) {
				File Jar = new File(plugin.getDataFolder().getAbsolutePath()+  "/" + plugin.latestVersion, "modreq.jar");
				File ChangeLog = new File(plugin.getDataFolder().getAbsolutePath()+  "/" + plugin.latestVersion, "Changelog.txt");
				if(!ChangeLog.exists()) {
				    try {
					saveUrl(ChangeLog.getAbsolutePath(), "http://website.shadowblox.com/plugins/modreqchangelog.txt");
				    } catch (Exception e) {
				
				    }
				}
				if(!Jar.exists()) {
				
					Jar.getParentFile().mkdir();
					try {
						String link = "http://dev.bukkit.org/media/files/" + plugin.DownloadLink.split("/files/")[1];
						saveUrl(Jar.getAbsolutePath(), link);
						sender.sendMessage(ChatColor.GOLD + "[ModReq]" + ChatColor.GREEN + "version " + plugin.latestVersion + " has been download to the plugin folder");
						return;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Could not download the latest version of ModReq");
						return;
					}
				}
				else {
				    sender.sendMessage(ChatColor.RED + "You already have the lastest version downloaded");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "This feature is not enabled in the config");
				return;
			}
		}
	}
    }
    public void saveUrl(String filename, String urlString) throws MalformedURLException, IOException{
    	BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try {
    		in = new BufferedInputStream(new URL(urlString).openStream());
    		fout = new FileOutputStream(filename);

    		byte data[] = new byte[1024];
    		int count;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
    		}
    	}
    	finally {
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
	}
}
