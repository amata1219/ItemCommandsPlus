package item.commands.plus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCommandsPlus extends JavaPlugin {

	public static ItemCommandsPlus plugin;
	public static CustomConfig config;
	public static CustomConfig data;

	private HashMap<String, TabExecutor> commands;
	private HashMap<String, String> cooldown;
	private List<CommandItem> items;

	@Override
	public void onEnable(){
		plugin = this;

		config = new CustomConfig(plugin);
		data = new CustomConfig(plugin);

		commands = new HashMap<String, TabExecutor>();

		cooldown = new HashMap<String, String>();

		items = new ArrayList<CommandItem>();

		createFolder();

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

	public List<CommandItem> getItems(){
		return items;
	}

	public void updateItems(){
		List<CommandItem> items = new ArrayList<CommandItem>();
		for(String name : getItemNames())items.add(getItem(name));
		this.items = items;
	}

	public List<String> getItemNames(){
		return data.getConfig().getStringList("List");
	}

	public void setItem(CommandItem item){
		data.getConfig().set(item.getName(), item);
	}

	public CommandItem getItem(String name){
		return (CommandItem) data.getConfig().get(name);
	}

	public void setCooldown(UUID uuid){
		cooldown.put(uuid.toString(), String.valueOf(System.currentTimeMillis()));
	}

	public void removeCooldown(UUID uuid){
		cooldown.remove(uuid.toString());
	}

	public long getCooldownTime(UUID uuid){
		return Long.valueOf(cooldown.get(uuid.toString()));
	}

	public boolean isCooldown(UUID uuid, int cooldownTick){
		if(!cooldown.containsKey(uuid.toString()))return false;
		return System.currentTimeMillis() - Long.valueOf(getCooldownTime(uuid)) <= cooldownTick;

	}

	private  void createFolder(){
		File file = new File(plugin.getDataFolder() + File.separator + "Backup");
		if(!file.exists())file.mkdir();
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