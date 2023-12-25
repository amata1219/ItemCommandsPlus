package item.commands.plus;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerUseCommandItemEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private CommandItem item;
	private ItemStack itemStack;
	private int slot;
	private Block block;
	private Entity entity;

	PlayerUseCommandItemEvent(Player player, CommandItem item, ItemStack itemStack, int slot, Block block, Entity entity){
		this.player = player;
		this.item = item;
		this.itemStack = itemStack;
		this.slot = slot;
		this.block = block;
		this.entity = entity;
	}

	public Player getPlayer(){
		return player;
	}

	public CommandItem getCommandItem(){
		return item;
	}

	public ItemStack getItemStack(){
		return itemStack;
	}

	public int getSlotNumber(){
		return slot;
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
