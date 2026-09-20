package fr.pandamcbe.mGConstructorAPI;

import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

    public class Team {

        private List<UUID> uuidList;
        private String string;
        private ChatColor color;
        private Integer points;

        public Team(String name, ChatColor color){
            this.string = name;
            this.color = color;
            resetTeamContent();
        }

        public Team(){}

        public void resetTeamContent(){
            uuidList = new ArrayList<>();
            points = 0;
        }

        public List<UUID> getUuidList() {
            return uuidList;
        }

        public void setUuidList(List<UUID> uuidList) {
            this.uuidList = uuidList;
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
            this.points = points;
        }

        public Team getNextTeam(TeamArena arena, Integer totalTeams){
            for(Team team : arena.getTeams()) {
//                System.out.println("Max Player "+ team.getString()+" :"+ getMaximum(arena.getAlluuids().size(), totalTeams)+ ", team count : "+ team.getUuidList().size());
                if(team.getUuidList().size() < getMaximum(arena.getAlluuids().size(), totalTeams)) {
                    return team;
                }
            }
            return null;
        }
        public void addPlayer(TeamArena nextArena, UUID uuid) {
//            System.out.println("TeamNumber :"+nextArena.getTeams().size());
//            System.out.println("team available :"+getNextTeam(nextArena, nextArena.getTotalTeams()).getString());
            if (getNextTeam(nextArena, nextArena.getTotalTeams()) != null) {
                Team nextTeam = getNextTeam(nextArena, nextArena.getTotalTeams());
                nextTeam.getUuidList().add(uuid);
//                System.out.println(getSpecificTeam(nextArena, ChatColor.RED).getUuidList().size());
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