package item.commands.plus;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener{

	private ItemCommandsPlus plugin = ItemCommandsPlus.plugin;

	@EventHandler
	public void onPlayerUseCommandItemEvent(PlayerUseCommandItemEvent e){
		Player p = e.getPlayer();
		CommandItem item = e.getCommandItem();
		UUID uuid = p.getUniqueId();
		String name = item.getName();
		ItemStack itemStack = e.getItemStack();
		Block b = e.getBlock();
		Entity en = e.getEntity();
		String permission = item.getPermission();
		int cooldownTick = item.getCooldownTick();
		if(permission != null && !p.hasPermission(permission)){
			p.sendMessage(plugin.replacePermission(plugin.getDefaultPermissionMessage(), permission));
			return;
		}
		if(cooldownTick > 0 && plugin.isCooldown(uuid, name, cooldownTick)){
			String s = item.getCooldownMessage() != null ? item.getCooldownMessage() : plugin.replaceCooldown(plugin.getDefaultCooldownMessage(), cooldownTick, plugin.getCooldownTick(uuid, name));
			p.sendMessage(s);
			return;
		}else if(!plugin.isCooldown(uuid, name, cooldownTick))plugin.removeCooldown(uuid, name);
		for(String command : item.getCommands()){
			command = plugin.replaceHolder(command, p);
			if(b != null)command = plugin.replaceBlock(command, b);
			if(en != null)command = plugin.replaceEntity(plugin.replaceEntityHealth(command, en), en);
			String toCommand = plugin.toCommand(command);
			if(command.startsWith("console ")){
				ServerCommandEvent event = new ServerCommandEvent(plugin.getServer().getConsoleSender(), toCommand);
				Bukkit.getPluginManager().callEvent(event);
				if(!event.isCancelled()){
					plugin.getServer().dispatchCommand(event.getSender(), event.getCommand());
					plugin.logging(p, item);
				}
			}else if(command.startsWith("operator ")){
				boolean op = p.isOp();
				try{
					if(!op)p.setOp(true);
					PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(p, "/" + toCommand);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						event.getPlayer().performCommand(toCommand);
						plugin.logging(p, item);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					p.setOp(op);
				}
			}else if(command.startsWith("player ")){
				PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(p, "/" + toCommand);
				Bukkit.getPluginManager().callEvent(event);
				if(!event.isCancelled()){
					event.getPlayer().performCommand(toCommand);
					plugin.logging(p, item);
				}
			}
		}
		if(item.getRemove()){
			if(itemStack.getAmount() == 1)itemStack = null;
			else itemStack.setAmount(itemStack.getAmount() - 1);
			if(e.getSlotNumber() == 106)p.getInventory().setItemInOffHand(itemStack);
			else p.getInventory().setItemInMainHand(itemStack);
		}
		plugin.setCooldown(uuid, name);
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		if(e.getAction().equals(Action.PHYSICAL))return;
		Player p = e.getPlayer();
		ItemStack item = null;
		int slot = 0;
		if(e.getHand().equals(EquipmentSlot.HAND)){
			item = p.getInventory().getItemInMainHand();
			slot = p.getInventory().getHeldItemSlot();
		}else{
			item = p.getInventory().getItemInOffHand();
			slot = 106;
		}
		if(item != null && item.getType() != null && item.getType() != Material.AIR){
			for(CommandItem commandItem : plugin.getItems()){
				if(item.isSimilar(commandItem.getItemStack()) && commandItem.getActions().contains(e.getAction().toString())){
					PlayerUseCommandItemEvent event = new PlayerUseCommandItemEvent(p, commandItem, item, slot, e.getClickedBlock(), null);
					Bukkit.getPluginManager().callEvent(event);
					e.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
		Player p = e.getPlayer();
		ItemStack item = null;
		int slot = 0;
		if(e.getHand().equals(EquipmentSlot.HAND)){
			item = p.getInventory().getItemInMainHand();
			slot = p.getInventory().getHeldItemSlot();
		}else{
			item = p.getInventory().getItemInOffHand();
			slot = 106;
		}
		if(item != null && item.getType() != null && item.getType() != Material.AIR){
			for(CommandItem commandItem : plugin.getItems()){
				if(item.isSimilar(commandItem.getItemStack()) && commandItem.getActions().contains("RIGHT_CLICK_ENTITY")){
					PlayerUseCommandItemEvent event = new PlayerUseCommandItemEvent(p, commandItem, item, slot, null, e.getRightClicked());
					Bukkit.getPluginManager().callEvent(event);
					e.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Player))return;
		Player p = (Player) e.getDamager();
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item != null && item.getType() != null && item.getType() != Material.AIR){
			for(CommandItem commandItem : plugin.getItems()){
				if(item.isSimilar(commandItem.getItemStack()) && commandItem.getActions().contains("LEFT_CLICK_ENTITY")){
					PlayerUseCommandItemEvent event = new PlayerUseCommandItemEvent(p, commandItem, item, p.getInventory().getHeldItemSlot(), null, e.getEntity());
					Bukkit.getPluginManager().callEvent(event);
					e.setCancelled(true);
					break;
				}
			}
		}
	}
}
