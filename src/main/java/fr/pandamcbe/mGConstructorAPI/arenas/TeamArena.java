package fr.pandamcbe.mGConstructorAPI.arenas;

import fr.pandamcbe.mGConstructorAPI.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public abstract class TeamArena extends Arena{

    public List<Team> teams;
    public Map<ChatColor, Location> spawnByTeam;
    public int totalTeams;
    public Map<ChatColor, Location> goalByTeam;

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Map<ChatColor, Location> getSpawnByTeam() {
        return spawnByTeam;
    }

    public void setSpawnByTeam(Map<ChatColor, Location> locByTeam) {
        this.spawnByTeam = locByTeam;
    }

    public int getTotalTeams() {
        return totalTeams;
    }

    public void setTotalTeams(int totalTeams) {
        this.totalTeams = totalTeams;
    }

    public Map<ChatColor, Location> getGoalByTeam() {
        return goalByTeam;
    }

    public void setGoalByTeam(Map<ChatColor, Location> goalByTeam) {
        this.goalByTeam = goalByTeam;
    }

    public void resetTeams(){
        teams.forEach(Team::resetTeamContent);
    }

}
