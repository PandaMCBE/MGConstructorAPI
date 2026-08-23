package fr.pandamcbe.mGConstructorAPI;

import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

    public class Team {

        private static List<UUID> uuidList;
        private String string;
        private ChatColor color;
        private static Integer points;

        public Team(String name, ChatColor color){
            this.string = name;
            this.color = color;
        }

        public Team(){}

        public static void resetTeamContent(){
            uuidList = new ArrayList<>();
            points = 0;
        }

        public List<UUID> getUuidList() {
            return uuidList;
        }

        public void setUuidList(List<UUID> uuidList) {
            Team.uuidList = uuidList;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public ChatColor getColor() {
            return color;
        }

        public void setColor(ChatColor color) {
            this.color = color;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            Team.points = points;
        }

        public Team getNextTeam(TeamArena arena, Integer totalTeams){
            for(Team team : arena.getTeams()) {
                if(team.getUuidList().size() < getMaximum(arena.getAlluuids().size(), totalTeams)) {
                    return team;
                }
            }
            return null;
        }
        public void addPlayer(TeamArena nextArena, Player p) {
            if (getNextTeam(nextArena, nextArena.getTotalTeams()) != null) {
                Team nextTeam = getNextTeam(nextArena, nextArena.getTotalTeams());
                if (!nextTeam.getUuidList().contains(p.getUniqueId()))
                    nextTeam.getUuidList().add(p.getUniqueId());
            }
        }

        public void TeleportTeams(TeamArena nextArena) {
            if (nextArena != null) {
                nextArena.getTeams().forEach(team -> team.getUuidList().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(nextArena.getSpawnByTeam().get(team.getColor()))));
            }
        }

        public Integer getMaximum(Integer playerNumber, Integer totalTeams){
            while (playerNumber % totalTeams != 0) {
                playerNumber++;
            }
            return playerNumber / totalTeams;

        }

        public Team getSpecificTeam(TeamArena arena, ChatColor color){
            for(Team team : arena.getTeams()){
                if(team.getColor().equals(color)){
                    return team;
                }
            }
            return null;
        }

        public Team getTeamByPlayer(Player player, TeamArena arena){
            for(Team team : arena.getTeams()) {
                if(team.getUuidList().contains(player.getUniqueId())) {
                    return team;
                }
            }
            return null;
        }
    }