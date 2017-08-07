package com.funkemunky.Scoreboard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.funkemunky.Scoreboard.Util.SimpleScoreboard;

import me.signatured.ezqueueshared.QueueInfo;
import net.milkbowl.vault.permission.Permission;


public class Core extends JavaPlugin implements Listener, PluginMessageListener {
	
	public static Permission perms = null;
	private HashMap<String, Integer> online = new HashMap<String, Integer>();
	
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            perms = permissionProvider.getProvider();
        }
        return (perms != null);
    }
	
	 @Override
     public void onPluginMessageReceived(String channel, Player player, byte[] message) {
             if (!channel.equals("BungeeCord")) return;
            
             try {
                     DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
                     String command = in.readUTF();
                    
                     if (command.equals("PlayerCount")) {
                             String server = in.readUTF();
                             int playerCount = in.readInt();
                            
                             online.put(server, playerCount);
                             
                     }
             } catch (Exception e) {
                     e.printStackTrace();
             }
     }
	
	public void refreshOnline(String server) {
		 try {
             ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b);

             out.writeUTF("PlayerCount");
             out.writeUTF(server);
            
             Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
          } catch (Exception e) {
             e.printStackTrace();
          }
	}
	
	public int getOnlinePlayers(String server) {
		return this.online.get(server);
	}
	
	public int getPosition(Player player) {
   
        if (player == null)
            return -1;
   
        return QueueInfo.getPosition(player.getName());
    }
	
	public boolean isInQueue(Player player) {
        String info = QueueInfo.getQueue(player.getName());
        if (info == null || info.equalsIgnoreCase("")) {
            return false;
        }
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
	    SimpleScoreboard scoreboard = new SimpleScoreboard("§c§lHCRiots");
	    scoreboard.send(p);
	    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				int onlinePlayers = getOnlinePlayers("ALL");
	    		if(!isInQueue(e.getPlayer())) {
	    			if(scoreboard.get(11, "&c&8&m----------------------") != null) {
	    				scoreboard.remove(11, "&c&8&m----------------------");
	    			}
				    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&c&8&m----------------------"), 10);
					scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lOnline"), 9);
					scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&f" + onlinePlayers + "/1500"), 8);
					scoreboard.add("   ", 7);
					scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lRank"), 6);
				    if(perms.getPrimaryGroup(p).equalsIgnoreCase("Owner")) {
				    	scoreboard.add(ChatColor.DARK_RED + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Manager")) {
				    	scoreboard.add(ChatColor.GREEN + perms.getPrimaryGroup(p), 5);
	    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("PlatformAdmin")) {
				    	scoreboard.add(ChatColor.RED.toString() + ChatColor.ITALIC + perms.getPrimaryGroup(p), 5);
	    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Mod+") || perms.getPrimaryGroup(p).equalsIgnoreCase("Twitch")) {
				    	scoreboard.add(ChatColor.DARK_PURPLE + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("TrialMod")) {
				    	scoreboard.add(ChatColor.DARK_AQUA + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Titanium")) {
				    	scoreboard.add(ChatColor.LIGHT_PURPLE + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Sapphire") || perms.getPrimaryGroup(p).equalsIgnoreCase("Mod")) {
				    	scoreboard.add(ChatColor.AQUA + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Ruby") || perms.getPrimaryGroup(p).equalsIgnoreCase("Youtuber") || perms.getPrimaryGroup(p).equalsIgnoreCase("Admin") || perms.getPrimaryGroup(p).equalsIgnoreCase("SeniorAdmin")) {
				    	scoreboard.add(ChatColor.RED + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Cobalt")) {
				    	scoreboard.add(ChatColor.BLUE + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Silver")) {
				    	scoreboard.add(ChatColor.GRAY + perms.getPrimaryGroup(p), 5);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Bronze") || perms.getPrimaryGroup(p).equalsIgnoreCase("Developer")){
				    	scoreboard.add(ChatColor.GOLD + perms.getPrimaryGroup(p), 5);
				    } else {
				    	scoreboard.add(ChatColor.WHITE + perms.getPrimaryGroup(p), 5);
				    }
				    scoreboard.add("  ", 4);
				    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lWebsite"), 3);
				    scoreboard.add(ChatColor.WHITE.toString() + "www.hcriots.com", 2);
				    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&8&m----------------------"), 1);
					scoreboard.update();
	    		} else {
				    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&c&8&m----------------------"), 11);
					scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lOnline"), 10);
					scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&f" + onlinePlayers + "/1500"), 9);
					scoreboard.add("   ", 8);
					scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lRank"), 7);
				    if(perms.getPrimaryGroup(p).equalsIgnoreCase("Owner")) {
				    	scoreboard.add(ChatColor.DARK_RED + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Manager")) {
				    	scoreboard.add(ChatColor.GREEN + perms.getPrimaryGroup(p), 6);
	    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("PlatformAdmin")) {
				    	scoreboard.add(ChatColor.RED.toString() + ChatColor.ITALIC + perms.getPrimaryGroup(p), 6);
	    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Mod+") || perms.getPrimaryGroup(p).equalsIgnoreCase("Twitch")) {
				    	scoreboard.add(ChatColor.DARK_PURPLE + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("TrialMod")) {
				    	scoreboard.add(ChatColor.DARK_AQUA + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Titanium")) {
				    	scoreboard.add(ChatColor.LIGHT_PURPLE + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Sapphire") || perms.getPrimaryGroup(p).equalsIgnoreCase("Mod")) {
				    	scoreboard.add(ChatColor.AQUA + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Ruby") || perms.getPrimaryGroup(p).equalsIgnoreCase("Youtuber") || perms.getPrimaryGroup(p).equalsIgnoreCase("Admin") || perms.getPrimaryGroup(p).equalsIgnoreCase("SeniorAdmin")) {
				    	scoreboard.add(ChatColor.RED + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Cobalt")) {
				    	scoreboard.add(ChatColor.BLUE + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Silver")) {
				    	scoreboard.add(ChatColor.GRAY + perms.getPrimaryGroup(p), 6);
				    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Bronze") || perms.getPrimaryGroup(p).equalsIgnoreCase("Developer")){
				    	scoreboard.add(ChatColor.GOLD + perms.getPrimaryGroup(p), 6);
				    } else {
				    	scoreboard.add(ChatColor.WHITE + perms.getPrimaryGroup(p), 6);
				    }
				    scoreboard.add("  ", 5);
				    scoreboard.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "Queue", 4);
				    scoreboard.add(ChatColor.RED + "Gamemode: " + ChatColor.WHITE + QueueInfo.getQueue(e.getPlayer().getName()), 3);
				    scoreboard.add(ChatColor.RED + "Position: " + ChatColor.WHITE + QueueInfo.getPosition(e.getPlayer().getName()) + "/" + QueueInfo.getQueueInfo(QueueInfo.getQueue(e.getPlayer().getName())).getSize(), 2);
				    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&8&m----------------------"), 1);
					scoreboard.update();
	    		}
	    	}
	    }, 5L, 5L);
	}
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		this.setupPermissions();
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
        	public void run() {
        		refreshOnline("ALL");
        	}
        }, 50L, 50L);
        
        if(Bukkit.getOnlinePlayers().length > 0) {
        	for(Player p : Bukkit.getOnlinePlayers()) {
        		 SimpleScoreboard scoreboard = new SimpleScoreboard("§c§lHCRiots");
        		    scoreboard.send(p);
        		    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
        				public void run() {
        					int onlinePlayers = getOnlinePlayers("ALL");
        		    		if(!isInQueue(p)) {
        		    			if(scoreboard.get(11, "&c&8&m----------------------") != null) {
        		    				scoreboard.remove(11, "&c&8&m----------------------");
        		    			}
        					    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&c&8&m----------------------"), 10);
        						scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lOnline"), 9);
        						scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&f" + onlinePlayers + "/1500"), 8);
        						scoreboard.add("   ", 7);
        						scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lRank"), 6);
        					    if(perms.getPrimaryGroup(p).equalsIgnoreCase("Owner")) {
        					    	scoreboard.add(ChatColor.DARK_RED + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Manager")) {
        					    	scoreboard.add(ChatColor.GREEN + perms.getPrimaryGroup(p), 5);
        		    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("PlatformAdmin")) {
        					    	scoreboard.add(ChatColor.RED.toString() + ChatColor.ITALIC + perms.getPrimaryGroup(p), 5);
        		    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Mod+") || perms.getPrimaryGroup(p).equalsIgnoreCase("Twitch")) {
        					    	scoreboard.add(ChatColor.DARK_PURPLE + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("TrialMod")) {
        					    	scoreboard.add(ChatColor.DARK_AQUA + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Titanium")) {
        					    	scoreboard.add(ChatColor.LIGHT_PURPLE + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Sapphire") || perms.getPrimaryGroup(p).equalsIgnoreCase("Mod")) {
        					    	scoreboard.add(ChatColor.AQUA + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Ruby") || perms.getPrimaryGroup(p).equalsIgnoreCase("Youtuber") || perms.getPrimaryGroup(p).equalsIgnoreCase("Admin") || perms.getPrimaryGroup(p).equalsIgnoreCase("SeniorAdmin")) {
        					    	scoreboard.add(ChatColor.RED + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Cobalt")) {
        					    	scoreboard.add(ChatColor.BLUE + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Silver")) {
        					    	scoreboard.add(ChatColor.GRAY + perms.getPrimaryGroup(p), 5);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Bronze") || perms.getPrimaryGroup(p).equalsIgnoreCase("Developer")){
        					    	scoreboard.add(ChatColor.GOLD + perms.getPrimaryGroup(p), 5);
        					    } else {
        					    	scoreboard.add(ChatColor.WHITE + perms.getPrimaryGroup(p), 5);
        					    }
        					    scoreboard.add("  ", 4);
        					    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lWebsite"), 3);
        					    scoreboard.add(ChatColor.WHITE.toString() + "www.hcriots.com", 2);
        					    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&8&m----------------------"), 1);
        						scoreboard.update();
        		    		} else {
        					    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&c&8&m----------------------"), 11);
        						scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lOnline"), 10);
        						scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&f" + onlinePlayers + "/1500"), 9);
        						scoreboard.add("   ", 8);
        						scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&7&lRank"), 7);
        					    if(perms.getPrimaryGroup(p).equalsIgnoreCase("Owner")) {
        					    	scoreboard.add(ChatColor.DARK_RED + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Manager")) {
        					    	scoreboard.add(ChatColor.GREEN + perms.getPrimaryGroup(p), 6);
        		    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("PlatformAdmin")) {
        					    	scoreboard.add(ChatColor.RED.toString() + ChatColor.ITALIC + perms.getPrimaryGroup(p), 6);
        		    		    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Mod+") || perms.getPrimaryGroup(p).equalsIgnoreCase("Twitch")) {
        					    	scoreboard.add(ChatColor.DARK_PURPLE + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("TrialMod")) {
        					    	scoreboard.add(ChatColor.DARK_AQUA + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Titanium")) {
        					    	scoreboard.add(ChatColor.LIGHT_PURPLE + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Sapphire") || perms.getPrimaryGroup(p).equalsIgnoreCase("Mod")) {
        					    	scoreboard.add(ChatColor.AQUA + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Ruby") || perms.getPrimaryGroup(p).equalsIgnoreCase("Youtuber") || perms.getPrimaryGroup(p).equalsIgnoreCase("Admin") || perms.getPrimaryGroup(p).equalsIgnoreCase("SeniorAdmin")) {
        					    	scoreboard.add(ChatColor.RED + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Cobalt")) {
        					    	scoreboard.add(ChatColor.BLUE + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Silver")) {
        					    	scoreboard.add(ChatColor.GRAY + perms.getPrimaryGroup(p), 6);
        					    } else if(perms.getPrimaryGroup(p).equalsIgnoreCase("Bronze") || perms.getPrimaryGroup(p).equalsIgnoreCase("Developer")){
        					    	scoreboard.add(ChatColor.GOLD + perms.getPrimaryGroup(p), 6);
        					    } else {
        					    	scoreboard.add(ChatColor.WHITE + perms.getPrimaryGroup(p), 6);
        					    }
        					    scoreboard.add("  ", 5);
        					    scoreboard.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "Queue", 4);
        					    scoreboard.add(ChatColor.RED + "Gamemode: " + ChatColor.WHITE + QueueInfo.getQueue(p.getName()), 3);
        					    scoreboard.add(ChatColor.RED + "Position: " + ChatColor.WHITE + QueueInfo.getPosition(p.getName()) + "/" + QueueInfo.getQueueInfo(QueueInfo.getQueue(p.getName())).getSize(), 2);
        					    scoreboard.add(ChatColor.translateAlternateColorCodes('&', "&8&m----------------------"), 1);
        						scoreboard.update();
        		    		}
        		    	}
        		    }, 5L, 5L);
        	}
        }
	}

}
