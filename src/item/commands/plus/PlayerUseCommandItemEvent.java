package item.commands.plus;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUseCommandItemEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private CommandItem item;
	private Block block;
	private Entity entity;

	PlayerUseCommandItemEvent(Player player, CommandItem item, Block block, Entity entity){
		this.player = player;
		this.item = item;
		this.block = block;
		this.entity = entity;
	}

	public Player getPlayer(){
		return player;
	}

	public CommandItem getCommandItem(){
		return item;
	}

	public Block getBlock(){
		return block;
	}

	public Entity getEntity(){
		return entity;
	}

	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}

	public boolean isCancelled(){
		return cancelled;
	}

	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
