package item.commands.plus;

import java.io.IOException;
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
				cmd(sender, args[0], null);
				return true;
			}
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item != null && item.getType() != null && item.getType() != Material.AIR){
				List<String> list = new ArrayList<String>();
				plugin.setItem(new CommandItem(args[1], item, null, true, false, false, 0, null, true, list, list));
				plugin.addNames(args[1]);
				p.sendMessage(ChatColor.AQUA + "手に持っているアイテムを[" + args[1] + "]として登録しました。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("remove")){
			if(args.length != 2){
				error(sender);
				cmd(sender, args[0], null);
				return true;
			}
			if(plugin.getItem(args[1]) != null){
				plugin.removeItem(args[1]);
				plugin.remvoeNames(args[1]);
				sender.sendMessage(ChatColor.AQUA + "指定アイテムの登録情報を削除しました。");
				return true;
			}
			sender.sendMessage(ChatColor.RED + "指定アイテムは存在しません。");
			return true;
		}else if(args[0].equalsIgnoreCase("list")){
			sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- 登録アイテム一覧 -");
			for(String name : plugin.getNames())sender.sendMessage("- " + name);
			return true;
		}else if(args[0].equalsIgnoreCase("edit")){
			if(args.length == 1){
				//how to use edit
				return true;
			}else if(args.length == 2){
				//item info
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
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("null"))item.setPermission(null);
				else item.setPermission(args[3]);
				String s = item.getPermission() != null ? "[" + args[3] + "]に設定しました。" : "削除しました。";
				sender.sendMessage(ChatColor.AQUA + "Permissionを" + s);
				return true;
			}else if(args[2].equalsIgnoreCase("click")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setClick(true);
				else if(args[3].equalsIgnoreCase("false"))item.setClick(false);
				else{
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				String s = item.canClick() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Clickを[" + s + "]にしました。");
				return true;
			}else if(args[2].equalsIgnoreCase("touch")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setTouch(true);
				else if(args[3].equalsIgnoreCase("false"))item.setTouch(false);
				else{
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				String s = item.canTouch() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Touchを[" + s + "]にしました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldown")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setCooldown(true);
				else if(args[3].equalsIgnoreCase("false"))item.setCooldown(false);
				else{
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				String s = item.getCooldown() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Cooldownを[" + s + "]にしました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldownTick")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
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
				sender.sendMessage(ChatColor.AQUA + "CooldownTickを[" + i + "tick]に設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldownMessage")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				String s = plugin.stringBuild(args, 3);
				item.setCooldownMessage(s);
				sender.sendMessage(ChatColor.AQUA + "CooldownMessageを[" + s + "]に設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("remove")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("true"))item.setRemove(true);
				else if(args[3].equalsIgnoreCase("false"))item.setRemove(false);
				else{
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				String s = item.getRemove() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Removeを[" + s + "]にしました。");
				return true;
			}else if(args[2].equalsIgnoreCase("actions")){
				if(args.length == 3||args.length == 4){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("add")){
					if(item.getActions().contains(args[4].toUpperCase())){
						sender.sendMessage(ChatColor.RED + "指定されたアクション[" + args[4].toUpperCase() + "]は既に追加されています。");
						return true;
					}
					for(String a : plugin.actions()){
						if(args[4].equalsIgnoreCase(a)){
							item.addAction(a);
							sender.sendMessage(ChatColor.AQUA + "Actionsに[" + a + "]を追加しました。");
							return true;
						}
					}
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}else if(args[3].equalsIgnoreCase("remove")){
					if(!item.getActions().contains(args[4].toUpperCase())){
						sender.sendMessage(ChatColor.RED + "指定されたアクション[" + args[4].toUpperCase() + "]は追加されていません。");
						return true;
					}
					for(String a : plugin.actions()){
						if(args[4].equalsIgnoreCase(a)){
							item.removeAction(a);
							sender.sendMessage(ChatColor.AQUA + "Actionsから[" + a + "]を削除しました。");
							return true;
						}
					}
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
			}else if(args[2].equalsIgnoreCase("commands")){
				if(args.length == 3||args.length == 4){
					error(sender);
					return true;
				}
				if(args[3].equalsIgnoreCase("add")){
					String s = plugin.stringBuild(args, 4);
					item.addCommand(s);
					sender.sendMessage(ChatColor.AQUA + "Commandsに[" + s + "]を追加しました。");
					return true;
				}else if(args[3].equalsIgnoreCase("remove")){
					String s = plugin.stringBuild(args, 4);
					for(String c : item.getCommands()){
						if(s.equalsIgnoreCase(c)){
							item.removeCommand(c);
							sender.sendMessage(ChatColor.AQUA + "Commandsから[" + c + "]を削除しました。");
							return true;
						}
					}
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
			}
		}else if(args[0].equalsIgnoreCase("backup")){
			if(args.length == 3){
				error(sender);
				cmd(sender, args[0], null);
				return true;
			}
			if(args[3].equalsIgnoreCase("config")||args[3].equalsIgnoreCase("data")){
				try{
					plugin.backupFile(args[3].toLowerCase());
				}catch(IOException e){
					sender.sendMessage(ChatColor.RED + "エラーが発生したため正常に処理を行えませんでした。");
					e.printStackTrace();
					return true;
				}
				sender.sendMessage(ChatColor.AQUA + "" + args[3].toLowerCase() + ".ymlをバックアップしました。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("reload")){
			config.reloadConfig();
			sender.sendMessage(ChatColor.AQUA + "コンフィグをリロードしました。");
			return true;
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

	public void cmd(CommandSender sender, String s, String sub){
		if(s.equalsIgnoreCase("add")){
			sender.sendMessage(ChatColor.AQUA + "/ic+ add [name]");
		}else if(s.equalsIgnoreCase("remove")){
			sender.sendMessage(ChatColor.AQUA + "/ic+ remove [name]");
		}else if(s.equalsIgnoreCase("edit")){
			if(sub.equalsIgnoreCase("permission")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] permission [permission]");
			}else if(sub.equalsIgnoreCase("click")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] click [true/false]");
			}else if(sub.equalsIgnoreCase("touch")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] touch [true/false]");
			}else if(sub.equalsIgnoreCase("cooldown")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] cooldown [true/false]");
			}else if(sub.equalsIgnoreCase("cooldownTick")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] cooldownTick [tick]");
			}else if(sub.equalsIgnoreCase("cooldownMessage")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] cooldownMessage [message]");
			}else if(sub.equalsIgnoreCase("remove")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] remove [true/false]");
			}else if(sub.equalsIgnoreCase("actions")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] actions [add/remove] [RIGHT_CLICK_AIR/RIGHT_CLICK_BLOCK/LEFT_CLICK_AIR/LEFT_CLICK_BLOCK/"
						+ "RIGHT_TOUCH_ENTITY/LEFT_TOUCH_ENTITY");
			}else if(sub.equalsIgnoreCase("commands")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] commands [add/remove] [command]");
			}
		}else if(s.equalsIgnoreCase("backup")){
			sender.sendMessage(ChatColor.AQUA + "/ic+ backup [config/data]");
		}
	}
}
