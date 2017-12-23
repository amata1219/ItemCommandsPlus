package item.commands.plus;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandItemUseEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private CommandItem item;

	public Player getPlayer(){
		return player;
	}

	public CommandItem getCommandItem(){
		return item;
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
