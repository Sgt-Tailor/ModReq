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
package modreq.managers;

import modreq.Comment;
import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TicketHandler {

    public static ModReq plugin = ModReq.getInstance();
    private Connection connection;
    private static final Logger logger = Logger.getLogger("Minecraft");

    private Connection getConnection() {
        try {
            if (connection != null) {
                if (connection.isClosed() == false) {
                    return connection;
                }
            }
            Class.forName("org.sqlite.JDBC");
            if (plugin.getConfig().getBoolean("use-mysql")) {
                String ip = plugin.getConfig().getString("mysql.ip");
                String user = plugin.getConfig().getString("mysql.user");
                String pass = plugin.getConfig().getString("mysql.pass");
                String table1 = plugin.getConfig().getString("mysql.tables.tickets", "tickets");
                String table2 = plugin.getConfig().getString("mysql.tables.comments", "comments");
                connection = DriverManager.getConnection("jdbc:mysql://"
                        + ip, user, pass);
                Statement stat = connection.createStatement();
                stat.execute("CREATE TABLE IF NOT EXISTS " + table1 + " (id INT, submitter TEXT, message TEXT, date TEXT, status TEXT, location TEXT, staff TEXT)");
                stat.execute("CREATE TABLE IF NOT EXISTS " + table2 + " (id INT, commenter TEXT, message TEXT, date TEXT)");
                KillConnection();
                return connection;
            } else {
                connection = DriverManager
                        .getConnection("jdbc:sqlite:plugins/ModReq/DataBase.sql");
                Statement stat = connection.createStatement();
                stat.execute("CREATE TABLE IF NOT EXISTS requests (id int, submitter String, message String, date String, status String, location String, staff String)");
                stat.execute("CREATE TABLE IF NOT EXISTS comments (id int, commenter String, message String, date String)");
                KillConnection();
                return connection;
            }

        } catch (Exception e) {
            logger.severe("[ModReq] no connection could be made with the database. Shutting down plugin D:");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

    }

    private void KillConnection() {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 2L);
    }

    public void clearTickets() {
        try {
            Connection conn = getConnection();
            Statement stat = conn.createStatement();
            stat.execute("DROP TABLE requests");
            stat.execute("DROP TABLE comments");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTicketsFromPlayer(Player p, Status status) throws SQLException {

        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT COUNT(1) FROM requests WHERE 'submitter' = ? AND 'status' = ?");
        stat.setString(1, p.getName());
        stat.setString(2, status.getStatusString());
        ResultSet result = stat.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }

        return 0;
    }

    public ArrayList<Ticket> getTicketsByPlayer(String target)
            throws SQLException {// returns an arraylist containing all the
        // tickets that a player has submitted
        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM requests WHERE submitter = ? ORDER BY ID DESC LIMIT 5");
        stat.setString(1, target);
        ResultSet result = stat.executeQuery();

        ArrayList<Integer> tickets = new ArrayList<Integer>();
        ArrayList<Ticket> value = new ArrayList<Ticket>();

        while (result.next()) {
            Ticket t = getTicketByResultSet(result);
            value.add(t);
        }
        return value;
    }

    public boolean hasClaimed(Player p) throws SQLException {

        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM requests WHERE staff = ? AND status = ? limit 1");
        stat.setString(1, p.getName());
        stat.setString(2, Status.CLAIMED.getStatusString());

        ResultSet result = stat.executeQuery();

        if (result.next()) {
            return true;
        }

        return false;
    }

    /**
     * TODO: use sql offset in combination with limit
     */
    public void sendPlayerPage(int page, Status status, Player p) {
        try {
            Connection conn = getConnection();
            Statement stat = conn.createStatement();
            ArrayList<Integer> tickets = new ArrayList<Integer>();
            int nmbr = page * 10;
            ResultSet resultOC;//Result of open and closed tickets
            ResultSet resultP;//Result of pending tickets

            StringBuilder statusSelect = new StringBuilder();
            statusSelect.append("status = '" + status.getStatusString() + "'");

            if (status == Status.OPEN) {
                if (plugin.getConfig().getBoolean("show-claimed-tickets-in-open-list")) {
                    statusSelect.append(" or status = 'claimed'");
                }
                if (plugin.getConfig().getBoolean("show-pending-tickets-in-open-list") && p.hasPermission("modreq.claim.pending")) {
                    resultP = stat.executeQuery("SELECT * FROM requests WHERE status = 'pending' limit " + nmbr);
                    while (resultP.next()) {
                        if (tickets.size() >= 10) {
                            break;
                        }

                        tickets.add(resultP.getInt(1));
                    }
                }
                resultOC = stat.executeQuery("SELECT * FROM requests WHERE " + statusSelect.toString() + " limit " + nmbr);

            } else {
                resultOC = stat
                        .executeQuery("SELECT * FROM requests WHERE status = '"
                                + status.getStatusString() + "' limit " + nmbr);
            }
            while (resultOC.next()) {
                if (resultOC.getRow() > nmbr - 10 && tickets.size() < 10) {
                    tickets.add(resultOC.getInt(1));
                }
            }
            p.sendMessage(ChatColor.GOLD + "-----List-of-"
                    + status.getStatusString() + "-Requests-----");
            for (int i = 0; i < tickets.size(); i++) {
                getTicketById(tickets.get(i)).sendSummarytoPlayer(p);
            }
            p.sendMessage(ChatColor.GOLD + "do /check <page> to see more");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTicketCount() {// get the total amount of tickets
        try {
            Connection conn = getConnection();
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT max(id) FROM requests ");
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public int getTicketAmount(Status status) {
        try {
            Connection conn = getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT count(1) FROM requests WHERE status = ?");
            stat.setString(1, status.getStatusString());
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public int addTicket(String submitter, String message, String date,
                         Status status, String location) throws SQLException {
        Connection conn = getConnection();

        PreparedStatement prep = conn.prepareStatement("INSERT INTO requests VALUES (?, ?, ?, ?, ?,?,?)");
        int id = getTicketCount() + 1;
        prep.setInt(1, id);
        prep.setString(2, submitter);
        prep.setString(3, message);
        prep.setString(4, date);
        prep.setString(5, status.getStatusString());
        prep.setString(6, location);
        prep.setString(7, "no staff member yet");
        prep.addBatch();

        prep.executeBatch();
        return id;
    }

    public Ticket getTicketById(int id) {
        try {
            Connection conn = getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM requests WHERE id = ?");
            stat.setInt(1, id);

            ResultSet result = stat.executeQuery();
            if (!result.next()) {
                return null;
            }

            Ticket ticket = getTicketByResultSet(result);
            stat.close();
            addCommentsToTicket(conn, ticket);
            return ticket;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Ticket getTicketByResultSet(ResultSet result) throws SQLException {
        int id = result.getInt(1);
        String status = result.getString(5);
        String submitter = result.getString(2);
        String date = result.getString(4);
        String location = result.getString(6);
        String message = result.getString(3);
        String staff = result.getString(7);
        return new Ticket(id, submitter, message, date, Status.getByString(status), location, staff);
    }

    public void updateTicket(Ticket t) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement("UPDATE requests SET status = ?, staff = ? WHERE id = ?");
        String status = t.getStatus().getStatusString();
        String staff = t.getStaff();

        prep.setString(1, status);
        prep.setString(2, staff);
        prep.setInt(3, t.getId());
        prep.execute();

        updateComments(conn, t);
    }

    private void addCommentsToTicket(Connection conn, Ticket t)
            throws SQLException {
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM comments WHERE id = ?");
        stat.setInt(1, t.getId());
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            String commenter = rs.getString(2);
            String comment = rs.getString(3);
            String date = rs.getString(4);

            Comment c = new Comment(commenter, comment, date);
            t.addComment(c);
        }
        rs.close();
        stat.close();
    }

    /**
     * TODO: simplify. This should append the last comment if it does not yet exist
     */
    private void updateComments(Connection conn, Ticket t) throws SQLException {
        if (t.getComments().isEmpty()) {
            return;
        }
        PreparedStatement prep = conn
                .prepareStatement("INSERT INTO comments VALUES (?, ?, ?, ?)");
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("SELECT * FROM comments WHERE id = '"
                + t.getId() + "'");
        Comment A = new Comment();
        while (rs.next()) {
            String commenter = rs.getString(2);
            String comment = rs.getString(3);
            String date = rs.getString(4);

            A = new Comment(commenter, comment, date);
        }
        stat.close();
        Comment B = t.getComments().get(t.getComments().size() - 1);
        if (!A.isValid()) {
            prep.setInt(1, t.getId());
            prep.setString(2, B.getCommenter());
            prep.setString(3, B.getComment());
            prep.setString(4, B.getDate());
            prep.addBatch();
            prep.executeBatch();

            return;
        }

        if (A.equalsComment(B)) {
            return;
        }
        prep.setInt(1, t.getId());
        prep.setString(2, B.getCommenter());
        prep.setString(3, B.getComment());
        prep.setString(4, B.getDate());
        prep.addBatch();
        prep.executeBatch();
    }
}
