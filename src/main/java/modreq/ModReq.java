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
package modreq;

import modreq.Metrics.Graph;
import modreq.repository.CommandManager;
import modreq.repository.TicketRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModReq extends JavaPlugin {

    public YamlConfiguration Messages;
    private static ModReq plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private File configFile;
    private File messages;
    private Metrics metrics;
    private TicketRepository ticketRepository;
    private static DateTimeFormatter dateTimeFormatter;

    @Override
    public void onEnable() {
        plugin = this;
        messages = new File(getDataFolder().getAbsolutePath() + "/messages.yml");

        checkConfigFile();
        loadMessages();

        YamlConfiguration schema = YamlConfiguration.loadConfiguration(getTextResource("schema.yml"));
        boolean usesql = getConfig().getBoolean("use-mysql");
        ticketRepository = new TicketRepository(schema, usesql);

        String timeformat = getConfig().get("timeformat").toString();
        String timezone = getConfig().get("timezone").toString();

        dateTimeFormatter = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneId.of(timezone));

        CommandManager cmdManager = new CommandManager(this);
        cmdManager.initCommands();

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ModReqListener(this), this);

        startNotify();
        startMetrics();



        logger.log(Level.INFO, "{0} version {1} is enabled.", new Object[]{this.getDescription().getName(), getCurrentVersion()});
    }

    @Override
    public void onDisable() {
        logger.log(Level.INFO, "{0} is now disabled ", this.getDescription().getName());
    }

    private void startMetrics() {
        if (ModReq.plugin.getConfig().getBoolean("metrics")) {
            try {
                metrics = new Metrics(ModReq.plugin);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startGraphs();
            logger.info("[ModReq] Using metrics");
        }

    }

    public void checkConfigFile() {
        configFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
        if (!configFile.exists()) {
            firstrun();
        }

        YamlConfiguration pluginYML = YamlConfiguration.loadConfiguration(getTextResource("plugin.yml"));
        if (!pluginYML.getString("config-version").equals(
                getConfig().getString("version"))) {
            logger.info("[ModReq] Your plugin version does not match the config version. Please visit the bukkitdev page for more information");
        }
    }

    public void reload() throws SQLException {
        messages = new File(getDataFolder().getAbsolutePath() + "/messages.yml");
        configFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
        if (!configFile.exists()) {
            firstrun();
        }

        YamlConfiguration pluginYML = YamlConfiguration.loadConfiguration(getTextResource("plugin.yml"));
        if (!pluginYML.getString("config-version").equals(
                getConfig().getString("version"))) {
            logger.info("[ModReq] Your plugin version does not match the config version. Please visit the bukkitdev page for more information");
        }
        loadMessages();

        YamlConfiguration schema = YamlConfiguration.loadConfiguration(getTextResource("schema.yml"));
        ticketRepository = new TicketRepository(schema, this.getConfig().getBoolean("use-mysql"));

        plugin.reloadConfig();

        ticketRepository.ForceReconnect();
    }

    public String getCurrentVersion() {
        return this.getDescription().getVersion();
    }

    public TicketRepository getTicketRepository() {
        return ticketRepository;
    }

    private void startGraphs() {
        metrics.start();
        try {
            Metrics metrics = new Metrics(plugin);
            Graph graph = metrics.createGraph("Tickets");
            graph.addPlotter(new Metrics.Plotter("Open") {
                @Override
                public int getValue() {
                    return ticketRepository.getTicketCountByStatus(Status.OPEN);
                }
            });
            graph.addPlotter(new Metrics.Plotter("Claimed") {
                @Override
                public int getValue() {
                    return ticketRepository.getTicketCountByStatus(Status.CLAIMED);
                }
            });
            graph.addPlotter(new Metrics.Plotter("Closed") {
                @Override
                public int getValue() {
                    return ticketRepository.getTicketCountByStatus(Status.CLOSED);
                }
            });
            metrics.start();
        } catch (IOException e) {
        }

    }

    private boolean checkTranslate() {
        if (!messages.exists()) {
            return false;
        } else {
            return true;
        }
    }

    private void loadMessages() {
        logger.info("[ModReq] Looking for messages.yml");
        if (checkTranslate()) {
            logger.info("[ModReq] messages.yml found. Trying to load messages.yml");
            Messages = YamlConfiguration.loadConfiguration(messages);
            logger.info("[ModReq] messages.yml loaded!");
        } else {
            logger.info("[ModReq] messages.yml not found, using default messages");
            saveDefaultMessages();
            Messages = getDefaultMessages();
        }
    }

    private void saveDefaultMessages() {
        plugin.saveResource("messages.yml", true);
    }

    public YamlConfiguration getDefaultMessages() {
        File messagesYaml = new File(getDataFolder().getAbsolutePath() + "/messages.yml");
        YamlConfiguration pluginYML = YamlConfiguration.loadConfiguration(messagesYaml);
        return pluginYML;
    }

    private void startNotify() {
        if (this.getConfig().getBoolean("notify-on-time")) {
            logger.info("[ModReq] Notifying on time enabled");
            long time = this.getConfig().getLong("time-period");
            time = time * 1200;
            Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                @Override
                public void run() {
                    TicketRepository th = getTicketRepository();
                    int opentickets = th.getTicketCountByStatus(Status.OPEN);
                    if (opentickets > 0) {
                        Player[] online = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
                        for (int i = 0; i < online.length; i++) {
                            if (online[i].hasPermission("modreq.check")) {
                                online[i].sendMessage(ChatColor.GOLD
                                        + "[ModReq]"
                                        + ChatColor.GREEN
                                        + Integer.toString(opentickets)
                                        + " "
                                        + plugin.Messages
                                        .getString("notification",
                                                "open tickets are waiting for you"));
                            }
                        }
                    }
                }
            }, 60L, time);
        }
    }

    private void firstrun() {
        this.saveDefaultConfig();
    }

    public static ModReq getInstance() {
        return plugin;
    }

    public static String getTimeString() {
        String timezone = ModReq.getInstance().getConfig().getString("timezone");
        DateFormat df = new SimpleDateFormat(ModReq.getInstance().getConfig().getString("timeformat", "YY-MM-dd HH:mm:ss"));
        TimeZone tz = TimeZone.getTimeZone(timezone);

        Calendar cal = Calendar.getInstance(Calendar.getInstance().getTimeZone(), Locale.ENGLISH);
        cal.add(Calendar.MILLISECOND, -(cal.getTimeZone().getRawOffset()));
        cal.add(Calendar.MILLISECOND, tz.getRawOffset());
        Date dt = new Date(cal.getTimeInMillis());

        return df.format(dt) + " @" + timezone;
    }

    public static String format(String input, String player, String number, String comment) {
        input = input.replace("%player%", player);
        input = input.replace("%number%", number);
        input = input.replace("%comment%", comment);
        input = ChatColor.translateAlternateColorCodes('&', input);
        return input;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }
}
