/*
 	KorikUtils - configuration and subcommand utils for Bukkit/Minecraft
    Copyright (C) 2013 korikisulda

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
package modreq.korik;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


public class AnnotationConfig extends SubCommandExecutor implements Configurable{
	
	@command(
			maximumArgsLength=0,
			description="null command",
			permissions="*")

	public void Null(CommandSender sender,String[] args){
		for(Field f:this.getClass().getFields()){
			if(f.isAnnotationPresent(config.class)){
				try{
					
				if(f.getType()==Location.class){
					if(f.get(this)!=null){
					sender.sendMessage(f.getName() + ": " + 
				((Location)f.get(this)).getWorld().getName() + "," + 
				((Location)f.get(this)).getBlockX() + "," + 
				((Location)f.get(this)).getBlockY() + "," + 
				((Location)f.get(this)).getBlockZ());
					}else{
						sender.sendMessage(ChatColor.GRAY + f.getName() + ": " + "null");
					}
					
				}else if(f.getType()==List.class){
				}else{
					
					if(f.get(this).toString()==""||f.get(this).toString()=="-1"){
						sender.sendMessage(ChatColor.GRAY + f.getName() + ": " + f.get(this).toString());			
					}else{
						sender.sendMessage(f.getName() + ": " + f.get(this).toString());
					}
				}
				
				}catch(Exception e){
					
				}
			}
			}
	}
	
	@command(
			maximumArgsLength=1000,
			minimumArgsLength=2,
			usage="<name> <value>",
			description="Changes settings.",
			permissions="*"
			)
	public void set(CommandSender sender,String[] args){

		for(Field f:this.getClass().getFields()){
			if(f.getName().equalsIgnoreCase(args[0])){
				if(f.isAnnotationPresent(config.class)){
					if(f.getAnnotation(config.class).settable()==false) return;
					try{
						if(f.get(this) instanceof String){
							f.set(this, Utils.join(args," ",1));
						}else if(f.get(this) instanceof Long){
							f.set(this, Long.parseLong(args[1]));
						}else if(f.get(this) instanceof Integer){
							f.set(this, Integer.parseInt(args[1]));
						}else if(f.get(this) instanceof Double){
							f.set(this, Double.parseDouble(args[1]));
						}else if(f.get(this) instanceof Boolean){
							f.set(this, Boolean.parseBoolean(args[1]));
						}else if(f.getType()==Location.class){
							if(args[1].equalsIgnoreCase("me")){
							f.set(this, ((Player)sender).getLocation());
							}else if(args[1].equalsIgnoreCase("null")){
							f.set(this, null);
							}else{
							sender.sendMessage("fail.");
							}
						}else if(f.getType()==SubCommandExecutor.class){
							((SubCommandExecutor)f.get(this)).onCommand(sender, Utils.stripArray(args));
							return;
						}
						sender.sendMessage(ChatColor.GREEN + "Set " + f.getName() + " to " + Utils.join(args," ",1));
					
					
					}catch(Exception e){
						sender.sendMessage(ChatColor.RED + "An error occured!");
						e.printStackTrace();
					}
	return;
					
				}
			}
		}
	}

	
	@command(
		maximumArgsLength=2,
		minimumArgsLength=1,
		usage="<name>",
		description="Clears a value.",
		permissions="*"
		)
public void clear(CommandSender sender,String[] args){

	for(Field f:this.getClass().getFields()){
		if(f.getName().equalsIgnoreCase(args[0])){
			if(f.isAnnotationPresent(config.class)){
				if(f.getAnnotation(config.class).settable()==false) return;
				try{
						f.set(this, null);
					sender.sendMessage(ChatColor.GREEN + "Set " + f.getName() + " to null");
				
				}catch(Exception e){
					sender.sendMessage(ChatColor.RED + "An error occured!");
					e.printStackTrace();
				}
return;
				
			}
		}
	}
}

	/*
	 * Saving and loading methods
	 */
	public ConfigurationSection save(ConfigurationSection c){
		for(Field f:this.getClass().getFields()){
			if(f.isAnnotationPresent(config.class)){
				try{
					
				if(f.get(this) instanceof Location){
					c.set(f.getName() + ".world", ((Location)f.get(this)).getWorld().getName());				
					c.set(f.getName() + ".x", ((Location)f.get(this)).getX());
					c.set(f.getName() + ".y", ((Location)f.get(this)).getY());
					c.set(f.getName() + ".z", ((Location)f.get(this)).getZ());
				}else if(f.get(this) instanceof List<?>){
					c.set(f.getName(),f.get(this));
				}else if(f.get(this) instanceof Configurable){
					c.createSection(f.getName());
					((Configurable)f.get(this)).applyTo(c.getConfigurationSection(f.getName()));
				}else{
					c.set(f.getName(), f.get(this));
				}
				
				}catch(Exception e){
					
				}
			}
			}
		return c;
	}

	public void load(ConfigurationSection c){
		for(Field f:this.getClass().getFields()){
			if(f.isAnnotationPresent(config.class)&&c.isSet(f.getName())){
				try{
					
					if(f.get(this) instanceof String){
						f.set(this, c.getString(f.getName()));
					}else if(f.get(this) instanceof Long){
						f.set(this, c.getLong(f.getName()));
					}else if(f.get(this) instanceof Integer){
						f.set(this, c.getInt(f.getName()));
					}else if(f.get(this) instanceof Double){
						f.set(this, c.getDouble(f.getName()));
					}else if(f.get(this) instanceof Boolean){
						f.set(this, c.getBoolean(f.getName()));
					}else if(f.getType()==Location.class){

								f.set(this, new Location(
								Bukkit.getServer().getWorld(c.getString(f.getName() + ".world")),
								c.getDouble(f.getName() + ".x"),
								c.getDouble(f.getName() + ".y"),
								c.getDouble(f.getName() + ".z")
								));

					}else if(f.getType()==List.class){

					
				}else if(f.get(this) instanceof Configurable){
					((Configurable)f.get(this)).loadFrom(c.getConfigurationSection(f.getName()));
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}
		
		

	}

	@Retention(RetentionPolicy.RUNTIME) public @interface config{
		String usage() default "";
		boolean comparison() default false;
		boolean settable() default true;
	}

	@Override
	public void applyTo(ConfigurationSection s) {
		save(s);
	}

	@Override
	public void loadFrom(ConfigurationSection s) {
		load(s);
	}
}
