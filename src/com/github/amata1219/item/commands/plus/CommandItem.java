package com.github.amata1219.item.commands.plus;

import java.io.Serializable;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CommandItem implements Serializable{
	
	private String name;
	private transient ItemStack item;
	private String permission;
	private String cooldownTick;
	private String cooldownMessage;
	private boolean remove;
	private List<String> actions;
	private List<String> commands;

	CommandItem(String name, ItemStack item, String permission, int cooldownTick, String cooldownMessage,
			boolean remove, List<String> actions, List<String> commands){
		setName(name);
		setItemStack(item);
		setPermission(permission);
		setCooldownTick(cooldownTick);
		setCooldownMessage(cooldownMessage);
		setRemove(remove);
		setActions(actions);
		setCommands(commands);
	}

	private void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setItemStack(ItemStack item){
		this.item = item;
	}

	public ItemStack getItemStack(){
		return item;
	}

	public void setPermission(String permission){
		this.permission = permission;
	}

	public String getPermission(){
		return permission;
	}

	public void setCooldownTick(int cooldownTick){
		this.cooldownTick = String.valueOf(cooldownTick);
	}

	public int getCooldownTick(){
		return Integer.valueOf(cooldownTick);
	}

	public void setCooldownMessage(String cooldownMessage){
		this.cooldownMessage = cooldownMessage;
	}

	public String getCooldownMessage(){
		return cooldownMessage;
	}

	public void setRemove(boolean remove){
		this.remove = remove;
	}

	public boolean getRemove(){
		return remove;
	}

	public void setActions(List<String> actions){
		this.actions = actions;
	}

	public void addAction(String action){
		actions.add(action);
	}

	public void removeAction(String action){
		actions.remove(action);
	}

	public List<String> getActions(){
		return actions;
	}

	public String getActionsForDisplay(){
		StringBuilder sb = new StringBuilder();
		for(String s : getActions())sb.append(s + ", ");
		return sb.substring(0, sb.length()).toString();
	}

	public void setCommands(List<String> commands){
		this.commands = commands;
	}

	public void addCommand(String command){
		commands.add(command);
	}

	public void removeCommand(String command){
		commands.remove(command);
	}

	public List<String> getCommands(){
		return commands;
	}

	public String getCommandsForDisplay(){
		StringBuilder sb = new StringBuilder();
		for(String s : getCommands())sb.append(substring(s) + ", ");
		return sb.substring(0, sb.length()).toString();
	}

	private String substring(String s){
		if(s.startsWith("console "))return "[console] " + s.substring(8);
		else if(s.startsWith("operator "))return "[operator] " +s.substring(9);
		else if(s.startsWith("player "))return "[player] " +s.substring(7);
		return s;
	}
}
