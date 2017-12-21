package item.commands.plus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCommand implements TabExecutor{

	private ItemCommandsPlus plugin = ItemCommandsPlus.plugin;
	private CustomConfig config = ItemCommandsPlus.config;
	private CustomConfig data= ItemCommandsPlus.data;

	MainCommand(ItemCommandsPlus plugin){
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0){
			sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- ItemCommands+ -");
			sender.sendMessage(ChatColor.WHITE + "Spigotバージョン: 1.12.2");
			sender.sendMessage(ChatColor.WHITE + "Pluginバージョン: " + plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.WHITE + "コマンド一覧: /ic+ commands");
			sender.sendMessage(ChatColor.WHITE + "公開トピック: " + plugin.getTopicURL());
			sender.sendMessage(ChatColor.GRAY + "Developed by amata1219(Twitter: @amata1219)");
			return true;
		}else if(args[0].equalsIgnoreCase("commands")){
			sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- ItemCommands+ - コマンド一覧");
			sender.sendMessage(ChatColor.AQUA + "/ic+");
			sender.sendMessage(ChatColor.WHITE + "- 本プラグインの詳細を表示します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ commands");
			sender.sendMessage(ChatColor.WHITE + "- 本プラグインのコマンド一覧を表示します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ add [name]");
			sender.sendMessage(ChatColor.WHITE + "- 手に持ったアイテムを指定した名前で登録します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ remove [name]");
			sender.sendMessage(ChatColor.WHITE + "- 指定した名前で登録されている情報を削除します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] [permission/click/touch/cooldown…]");
			sender.sendMessage(ChatColor.WHITE + "- 登録されたアイテム情報を編集します。使用方法は /ic+ edit でご確認下さい。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ list");
			sender.sendMessage(ChatColor.WHITE + "- 全アイテム名を表示します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ backup [config/data]");
			sender.sendMessage(ChatColor.WHITE + "- 指定ファイルをバックアップします。データは[plugins/ItemCommandPlus/Backup/指定ファイル名-現在のミリ秒時間.yml]として保存されます。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ reload");
			sender.sendMessage(ChatColor.WHITE + "- コンフィグをリロードします。/ic+ edit コマンドを使用して値を書き換えた場合は実行する必要はありません。");
			return true;
		}else if(args[0].equalsIgnoreCase("add")){
			Player p = isPlayer(sender);
			if(p == null)return true;
			if(args.length != 2){
				error(sender);
				return true;
			}
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item != null && item.getType() != null && item.getType() != Material.AIR){
				List<String> list = new ArrayList<String>();
				plugin.setItem(new CommandItem(args[1], item, null, true, false, false, 0, null, true, list, list));
				p.sendMessage(ChatColor.AQUA + "手に持っているアイテムを[" + args[1] + "]として登録しました。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("remove")){
			if(args.length != 2){
				error(sender);
				return true;
			}
			if(plugin.getItem(args[1]) != null){
				plugin.removeItem(args[1]);
				sender.sendMessage(ChatColor.AQUA + "指定アイテムの登録情報を削除しました。");
				return true;
			}
			sender.sendMessage(ChatColor.RED + "指定アイテムは存在しません。");
			return true;
		}else if(args[0].equalsIgnoreCase("list")){
			sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- 登録アイテム一覧 -");
			for(String name : plugin.getItemNames())sender.sendMessage("- " + name);
			return true;
		}else if(args[0].equalsIgnoreCase("edit")){
			if(args.length == 1){
				error(sender);
				return true;
			}else if(args.length == 2){
				error(sender);
				return true;
			}
			CommandItem item = plugin.getItem(args[1]);
			if(item == null){
				sender.sendMessage(ChatColor.RED + "指定アイテムは存在しません。");
				return true;
			}
			if(args[2].equalsIgnoreCase("permission")){
				if(args.length == 3){
					error(sender);
					return true;
				}
				if(args[3].equalsIgnoreCase("null"))item.setPermission(null);
				else item.setPermission(args[3]);
				String s = item.getPermission() != null ? args[3] + "に設定しました。" : "削除しました。";
				sender.sendMessage(ChatColor.AQUA + "Permissionを" + s);
				return true;
			}else if(args[2].equalsIgnoreCase("click")){
				if(args.length == 3){
					error(sender);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setClick(true);
				else if(args[3].equalsIgnoreCase("false"))item.setClick(false);
				else{
					error(sender);
					return true;
				}
				String s = item.canClick() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Clickを" + s + "に設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("touch")){
				if(args.length == 3){
					error(sender);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setTouch(true);
				else if(args[3].equalsIgnoreCase("false"))item.setTouch(false);
				else{
					error(sender);
					return true;
				}
				String s = item.canTouch() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Touchを" + s + "に設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldown")){
				if(args.length == 3){
					error(sender);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setCooldown(true);
				else if(args[3].equalsIgnoreCase("false"))item.setCooldown(false);
				else{
					error(sender);
					return true;
				}
				String s = item.getCooldown() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Cooldownを" + s + "に設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldownTick")){
				if(args.length == 3){
					error(sender);
					return true;
				}
				int i = 0;
				try{
					i = Integer.valueOf(args[3]);
				}catch(NumberFormatException e){
					error(sender);
					return true;
				}
				item.setCooldownTick(i);
				sender.sendMessage(ChatColor.AQUA + "CooldownTickを" + i + "tickに設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldownMessage")){

			}else if(args[2].equalsIgnoreCase("remove")){

			}else if(args[2].equalsIgnoreCase("actions")){

			}else if(args[2].equalsIgnoreCase("commands")){

			}
		}else if(args[0].equalsIgnoreCase("backup")){

		}else if(args[0].equalsIgnoreCase("reload")){

		}
		return false;
	}

	public Player isPlayer(CommandSender sender){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "ゲーム内から実行して下さい。");
			return null;
		}
		return (Player) sender;
	}

	public void error(CommandSender sender){
		sender.sendMessage(ChatColor.RED + "入力内容が不正です。");
	}
}
