package me.sh1omi.stats;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;



public class MainClass extends JavaPlugin implements Listener{
	
	public static MainClass plugin;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[ " + pdfFile.getName() + " ] Version: " +  pdfFile.getVersion() + " Has been disabled");
		
	}
	
	@Override
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		getServer().getPluginManager().registerEvents(this, this);
		this.logger.info("[ " + pdfFile.getName() + " ] Version: " +  pdfFile.getVersion() + " Has been enabled");
		File folder = new File("plugins\\StatsPlayer");
		if (!folder.exists())
			folder.mkdir();
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent event)
	{
		File killed = new File(getDataFolder(), event.getEntity().getName()+".yml");
		File killer = new File(getDataFolder(), event.getEntity().getKiller().getName()+".yml");
		try {
			getConfig().load(killed);
			getConfig().set("d", getConfig().getInt("d")+1);
			if(getConfig().getInt("ks_a") > getConfig().getInt("ks"))
			{
				getConfig().set("ks",getConfig().getInt("ks_a"));
				event.getEntity().sendMessage(ChatColor.AQUA + "Nice one! Your kill streak now is " + getConfig().getInt("ks_a"));
			}
			getConfig().set("ks_a",0);
			getConfig().save(killed);
			getConfig().load(killer);
			getConfig().set("k", getConfig().getInt("k")+1);
			getConfig().set("ks_a", getConfig().getInt("ks_a")+1);
			getConfig().save(killer);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		File file = new File(getDataFolder(), event.getPlayer().getDisplayName()+".yml");
		if(!file.exists())
			try {
				file.createNewFile();
            } catch (IOException e) {
            	getServer().broadcastMessage(e.getMessage());
            }
		try {
			getConfig().load(file);
			getConfig().set("k", 0);
			getConfig().set("d", 0);
			getConfig().set("ks", 0);
			getConfig().set("ks_a", 0);
			getConfig().save(file);
		} catch (IOException e) {
			getServer().broadcastMessage(e.getMessage());
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd,String commandLabel,String[] args){
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(commandLabel.equalsIgnoreCase("stats"))
			{
				if(args.length == 0)
				{
					if(player.hasPermission("stats.show.my"))
					{
					File file = new File(getDataFolder(), player.getDisplayName()+".yml");
					int kills = 0;
					int deaths = 0;
					int killsteak = 0;
					try {
						getConfig().load(file);
						kills = getConfig().getInt("k");
						deaths = getConfig().getInt("d");
						killsteak = getConfig().getInt("ks");
						getConfig().save(file);
					} catch (IOException e) {
						getServer().broadcastMessage(e.getMessage());
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
					player.sendMessage(ChatColor.AQUA + "Kills: " + kills + " | Deaths: " + deaths);
					player.sendMessage(ChatColor.AQUA + "Ratio: " + String.format("%.2f", (double) kills / deaths) + " | Kill Streak: " + killsteak);
					} else
						player.sendMessage(ChatColor.DARK_RED + "[Stats] You dont have permission to do that!");
				} else if(player.hasPermission("stats.show.other"))
				{
				File file = new File(getDataFolder(), args[0]+".yml");
				if(file.exists())
				{
					int kills = 0;
					int deaths = 0;
					int killsteak = 0;
					try {
						getConfig().load(file);
						kills = getConfig().getInt("k");
						deaths = getConfig().getInt("d");
						killsteak = getConfig().getInt("ks");
						getConfig().save(file);
					} catch (IOException e) {
						getServer().broadcastMessage(e.getMessage());
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
					player.sendMessage(ChatColor.AQUA + "Kills: " + kills + " | Deaths: " + deaths);
					player.sendMessage(ChatColor.AQUA + "Ratio: " + String.format("%.2f", (double) kills / deaths) + " | Kill Streak: " + killsteak);
					} else
						player.sendMessage(ChatColor.DARK_RED + "[Stats] This player not exist.");
				}else player.sendMessage(ChatColor.DARK_RED + "[Stats] You dont have permission to do that!");
					return true;
				}
				else if(commandLabel.equalsIgnoreCase("resetstats"))
				{
					if(player.hasPermission("stats.reset"))
					{
					if(args.length == 0)
						player.sendMessage(ChatColor.DARK_RED + "[Stats] /ResetStats [player name]");
					else
					{
						File file = new File(getDataFolder(), args[0]+".yml");
						if(file.exists())
						{
							try {
								getConfig().load(file);
								getConfig().set("k",0);
								getConfig().set("d",0);
								getConfig().set("ks",0);
								getConfig().set("ks_a",0);
								getConfig().save(file);
							} catch (IOException e) {
								getServer().broadcastMessage(e.getMessage());
							} catch (InvalidConfigurationException e) {
								e.printStackTrace();
							}
							player.sendMessage(ChatColor.AQUA + "[Stats] The stats of the player " + args[0] + " reseted!");
						}
						else
							player.sendMessage(ChatColor.DARK_RED + "[Stats] This player not exist.");
					}
					} else
						player.sendMessage(ChatColor.DARK_RED + "[Stats] You dont have permission to do that!");
					return true;
				}
				else if(commandLabel.equalsIgnoreCase("creditstats"))
				{
					player.sendMessage(ChatColor.AQUA + "[Stats] plugin made by sh1omi2(skype), Thanks for use it :)");
					return true;
				}
		}
		return false;
		
	}
	
	public static String replaceColors(String string){
		
		return string.replaceAll("(?i)&([a-k0-9])", "\u00A7$1");
		}
	
}
