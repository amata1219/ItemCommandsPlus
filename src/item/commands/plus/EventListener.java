package item.commands.plus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener{

	private ItemCommandsPlus plugin = ItemCommandsPlus.plugin;
	private CustomConfig config = ItemCommandsPlus.config;

	@EventHandler
	public void onPlayerUseCommandItemEvent(PlayerUseCommandItemEvent e){

	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){

	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){

	}
}
