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
package modreq.repository;

import modreq.Comment;
import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TicketRepository {

    public static ModReq plugin = ModReq.getInstance();
    private static final Logger logger = Logger.getLogger("Minecraft");
    private Connection connection;
    private boolean useMysql;

    private List<String> createTables;
    private String getInsertedId;


    public TicketRepository(YamlConfiguration schema, boolean useMysql) {
        this.useMysql = useMysql;
        String prefix = useMysql ? "mysql" : "sqlite";

        createTables = schema.getStringList(prefix + ".schema");
        this.getInsertedId = schema.get(prefix + ".get-inserted-id").toString();
    }

    private Connection getConnection() {
        try {
            if (connection != null) {
                if (connection.isClosed() == false) {
                    return connection;
                }
            }
            if (useMysql) {
                String ip = plugin.getConfig().getString("mysql.ip");
                String user = plugin.getConfig().getString("mysql.user");
                String pass = plugin.getConfig().getString("mysql.pass");
                connection = DriverManager.getConnection("jdbc:mysql://" + ip, user, pass);
            } else {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:plugins/ModReq/DataBase.sql");
            }

            Statement stat = connection.createStatement();

            for (String query : this.createTables) {
                stat.execute(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("[ModReq] no connection could be made with the database. Shutting down plugin D:");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

        return connection;
    }

    public void clearTickets() {
        try {
            Connection conn = getConnection();
            Statement stat = conn.createStatement();
            stat.execute("DROP TABLE ticket");
            stat.execute("DROP TABLE commment");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTicketCountBySubmitter(Player p, Status status) throws SQLException {

        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT COUNT(1) FROM ticket WHERE 'submitterUUID' = ? AND 'status' = ?");
        stat.setString(1, p.getUniqueId().toString());
        stat.setString(2, status.getStatusString());
        ResultSet result = stat.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }

        return 0;
    }

    public ArrayList<Ticket> getTicketsBySubmitter(Player p, int page) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM ticket WHERE submitterUUID = ? ORDER BY ID DESC LIMIT 5 OFFSET ?");
        stat.setString(1, p.getUniqueId().toString());
        stat.setInt(2, (page * 5) - 5);
        ResultSet result = stat.executeQuery();

        ArrayList<Ticket> value = new ArrayList<Ticket>();

        while (result.next()) {
            Ticket t = getTicketByResultSet(result);
            this.addCommentsToTicket(conn, t);
            value.add(t);
        }
        return value;
    }

    public boolean playerHasClaimedTicket(Player p) throws SQLException {

        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM ticket WHERE staffUUID = ? AND status = ? limit 1");
        stat.setString(1, p.getUniqueId().toString());
        stat.setString(2, Status.CLAIMED.getStatusString());

        ResultSet result = stat.executeQuery();

        if (result.next()) {
            return true;
        }

        return false;
    }

    public void sendPlayerPage(int page, Status status, Player p) throws SQLException {
        Connection conn = getConnection();

        List<Ticket> tickets = new ArrayList<Ticket>();

        int ticketsPerPage = 10;
        int offset = (ticketsPerPage * page) - 10;

        StringBuilder statusSelect = new StringBuilder();
        statusSelect.append("status = '" + status.getStatusString() + "'");

        if (status == Status.OPEN) {
            if (plugin.getConfig().getBoolean("show-claimed-tickets-in-open-list")) {
                statusSelect.append(" or status = 'claimed'");
            }
            if (plugin.getConfig().getBoolean("show-pending-tickets-in-open-list") && p.hasPermission("modreq.claim.pending")) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ticket WHERE status = 'pending' limit ? offset ?");

                preparedStatement.setInt(1, ticketsPerPage);
                preparedStatement.setInt(2, offset);
                ResultSet resultPending = preparedStatement.executeQuery();

                while (resultPending.next()) {
                    tickets.add(getTicketByResultSet(resultPending));
                }
            }
        }

        // only fetch more tickets if we have not exceeded the limit yet
        int openTicketLimit = ticketsPerPage - tickets.size();
        if (openTicketLimit > 0) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ticket WHERE " + statusSelect.toString() + " limit ? offset ? ");
            preparedStatement.setInt(1, openTicketLimit);
            preparedStatement.setInt(2, offset);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                tickets.add(getTicketByResultSet(resultSet));
            }
        }
        p.sendMessage(ChatColor.GOLD + "-----List-of-" + status.getStatusString() + "-Requests-----");
        for (Ticket t : tickets) {
            t.sendSummarytoPlayer(p);
        }
        p.sendMessage(ChatColor.GOLD + "do /check <page> to see more");
    }

    public int getTicketCountByStatus(Status status) {
        try {
            Connection conn = getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT count(1) FROM ticket WHERE status = ?");
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

    public int addTicket(Ticket t) throws SQLException {
        if (t.getId() != 0) {
            throw new RuntimeException("TicketId is not 0");
        }
        Connection conn = getConnection();

        PreparedStatement prep = conn.prepareStatement("INSERT INTO ticket (submitter, submitterUUID, message, date, status, location, staff, staffUUID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        prep.setString(1, t.getSubmitter());
        prep.setString(2, t.getSubmitterUUID().toString());
        prep.setString(3, t.getMessage());
        prep.setObject(4, LocalDateTime.ofInstant(t.getDate(), ZoneOffset.UTC));
        prep.setString(5, t.getStatus().getStatusString());
        prep.setString(6, t.getLocationString());
        prep.setString(7, null);
        prep.setString(8, null);
        prep.addBatch();

        prep.executeBatch();

        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(this.getInsertedId);
        if (!set.next()) {
            throw new SQLException("not data returned by " + this.getInsertedId);
        }

        int ticketId = set.getInt(1);
        t.setId(ticketId);
        return ticketId;
    }

    public Ticket getTicketById(int id) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM ticket WHERE id = ?");
        stat.setInt(1, id);

        ResultSet result = stat.executeQuery();
        if (!result.next()) {
            return null;
        }

        Ticket ticket = getTicketByResultSet(result);
        addCommentsToTicket(conn, ticket);
        return ticket;
    }

    public void updateTicket(Ticket t) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement("UPDATE ticket SET status = ?, staff = ?, staffUUID = ? WHERE id = ?");

        prep.setString(1, t.getStatus().getStatusString());
        prep.setString(2, t.getStaff());
        prep.setString(3, t.getStaffUUID() == null ? null : t.getStaffUUID().toString());
        prep.setInt(4, t.getId());
        prep.execute();

        updateComments(conn, t);
    }

    private Ticket getTicketByResultSet(ResultSet result) throws SQLException {
        int id = result.getInt(1);
        String submitter = result.getString(2);
        String submitterUUIDString = result.getString(3);
        String message = result.getString(4);

        LocalDateTime date;
        if (useMysql) {
            date = result.getObject(5, LocalDateTime.class);
        } else {
            date = LocalDateTime.parse(result.getString(5));
        }
        String status = result.getString(6);
        String location = result.getString(7);
        String staff = result.getString(8);
        String staffUUIDString = result.getString(9);

        UUID staffUUID = null;
        if (staff != null) {
            staffUUID = UUID.fromString(staffUUIDString);
        }

        return new Ticket(id, submitter, UUID.fromString(submitterUUIDString), message, date.toInstant(ZoneOffset.UTC), Status.getByString(status), location, staff, staffUUID);
    }

    private void addCommentsToTicket(Connection conn, Ticket t) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM comment WHERE ticketId = ?");
        stat.setInt(1, t.getId());
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            int id = rs.getInt(1);
            String commenter = rs.getString(3);
            String commenterUUIDString = rs.getString(4);
            String comment = rs.getString(5);
            LocalDateTime date = rs.getObject(6, LocalDateTime.class);

            Comment c = new Comment(id, commenter, UUID.fromString(commenterUUIDString), comment, date.toInstant(ZoneOffset.UTC));
            t.insertComment(c);
        }
        stat.close();
    }

    /**
     * TODO: simplify. This should append the last comment if it does not yet exist.
     */
    private void updateComments(Connection conn, Ticket t) throws SQLException {
        if (t.getComments().isEmpty()) {
            return;
        }
        PreparedStatement prep = conn.prepareStatement("INSERT INTO comment (ticketId, commenter, commenterUUID, message, date) VALUES (?, ?, ?, ?, ?)");

        for (Comment c : t.getComments()) {
            if (c.getId() > 0) {
                continue;
            }

            prep.setInt(1, t.getId());
            prep.setString(2, c.getCommenter());
            prep.setString(3, c.getCommenterUUID().toString());
            prep.setString(4, c.getComment());
            prep.setObject(5, LocalDateTime.ofInstant(c.getDate(), ZoneOffset.UTC));
            prep.addBatch();
            prep.executeBatch();
        }
    }
}
