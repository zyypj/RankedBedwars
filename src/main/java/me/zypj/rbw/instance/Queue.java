package me.zypj.rbw.instance;

import lombok.Getter;
import lombok.Setter;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.database.SQLite;
import me.zypj.rbw.instance.cache.PartyCache;
import me.zypj.rbw.instance.cache.QueueCache;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.PickingMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Queue {

    private final String ID;
    private final int playersEachTeam;
    private final PickingMode pickingMode;
    private final boolean casual;
    private final List<Player> players;
    private final double eloMultiplier;

    BukkitTask queueTimer;

    public Queue(String ID) {
        this.ID = ID;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM queues WHERE discordID='" + ID + "';");

        try {
            this.playersEachTeam = resultSet.getInt(2);
            this.pickingMode = PickingMode.valueOf(resultSet.getString(3).toUpperCase());
            this.casual = Boolean.parseBoolean(resultSet.getString(4));
            this.eloMultiplier = resultSet.getDouble(5);

            SQLite.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        players = new ArrayList<>();

        QueueCache.initializeQueue(ID, this);

        Queue q = this;

        queueTimer = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (players.size() >= playersEachTeam * 2) {

                        List<Party> partiesInQ = new ArrayList<>();
                        List<Player> soloPlayersInQ = new ArrayList<>();

                        // CHECK FOR ALL PARTIES AND SOLO PLAYERS
                        for (Player p : players) {
                            if (PartyCache.getParty(p) != null) {
                                if (!partiesInQ.contains(PartyCache.getParty(p))) {
                                    partiesInQ.add(PartyCache.getParty(p));
                                }
                            } else {
                                soloPlayersInQ.add(p);
                            }
                        }

                        List<Party> partiesStillInQ = new ArrayList<>(partiesInQ);
                        // REMOVE PARTIES IF NOT ALL PARTY PLAYERS IN Q
                        if (!partiesInQ.isEmpty()) {
                            for (Party p : partiesInQ) {
                                for (Player player : p.getMembers()) {
                                    if (!players.contains(player)) {
                                        partiesStillInQ.remove(PartyCache.getParty(player));
                                    }
                                }
                            }
                        }

                        // CHECK HOW MANY STILL ABLE TO Q
                        List<Player> ableToQ = new ArrayList<>(soloPlayersInQ);
                        for (Party p : partiesStillInQ) {
                            ableToQ.addAll(p.getMembers());
                        }

                        if (ableToQ.size() >= getPlayersEachTeam() * 2) {
                            List<Player> playerList = new ArrayList<>();

                            for (Party p : partiesStillInQ) {
                                if (playerList.size() + p.getMembers().size() <= getPlayersEachTeam() * 2) {
                                    playerList.addAll(p.getMembers());
                                    ableToQ.removeAll(p.getMembers());
                                }
                            }

                            int tempPL = playerList.size();
                            for (int i = 0; i < getPlayersEachTeam() * 2 - tempPL; i++) {
                                playerList.add(ableToQ.get(i));
                            }

                            if (playerList.size() == getPlayersEachTeam() * 2) {
                                int channelCount;

                                try {
                                    channelCount = Objects.requireNonNull(RBWPlugin.guild.getCategoryById(Config.getValue("game-vcs-category"))).getChannels().size();
                                } catch (Exception e) {
                                    channelCount = 0;
                                }

                                if (channelCount < 49) {
                                    new Game(playerList, q).pickTeams();
                                    for (Player p : playerList) {
                                        players.remove(p);
                                    }
                                } else {
                                    StringBuilder mentions = new StringBuilder();
                                    for (Player p : playerList) {
                                        mentions.append("<@").append(p.getID()).append(">");
                                    }

                                    Embed embed = new Embed(EmbedType.ERROR, "Game Limit Reached!", "There are currently over 50 voice channels (maximum for Discord) and I can't do any more\n" +
                                            "Please wait until some matches finish", 1);
                                    Objects.requireNonNull(RBWPlugin.guild.getTextChannelById(Config.getValue("alerts-channel"))).sendMessage(mentions.toString()).setEmbeds(embed.build()).queue();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(RBWPlugin.getInstance(), 0L, 140L);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public static void delete(String ID) {
        QueueCache.removeQueue(QueueCache.getQueue(ID));

        SQLite.updateData("DELETE FROM queues WHERE discordID='" + ID + "';");
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
}
