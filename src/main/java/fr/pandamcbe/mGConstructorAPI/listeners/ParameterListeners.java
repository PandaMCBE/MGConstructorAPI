package fr.pandamcbe.mGConstructorAPI.listeners;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ParameterListeners implements Listener {

    @EventHandler
    void onEntityDamage(EntityDamageEvent event) {
        // Vérifiez si les dégâts sont causés par une chute
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            // Annulez les dégâts de chute
            event.setCancelled(true);
        }

//        if (OneShot.getInstance().invincible.contains(player)) {
//            // Empêche le mouvement en annulant l'event
//            event.setCancelled(true);
//        }
    }

    @EventHandler
    void onWeatherChange (WeatherChangeEvent event){
        event.setCancelled(true); //Annule la météo
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        //GLOBAL
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        if (MGConstructorAPI.getInstance().getGameManager().frozens.contains(player.getUniqueId())) {
            // Empêche le mouvement en annulant l'event
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void ProjectileHit(ProjectileHitEvent event){
        if (event.getEntity() instanceof Arrow){ //Si le projectile qui a touché la victime est une entité de type Arrow
            Arrow arrow = (Arrow) event.getEntity(); //Récupération de l'objet Arrow en faisant un cast légitime de l'entité
            arrow.remove(); //Retirer la flèche sur le corps de l'individu pour ne pas faire un hérisson.
        }
    }

    @EventHandler
    void onSleepling(PlayerBedEnterEvent event){
        event.setCancelled(true);
    }


    @EventHandler
    void onDeath(PlayerDeathEvent e) {
//        Player victim = e.getEntity();
        e.getDrops().clear(); //Destruction des items lorsqu'ils tombent au sol pour ne pas avoir des entités sur la map
//        victim.spigot().respawn(); //Exécuter une resuscitation du joueur forcée et automatique via spigot
    }

}