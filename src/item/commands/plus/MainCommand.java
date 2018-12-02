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
	private CustomConfig data = ItemCommandsPlus.data;

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
			sender.sendMessage(ChatColor.WHITE + "- メインハンドに持っているアイテムを指定した名前で登録します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ remove [name]");
			sender.sendMessage(ChatColor.WHITE + "- 指定アイテムの登録情報を削除します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ give [name]");
			sender.sendMessage(ChatColor.WHITE + "- 指定した名前で登録されているアイテムを入手します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] [permission/cooldownTick/cooldownMessage/remove/actions/commands]");
			sender.sendMessage(ChatColor.WHITE + "- 登録されたアイテム情報を編集します。使用方法は /ic+ edit でご確認下さい。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ list");
			sender.sendMessage(ChatColor.WHITE + "- 全アイテム名を表示します。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ backup [config/data]");
			sender.sendMessage(ChatColor.WHITE + "- 指定ファイルをバックアップします。データは[plugins/ItemCommandPlus/Backup/指定ファイル名-現在のミリ秒時間.yml]として保存されます。");
			sender.sendMessage(ChatColor.AQUA + "/ic+ reload [config/data]");
			sender.sendMessage(ChatColor.WHITE + "- 指定ファイルをリロードします。/ic+ edit コマンドを使用して値を書き換えた場合は実行する必要はありません。");
			return true;
		}else if(args[0].equalsIgnoreCase("add")){
			Player p = isPlayer(sender);
			if(p == null)return true;
			if(args.length == 1){
				ItemStack item = p.getInventory().getItemInMainHand();
				if(item != null && item.getType() != null && item.getType() != Material.AIR){
					if(item.getItemMeta().getDisplayName() == null){
						p.sendMessage(ChatColor.RED + "手に持っているアイテムにカスタムネームが設定されていません。");
						return true;
					}
					String c = item.getItemMeta().getDisplayName();
					if(plugin.getNames().contains(c)){
						p.sendMessage(ChatColor.RED + "既に登録名が使用されています。");
						return true;
					}
					plugin.setItem(new CommandItem(c, item, null, 0, null, true, new ArrayList<String>(), new ArrayList<String>()));
					plugin.addNames(c);
					p.sendMessage(ChatColor.AQUA + "手に持っているアイテムを[" + c + ChatColor.AQUA + "]として登録しました。");
					return true;
				}else{
					p.sendMessage(ChatColor.RED + "空気は登録出来ません。");
					return true;
				}
			}
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item != null && item.getType() != null && item.getType() != Material.AIR){
				String s = replaceColor(args[1]);
				if(plugin.getNames().contains(s)){
					p.sendMessage(ChatColor.RED + "既に登録名が使用されています。");
					return true;
				}
				plugin.setItem(new CommandItem(s, item, null, 0, null, true, new ArrayList<String>(), new ArrayList<String>()));
				plugin.addNames(s);
				p.sendMessage(ChatColor.AQUA + "手に持っているアイテムを[" + s + ChatColor.AQUA + "]として登録しました。");
				return true;
			}else{
				p.sendMessage(ChatColor.RED + "空気は登録出来ません。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("remove")){
			if(args.length != 2){
				error(sender);
				cmd(sender, args[0], null);
				return true;
			}
			String s = replaceColor(args[1]);
			if(plugin.getItem(s) != null){
				plugin.removeItem(s);
				plugin.removeNames(s);
				sender.sendMessage(ChatColor.AQUA + "指定アイテム[" + s + ChatColor.AQUA + "]の登録情報を削除しました。");
				return true;
			}
			sender.sendMessage(ChatColor.RED + "指定アイテムは存在しません。");
			return true;
		}else if(args[0].equalsIgnoreCase("give")){
			Player p = isPlayer(sender);
			if(p == null)return true;
			if(args.length != 2){
				error(sender);
				cmd(sender, args[0], null);
				return true;
			}
			String s = replaceColor(args[1]);
			if(plugin.getItem(s) != null){
				plugin.getServer().getWorld(p.getWorld().getName()).dropItem(p.getLocation(), plugin.getItem(s).getItemStack());
				sender.sendMessage(ChatColor.AQUA + "指定アイテム[" + s + ChatColor.AQUA + "]を入手しました。");
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
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- Edit使用方法 -");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] permission [permission/null]");
				sender.sendMessage(ChatColor.WHITE + "アイテムの使用に必要なパーミッションを設定します。値にnullを選択すると削除します。"
						+ "パーミッションを設定しなくてもアイテムは使用出来ます。");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] cooldown [true/false]");
				sender.sendMessage(ChatColor.WHITE + "クールダウン機能を使用するか選択します。*");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] cooldownTick [tick]");
				sender.sendMessage(ChatColor.WHITE + "クールダウン時間を指定します。単位はtickで、1秒=20tickとなります。");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] cooldownMessage [message/null]");
				sender.sendMessage(ChatColor.WHITE + "クールダウン中にアイテムを使用しようとした場合に表示するメッセージを設定します。値にnullを選択すると削除します。"
						+ "メッセージを設定していない場合はコンフィグのデフォルト値が参照されます。");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] remove [true/false]");
				sender.sendMessage(ChatColor.WHITE + "アイテムを消費するか選択します。*");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] actions [add/remove/clear] [RIGHT_CLICK_AIR/RIGHT_CLICK_BLOCK/LEFT_CLICK_AIR/LEFT_CLICK_BLOCK/"
						+ "RIGHT_CLICK_ENTITY/LEFT_CLICK_ENTITY]");
				sender.sendMessage(ChatColor.WHITE + "アイテム使用タイミングの条件選択をします。addで追加、removeで削除、clearで全削除します。");
				sender.sendMessage(ChatColor.AQUA + "/ic+ edit [name] commands [add/remove] [console/operator/player] [command]");
				sender.sendMessage(ChatColor.WHITE + "アイテム使用時に実行するコマンドを指定します。addで追加、removeで削除、clearで全削除します。"
						+ "consoleでコンソールから、operatorで権限無視、playerで権限確認有りのコマンドとして登録します。");
				sender.sendMessage(ChatColor.GRAY + "* trueで有効、falseで無効になります。");
				return true;
			}else if(args.length == 2){
				String s = replaceColor(args[1]);
				CommandItem item = plugin.getItem(s);
				if(item == null){
					sender.sendMessage(ChatColor.RED + "指定アイテムは存在しません。");
					return true;
				}
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "- " + item.getName() + " -");
				sender.sendMessage(ChatColor.WHITE + "- Permission: " + item.getPermission());
				sender.sendMessage(ChatColor.WHITE + "- CooldownTick: " + item.getCooldownTick());
				sender.sendMessage(ChatColor.WHITE + "- CooldownMessage: " + item.getCooldownMessage());
				sender.sendMessage(ChatColor.WHITE + "- Remove: " + item.getRemove());
				sender.sendMessage(ChatColor.WHITE + "- Actions: " + item.getActionsForDisplay());
				sender.sendMessage(ChatColor.WHITE + "- Commands: " + item.getCommandsForDisplay());
				return true;
			}
			String sc = replaceColor(args[1]);
			CommandItem item = plugin.getItem(sc);
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
				plugin.setItem(item);
				String s = item.getPermission() != null ? "[" + args[3] + "]に設定しました。" : "削除しました。";
				sender.sendMessage(ChatColor.AQUA + "Permissionを" + s);
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
				plugin.setItem(item);
				sender.sendMessage(ChatColor.AQUA + "CooldownTickを[" + i + "tick]に設定しました。");
				return true;
			}else if(args[2].equalsIgnoreCase("cooldownMessage")){
				if(args.length == 3){
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args.length == 4 && args[3].equalsIgnoreCase("null")){
					item.setCooldownMessage(null);
					plugin.setItem(item);
					sender.sendMessage(ChatColor.AQUA + "CooldownMessageを削除しました。");
					return true;
				}
				String s = plugin.stringBuild(args, 3);
				String c = replaceColor(s);
				item.setCooldownMessage(c);
				plugin.setItem(item);
				sender.sendMessage(ChatColor.AQUA + "CooldownMessageを[" + c + ChatColor.AQUA + "]に設定しました。");
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
				plugin.setItem(item);
				String s = item.getRemove() ? "有効" : "無効";
				sender.sendMessage(ChatColor.AQUA + "Removeを[" + s + "]にしました。");
				return true;
			}else if(args[2].equalsIgnoreCase("actions")){
				if(args.length == 3||args.length == 4){
					if(args.length == 4 && args[3].equalsIgnoreCase("clear")){
						item.setActions(new ArrayList<String>());
						plugin.setItem(item);
						sender.sendMessage(ChatColor.AQUA + "Actionsをクリアしました。");
						return true;
					}
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
							plugin.setItem(item);
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
							plugin.setItem(item);
							sender.sendMessage(ChatColor.AQUA + "Actionsから[" + a + "]を削除しました。");
							return true;
						}
					}
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
			}else if(args[2].equalsIgnoreCase("commands")){
				if(args.length == 3||args.length == 4||args.length == 5||!plugin.types().contains(args[4].toLowerCase())){
					if(args.length == 4 && args[3].equalsIgnoreCase("clear")){
						item.setCommands(new ArrayList<String>());
						plugin.setItem(item);
						sender.sendMessage(ChatColor.AQUA + "Commandsをクリアしました。");
						return true;
					}
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
				if(args[3].equalsIgnoreCase("add")){
					String s = plugin.stringBuild(args, 4);
					item.addCommand(s);
					plugin.setItem(item);
					sender.sendMessage(ChatColor.AQUA + "Commandsに[" + plugin.substring(s) + "]を追加しました。");
					return true;
				}else if(args[3].equalsIgnoreCase("remove")){
					String s = plugin.stringBuild(args, 4);
					for(String c : item.getCommands()){
						if(s.equals(c)){
							item.removeCommand(c);
							plugin.setItem(item);
							sender.sendMessage(ChatColor.AQUA + "Commandsから[" + plugin.substring(s)  + "]を削除しました。");
							return true;
						}
					}
					error(sender);
					cmd(sender, args[0], args[2]);
					return true;
				}
			}
		}else if(args[0].equalsIgnoreCase("backup")){
			if(args.length == 1){
				error(sender);
				cmd(sender, args[0], null);
				return true;
			}
			if(args[1].equalsIgnoreCase("config")||args[1].equalsIgnoreCase("data")){
				try{
					plugin.backupFile(args[1].toLowerCase());
				}catch(IOException e){
					sender.sendMessage(ChatColor.RED + "エラーが発生したため正常に処理を行えませんでした。");
					e.printStackTrace();
					return true;
				}
				sender.sendMessage(ChatColor.AQUA + "" + args[1].toLowerCase() + ".ymlをバックアップしました。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("reload")){
			if(args.length == 1){
				error(sender);
				cmd(sender, args[0], null);
				return true;
			}
			if(args[1].equalsIgnoreCase("config")){
				config.reloadConfig();
				plugin.loadValue();
				sender.sendMessage(ChatColor.AQUA + "コンフィグ(config.yml)をリロードしました。");
				return true;
			}else if(args[1].equalsIgnoreCase("data")){
				data.reloadConfig();
				sender.sendMessage(ChatColor.AQUA + "コンフィグ(data.yml)をリロードしました。");
				return true;
			}
			error(sender);
			cmd(sender, args[0], null);
			return true;
		}
		return true;
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
			sender.sendMessage(ChatColor.GRAY + "/ic+ add [name]");
		}else if(s.equalsIgnoreCase("remove")){
			sender.sendMessage(ChatColor.GRAY + "/ic+ remove [name]");
		}else if(s.equalsIgnoreCase("edit")){
			if(sub.equalsIgnoreCase("permission")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] permission [permission]");
			}else if(sub.equalsIgnoreCase("cooldownTick")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] cooldownTick [tick]");
			}else if(sub.equalsIgnoreCase("cooldownMessage")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] cooldownMessage [message]");
			}else if(sub.equalsIgnoreCase("remove")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] remove [true/false]");
			}else if(sub.equalsIgnoreCase("actions")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] actions [add/remove/clear] [RIGHT_CLICK_AIR/RIGHT_CLICK_BLOCK/LEFT_CLICK_AIR/LEFT_CLICK_BLOCK/"
						+ "RIGHT_CLICK_ENTITY/LEFT_CLICK_ENTITY");
			}else if(sub.equalsIgnoreCase("commands")){
				sender.sendMessage(ChatColor.GRAY + "/ic+ edit [name] commands [add/remove/clear] [console/operator/player] [command]");
			}
		}else if(s.equalsIgnoreCase("backup")){
			sender.sendMessage(ChatColor.GRAY + "/ic+ backup [config/data]");
		}else if(s.equalsIgnoreCase("reload")){
			sender.sendMessage(ChatColor.GRAY + "/ic+ reload [config/data]");
		}
	}

	private String replaceColor(String s){
		return s.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2").replace("&3", "§3").replace("&4", "§4")
				.replace("&5", "§5").replace("&6", "§6").replace("&7", "§7").replace("&8", "§8").replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
				.replace("&c", "§c").replace("&d", "§d").replace("&e", "§e").replace("&f", "§f").replace("&k", "§k").replace("&l", "§l").replace("&m", "§m")
				.replace("&n", "§n").replace("&o", "§o").replace("&r", "§r");
	}
}
