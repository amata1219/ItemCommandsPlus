package com.github.amata1219.item.commands.plus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ItemCommandsPlus extends JavaPlugin {

	public static ItemCommandsPlus plugin;
	public static CustomConfig config;
	public static CustomConfig data;

	private HashMap<String, TabExecutor> commands;
	private HashMap<String, String> cooldown;
	private List<CommandItem> items;

	private String defaultCooldownMessage;
	private String defaultPermissionMessage;
	private boolean enableLogging;

	@Override
	public void onEnable(){
		plugin = this;

		config = new CustomConfig(plugin);
		config.saveDefaultConfig();

		data = new CustomConfig(plugin, "data.yml");
		data.saveDefaultConfig();

		commands = new HashMap<String, TabExecutor>();
		commands.put("ic+", new MainCommand(plugin));

		cooldown = new HashMap<String, String>();

		items = new ArrayList<CommandItem>();
		loadItems();

		loadValue();

		createBackupFolder();

		binaryToText();

		getServer().getPluginManager().registerEvents(new EventListener(), plugin);

		info("ItemCommands+ is enable!");
	}

	@Override
	public void onDisable(){
		info("ItemCommands+ is disable!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	public void loadValue(){
		defaultCooldownMessage = config.getConfig().getString("DefaultCooldownMessage");
		defaultPermissionMessage = config.getConfig().getString("DefaultPermissionMessage");
		enableLogging = config.getConfig().getBoolean("EnableLogging");
	}

	public void info(String s){
		getLogger().info(s);
	}

	public void logging(Player p, CommandItem item){
		if(isEnableLogging())info(p.getName() + "(" + p.getUniqueId() + ") used " + item.getName() + ".");
	}

	public CommandItem deserializableCommandItem(String name){
		CommandItem item = null;
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(plugin.getDataFolder() + File.separator + "Data" + File.separator + name + ".bin"))){
			item = (CommandItem) ois.readObject();
		}catch(IOException|ClassNotFoundException e){
			return null;
		}
		return item;
	}

	public void loadItems(){
		items.clear();
		for(String name : getNames())items.add(getItem(name));
	}

	public List<CommandItem> getItems(){
		return items;
	}

	public void addNames(String name){
		List<String> list = getNames();
		if(!list.contains(name)){
			list.add(name);
			data.getConfig().set("Names", list);
			data.updateConfig();
			loadItems();
		}
	}

	public void removeNames(String name){
		List<String> list = getNames();
		List<String> remove = new ArrayList<String>();
		for(String s : list){
			if(name.equalsIgnoreCase(s))remove.add(s);
		}
		for(String s : remove)list.remove(s);
		data.getConfig().set("Names", list);
		data.updateConfig();
		loadItems();
	}

	public List<String> getNames(){
		return data.getConfig().getStringList("Names");
	}

	public void setItem(CommandItem item){
		data.getConfig().set(item.getName(), item.getItemStack());
		data.getConfig().set(item.getName() + "-Setting.Permission", item.getPermission());
		data.getConfig().set(item.getName() + "-Setting.CooldownTick", item.getCooldownTick());
		data.getConfig().set(item.getName() + "-Setting.CooldownMessage", item.getCooldownMessage());
		data.getConfig().set(item.getName() + "-Setting.Remove", item.getRemove());
		data.getConfig().set(item.getName() + "-Setting.Actions", item.getActions());
		data.getConfig().set(item.getName() + "-Setting.Commands", item.getCommands());
		data.updateConfig();
		loadItems();
	}

	public void removeItem(String name){
		deleteFile(name);
		try{
			data.getConfig().set(name, null);
			data.getConfig().set(name + "-Setting.Permission", null);
			data.getConfig().set(name + "-Setting.CooldownTick", null);
			data.getConfig().set(name + "-Setting.CooldownMessage", null);
			data.getConfig().set(name + "-Setting.Remove", null);
			data.getConfig().set(name + "-Setting.Actions", null);
			data.getConfig().set(name + "-Setting.Commands", null);
			data.updateConfig();
		}catch(NullPointerException e){
			return;
		}
		loadItems();
	}

	public CommandItem getItem(String name){
		if(!getNames().contains(name))return null;
		FileConfiguration d = data.getConfig();
		CommandItem item = new CommandItem(name, d.getItemStack(name), d.getString(name + "-Setting.Permission"), d.getInt(name + "-Setting.CooldownTick"),
				d.getString(name + "-Setting.CooldownMessage"), d.getBoolean(name + "-Setting.Remove"), d.getStringList(name + "-Setting.Actions"),
				d.getStringList(name + "-Setting.Commands"));
		return item;
	}

	public void setCooldown(UUID uuid, String name){
		cooldown.put(uuid.toString() + ":" + name, String.valueOf(System.currentTimeMillis()));
	}

	public void removeCooldown(UUID uuid, String name){
		cooldown.remove(uuid.toString() + ":" + name);
	}

	public long getCooldownTime(UUID uuid, String name){
		return Long.valueOf(cooldown.get(uuid.toString() + ":" + name));
	}

	public int getCooldownTick(UUID uuid, String name){
		return (int) ((System.currentTimeMillis() - Long.valueOf(getCooldownTime(uuid,  name))) / 50);
	}

	public boolean isCooldown(UUID uuid, String name, int cooldownTick){
		if(!cooldown.containsKey(uuid.toString() + ":" + name))return false;
		return (System.currentTimeMillis() - Long.valueOf(getCooldownTime(uuid,  name))) / 50 <= cooldownTick;
	}

	public String getDefaultCooldownMessage(){
		return defaultCooldownMessage;
	}

	public String getDefaultPermissionMessage(){
		return defaultPermissionMessage;
	}

	public String replaceCooldown(String s, int cooldownTick, int tick){
		return s.replace("[cooldown:t]", String.valueOf(cooldownTick - tick)).replace("[cooldown:s]", String.valueOf((double) (cooldownTick -  tick) / 20));
	}

	public String replacePermission(String s, String permission){
		return s.replace("[permission]", permission);
	}

	public String replaceBlock(String s, Block b){
		Location loc = b.getLocation();
		return s.replace("[b_loc:x]", String.valueOf(loc.getBlockX())).replace("[b_loc:y]", String.valueOf(loc.getBlockY())).replace("[b_loc:z]", String.valueOf(loc.getBlockZ()))
				.replace("[b_name]", b.getType().toString()).replace("[biome]", b.getBiome().toString());
	}

	public String replaceEntity(String s, Entity e){
		Location loc = e.getLocation();
		String customName =  e.getCustomName() != null ? e.getCustomName() : "";
		return s.replace("[e_loc:x]", String.valueOf(loc.getBlockX())).replace("[e_loc:y]", String.valueOf(loc.getBlockY())).replace("[e_loc:z]", String.valueOf(loc.getBlockZ()))
				.replace("[e_name]", e.getName()).replace("[e_customName]", customName);
	}

	@SuppressWarnings("deprecation")
	public String replaceEntityHealth(String s, Entity e){
		if(e instanceof Entity){
			LivingEntity l = (LivingEntity) e;
			return s.replace("[e_hp]", String.valueOf(l.getHealth())).replace("[e_maxHp]", String.valueOf(l.getMaxHealth()));
		}
		return s.replace("[e_hp]", "").replace("[e_maxHp]", "");
	}

	@SuppressWarnings("deprecation")
	public String replaceHolder(String s, Player p){
		Location loc = p.getLocation();
		Location bed = p.getBedSpawnLocation()!= null ? p.getBedSpawnLocation() : new Location(p.getWorld(), 0, 0, 0);
		String customName =  p.getCustomName() != null ? p.getCustomName() : "";
		Vector v = p.getLocation().getDirection();
		return s.replace("[name]", p.getName()).replace("[customName]",customName).replace("[uuid]", p.getUniqueId().toString())
				.replace("[loc:x]", String.valueOf(loc.getBlockX())).replace("[loc:y]", String.valueOf(loc.getBlockY())).replace("[loc:z]", String.valueOf(loc.getBlockZ()))
				.replace("[bed:x]", String.valueOf(bed.getBlockX())).replace("[bed:y]", String.valueOf(bed.getBlockY())).replace("[bed:z]", String.valueOf(bed.getBlockZ()))
				.replace("[exp]", String.valueOf(p.getExp())).replace("[expLv]", String.valueOf(p.getExpToLevel())).replace("[maxHp]", String.valueOf(p.getMaxHealth()))
				.replace("[hp]", String.valueOf(p.getHealth())).replace("[hunger]", String.valueOf(p.getFoodLevel())).replace("[loc:w]", loc.getWorld().getName())
				.replace("[bed:w]", bed.getWorld().getName()).replace("[direction:x]", String.valueOf(v.getX())).replace("[direction:y]", String.valueOf(v.getY()))
				.replace("[direction:z]", String.valueOf(v.getZ())).replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4")
				.replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
				.replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&k", "§k").replace("&l", "§l").replace("&m", "§m")
				.replace("&n", "§n").replace("&o", "§o").replace("&r", "§r");
	}

	public String toCommand(String s){
		return s.replace("console ", "").replace("operator ", "").replace("player ", "");
	}

	public boolean isEnableLogging(){
		return enableLogging;
	}

	public List<String> actions(){
		List<String> list = new ArrayList<String>(Arrays.asList("RIGHT_CLICK_AIR", "RIGHT_CLICK_BLOCK", "LEFT_CLICK_AIR", "LEFT_CLICK_BLOCK",
				"RIGHT_CLICK_ENTITY", "LEFT_CLICK_ENTITY"));
		return list;
	}

	public List<String> types(){
		List<String> list = new ArrayList<String>(Arrays.asList("console", "operator", "player"));
		return list;
	}

	public String substring(String s){
		if(s.startsWith("console "))return "[console] " + s.substring(8);
		else if(s.startsWith("operator "))return "[operator] " +s.substring(9);
		else if(s.startsWith("player "))return "[player] " +s.substring(7);
		return s;
	}
	public String stringBuild(String[] args, int min){
		StringBuilder sb = new StringBuilder();
		for(int i = min; i < args.length; i++)sb.append(args[i] + " ");
		return sb.toString().substring(0, sb.length() - 1);
	}

	private  void createBackupFolder(){
		File file = new File(plugin.getDataFolder() + File.separator + "Backup");
		if(!file.exists())file.mkdir();
	}

	private void deleteFile(String name){
		File file = new File(plugin.getDataFolder() + File.separator + "Data" + File.separator + name + ".bin");
		if(file.exists())file.delete();
	}

	private void binaryToText(){
		File d = new File(plugin.getDataFolder() + File.separator + "Data");
		if(d.exists()){
			for(String s : getNames()){
				File bin = new File(plugin.getDataFolder() + File.separator + "Data" + File.separator + s + ".bin");
				if(bin.exists()){
					CommandItem item = deserializableCommandItem(s);
					data.getConfig().set(item.getName() + "-Setting.Permission", item.getPermission());
					data.getConfig().set(item.getName() + "-Setting.CooldownTick", item.getCooldownTick());
					data.getConfig().set(item.getName() + "-Setting.CooldownMessage", item.getCooldownMessage());
					data.getConfig().set(item.getName() + "-Setting.Remove", item.getRemove());
					data.getConfig().set(item.getName() + "-Setting.Actions", item.getActions());
					data.getConfig().set(item.getName() + "-Setting.Commands", item.getCommands());
					data.updateConfig();
					deleteFile(s);
				}
			}
			d.delete();
		}
	}

	public void backupFile(String s) throws IOException{
		FileInputStream fileIn = new FileInputStream(plugin.getDataFolder() + File.separator + s + ".yml");
		FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder() + File.separator + "Backup" + File.separator + s + "-" + System.currentTimeMillis() + ".yml");
		byte[] buf = new byte[256];
		@SuppressWarnings("unused")
		int len = 0;
		while((len = fileIn.read(buf)) != -1){
			fileOut.write(buf);
		}
		fileOut.flush();
		fileOut.close();
		fileIn.close();
	}

	public URL getTopicURL(){
		URL url = null;
		try{
		  URI uri = URI.create("http://forum.minecraftuser.jp/viewtopic.php?f=38&t=35479");
		  url = uri.toURL();
		}catch(MalformedURLException e){
		  info(ChatColor.GRAY + "本プラグイン公開トピックのURLが正常に読み込めませんでした。");
		}
		return url;
	}
}