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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class VersionChecker implements Runnable {

    private ModReq plugin;
    private String currentVersion;
    private Logger logger = Logger.getLogger("Minecraft");

    public VersionChecker(ModReq instance) {
        plugin = instance;
        currentVersion = plugin.getCurrentVersion();
        logger.info("current version " + currentVersion);
    }

    public String getVersion() throws Exception {
        URL website = new URL(
                "http://website.shadowblox.com/plugins/modreqversion.txt");
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public String getDownloadLink() throws Exception {
        URL website = new URL(
                "http://website.shadowblox.com/plugins/downloadlink.txt");
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    @Override
    public void run() {

        try {
            String latestVersion = getVersion();
            String downloadLink = getDownloadLink();

            plugin.latestVersion = latestVersion;
            plugin.DownloadLink = downloadLink;

            // logger.info("[ModReq] Latest version is " + latestVersion +
            // ", comparing to current version");

            if (!currentVersion.equals(latestVersion)) {
                logger.info("[ModReq] The current and the lastest version do not match, a newer version must be available! (or you are running an Alfa/Beta build)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Failed");
        }

    }
}
