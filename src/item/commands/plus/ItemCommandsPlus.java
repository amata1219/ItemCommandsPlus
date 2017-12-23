package item.commands.plus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCommandsPlus extends JavaPlugin {

	public static ItemCommandsPlus plugin;
	public static CustomConfig config;
	public static CustomConfig data;

	private HashMap<String, TabExecutor> commands;
	private HashMap<String, String> cooldown;

	@Override
	public void onEnable(){
		plugin = this;

		config = new CustomConfig(plugin);
		data = new CustomConfig(plugin, "data.yml");

		commands = new HashMap<String, TabExecutor>();
		commands.put("ic+", new MainCommand(plugin));

		cooldown = new HashMap<String, String>();

		createFolder();

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

	public void info(String s){
		getLogger().info(s);
	}

	public void logging(Player p, CommandItem item){
		if(isEnableLogging())info(ChatColor.GRAY + "" + p.getName() + "(" + p.getUniqueId() + ") used " + item.getName() + ".");
	}

	public List<CommandItem> getItems(){
		List<CommandItem> items = new ArrayList<CommandItem>();
		for(String name : getNames())items.add(getItem(name));
		return items;
	}

	public void addNames(String name){
		List<String> list = getNames();
		if(!list.contains(name)){
			list.add(name);
			data.getConfig().set("Names", list);
		}
	}

	public void remvoeNames(String name){
		List<String> list = getNames();
		list.remove(name);
		data.getConfig().set("Names", list);
	}

	public List<String> getNames(){
		return data.getConfig().getStringList("Names");
	}

	public void setItem(CommandItem item){
		data.getConfig().set(item.getName(), item);
		config.updateConfig();
	}

	public void removeItem(String name){
		data.getConfig().set(name, null);
	}

	public CommandItem getItem(String name){
		return (CommandItem) data.getConfig().get(name);
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
		if(!cooldown.containsKey(uuid.toString()))return false;
		return (System.currentTimeMillis() - Long.valueOf(getCooldownTime(uuid,  name))) / 50 <= cooldownTick;
	}

	public String getDefaultCooldownMessage(){
		return config.getConfig().getString("DefaultCooldownMessage");
	}

	public String replaceCooldown(String s, int tick){
		return s.replace("[cooldown:t]", String.valueOf(tick)).replace("[cooldown:s]", String.valueOf((double) tick / 20));
	}

	public String replaceBlock(String s, Block b){
		Location loc = b.getLocation();
		return s.replace("[b_loc:x]", String.valueOf(loc.getBlockX())).replace("[b_loc:y]", String.valueOf(loc.getBlockY())).replace("[b_loc:z]", String.valueOf(loc.getBlockZ()))
				.replace("[b_name]", b.getType().toString()).replace("[biome]", b.getBiome().toString());
	}

	public String replaceEntity(String s, Entity e){
		Location loc = e.getLocation();
		return s.replace("[e_loc:x]", String.valueOf(loc.getBlockX())).replace("[e_loc:y]", String.valueOf(loc.getBlockY())).replace("[e_loc:z]", String.valueOf(loc.getBlockZ()))
				.replace("[e_name]", e.getName()).replace("[e_customName]", e.getCustomName());
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
		Location bed = p.getBedSpawnLocation();
		return s.replace("[name]", p.getName()).replace("[customName]", p.getCustomName()).replace("[uuid]", p.getUniqueId().toString())
				.replace("[loc:x]", String.valueOf(loc.getBlockX())).replace("[loc:y]", String.valueOf(loc.getBlockY())).replace("[loc:z]", String.valueOf(loc.getBlockZ()))
				.replace("[bed:x]", String.valueOf(bed.getBlockX())).replace("[bed:y]", String.valueOf(bed.getBlockY())).replace("[bed:z]", String.valueOf(bed.getBlockZ()))
				.replace("[exp]", String.valueOf(p.getExp())).replace("[expLv]", String.valueOf(p.getExpToLevel())).replace("[maxHp]", String.valueOf(p.getMaxHealth()))
				.replace("[hp]", String.valueOf(p.getHealth())).replace("[hunger]", String.valueOf(p.getFoodLevel())).replace("[loc:w]", loc.getWorld().getName())
				.replace("[bed:w]", bed.getWorld().getName());
	}

	public String toCommand(String s){
		return s.replace("console ", "").replace("operator ", "").replace("player ", "");
	}

	public boolean isEnableLogging(){
		return config.getConfig().getBoolean("EnableLogging");
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

	public String stringBuild(String[] args, int min){
		StringBuilder sb = new StringBuilder();
		for(int i = min; i < args.length; i++)sb.append(sb + " ");
		return sb.substring(0, sb.length()).toString();
	}

	private  void createFolder(){
		File file = new File(plugin.getDataFolder() + File.separator + "Backup");
		if(!file.exists())file.mkdir();
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
		  url = new URL("http://forum.minecraftuser.jp/viewtopic.php?f=38&t=35479");
		}catch(MalformedURLException e){
		  info(ChatColor.GRAY + "本プラグイン公開トピックのURLが正常に読み込めませんでした。");
		}
		return url;
	}
}