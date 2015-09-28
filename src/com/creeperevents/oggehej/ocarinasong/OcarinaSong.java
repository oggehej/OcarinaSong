package com.creeperevents.oggehej.ocarinasong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.MetricsLite;

public class OcarinaSong extends JavaPlugin implements CommandExecutor {
	WeakHashMap<Player, SizedStack<ONote>> playingPlayers = new WeakHashMap<Player, SizedStack<ONote>>();
	WeakHashMap<Player, Boolean> cooldown = new WeakHashMap<Player, Boolean>();
	WeakHashMap<Player, BukkitTask> isReplay = new WeakHashMap<Player, BukkitTask>();
	WeakHashMap<Player, Boolean> isTaming = new WeakHashMap<Player, Boolean>();

	public void onEnable() {
		// Register listeners and commands
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getCommand("ocarina").setExecutor(this);

		// Prepare configuration
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Create recipe
		ItemStack lapis = new ItemStack(Material.INK_SACK);
		lapis.setDurability((short) 4);

		ItemStack ocarina = new ItemStack(lapis);
		ItemMeta ocarinaMeta = ocarina.getItemMeta();
		ocarinaMeta.setDisplayName("Ocarina");
		List<String> lore = new ArrayList<String>();
		lore.add("Right click to play");
		ocarinaMeta.setLore(lore);
		ocarina.setItemMeta(ocarinaMeta);

		ShapedRecipe ocarinaRecipe = new ShapedRecipe(ocarina);
		ocarinaRecipe.shape("lll", "bbb", "lll");
		ocarinaRecipe.setIngredient('l', lapis.getData());
		ocarinaRecipe.setIngredient('b', Material.CLAY);
		getServer().addRecipe(ocarinaRecipe);

		// Initialise metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {}
	}

	public void onDisable() {
		playingPlayers.clear();
		cooldown.clear();
		isReplay.clear();
		isTaming.clear();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length >= 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("ocarina.reload")) {
			reloadConfig();
			sender.sendMessage(ChatColor.AQUA + "Config reloaded!");
		} else {
			sender.sendMessage(ChatColor.AQUA + "Songs you can play: ");
			sender.sendMessage(ChatColor.GRAY + "Song of Storms:"      + ChatColor.YELLOW + " S ↓ ↑ S ↓ ↑");
			sender.sendMessage(ChatColor.GRAY + "Song of Time:"        + ChatColor.YELLOW + " → S ↓ → S ↓");
			sender.sendMessage(ChatColor.GRAY + "Song of Healing:"     + ChatColor.YELLOW + " ← → ↓ ← → ↓");
			sender.sendMessage(ChatColor.GRAY + "Zelda's Lullaby:"     + ChatColor.YELLOW + " ← ↑ → ← ↑ →");
			sender.sendMessage(ChatColor.GRAY + "Epona's Song:"        + ChatColor.YELLOW + " ↑ ← → ↑ ← →");
			sender.sendMessage(ChatColor.GRAY + "Sonata of Awakening:" + ChatColor.YELLOW + " ↑ ← ↑ ← S → S");
		}
		return true;
	}

	/**
	 * Let the player play a note
	 * @param player Player
	 * @param note Note
	 */
	void playNote(Player player, ONote note) {
		playingPlayers.get(player).push(note);

		player.getWorld().playSound(player.getLocation(), Sound.NOTE_PIANO, 1, note.getPitch());
		player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.NOTE, 0);

		songCheck(player);
	}

	/**
	 * Remove player from the "play" mode
	 * @param player Player
	 */
	public void stopPlaying(Player player) {
		if(isReplay.containsKey(player)) {
			isReplay.get(player).cancel();
			isReplay.remove(player);
		}

		player.sendMessage(ChatColor.YELLOW + "You stopped playing your ocarina");
		playingPlayers.remove(player);
	}

	/**
	 * Check if the player has played a valid melody and execute associated methods
	 * @param player Player
	 * @return Has played a melody
	 */
	boolean songCheck(Player player) {
		ONote[] array = playingPlayers.get(player).toArray(new ONote[0]);
		ArrayUtils.reverse(array);
		Songs song = Songs.getSongFromNotes(array);
		if(song == null)
			return false;
		else if(!player.hasPermission(song.getBasePermission())) {
			player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
			return false;
		}

		player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, .5f, 1);

		BukkitTask runnable = new SongRunnable(song, player, this).runTaskTimer(this, 20L, 4L);

		isReplay.put(player, runnable);

		return true;
	}

	private class SongRunnable extends BukkitRunnable {
		Songs song;
		Player player;
		OcarinaSong plugin;
		int n = 0;

		SongRunnable(Songs song, Player player, OcarinaSong plugin) {
			this.song = song;
			this.player = player;
			this.plugin = plugin;
		}

		@Override
		public void run() {
			if(song.song[n] != 0) {
				player.getWorld().playSound(player.getLocation(), Sound.NOTE_PIANO, 1, ONote.toPitch(song.song[n]));
				player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.NOTE, 0);
			}

			if(++n >= song.song.length) {
				player.sendMessage(ChatColor.AQUA + "You played "
						+ ChatColor.GRAY + WordUtils.capitalizeFully(song.toString().replace('_', ' ')) + ChatColor.AQUA + "!");
				song.run(player, plugin);
				if(song.signName != null && player.hasPermission("ocarina.sign.play." + song.signName.toLowerCase()))
					new SignChecker(player, plugin, song).runTaskAsynchronously(plugin);
				isReplay.remove(player);
				playingPlayers.remove(player);
				cancel();
			}
		}
	}
}
