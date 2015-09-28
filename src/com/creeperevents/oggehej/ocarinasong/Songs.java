package com.creeperevents.oggehej.ocarinasong;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

public enum Songs {
	EPONAS_SONG(new ONote[]{ONote.UP, ONote.LEFT, ONote.RIGHT, ONote.UP, ONote.LEFT, ONote.RIGHT},
			new Byte[]{20, 0, 17, 0, 15, 0, 0, 0, 20, 0, 17, 0, 15, 0, 0, 0, 20, 0, 17, 0, 15, 0, 0, 0, 17, 0, 0, 0, 15},
			null, "epona") {
				public void run(Player player, OcarinaSong plugin) {
					int entityCount = 0;

					// Get all entities that has been tamed by the player
					List<Entity> entities = player.getLocation().getWorld().getEntities();
					for(Entity entity : entities)
						if(entity.hasMetadata("tamed") && entity.getMetadata("tamed").get(0).asString().equals(player.getUniqueId().toString())) {
							entity.teleport(player.getLocation().add(1.0D, 0.0D, 1.0D));
							entityCount++;
						}

					if(entityCount == 1)
						player.sendMessage(ChatColor.AQUA + "Your pet is coming!");
					else if(entityCount > 1)
						player.sendMessage(ChatColor.AQUA + "Your pets are coming!");
					else
						player.sendMessage(ChatColor.RED + "It seems like your pet is non-existent");
				}
			},
	SONATA_OF_AWAKENING(new ONote[]{ONote.UP, ONote.LEFT, ONote.UP, ONote.LEFT, ONote.A, ONote.RIGHT, ONote.A},
			new Byte[]{20, 17, 20, 17, 0, 8, 0, 15, 0, 0, 0, 8, 0, 0, 0, 0, 20, 17, 20, 17, 0, 8, 0, 15},
			"Awaken", "awaken") {
				public void run(Player player, OcarinaSong plugin) {}
			},
	SONG_OF_HEALING(new ONote[]{ONote.LEFT, ONote.RIGHT, ONote.DOWN, ONote.LEFT, ONote.RIGHT, ONote.DOWN},
			new Byte[]{17, 0, 15, 0, 11, 0, 0, 0, 17, 0, 15, 0, 11, 0, 0, 0, 17, 0, 15, 0, 10, 8, 10},
			null, "healing") {
				public void run(Player player, OcarinaSong plugin) {
					if (player.getHealth() <= 18)
						player.setHealth(player.getHealth() + 2);
					else
						player.setHealth(20);

					int radius = plugin.getConfig().getInt("HealingRadius");
					for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
						if(player.hasPermission("ocarina.play.healing.animal")
								&& (entity instanceof Animals || entity instanceof Player || entity instanceof Squid)) {
							Damageable animal = (Damageable) entity;
							
							if (animal.getHealth() <= animal.getMaxHealth() - 2)
								animal.setHealth(animal.getHealth() + 2);
							else
								animal.setHealth(animal.getMaxHealth());
						} else if(entity instanceof PigZombie) {
							if(player.hasPermission("ocarina.play.healing.pig")) {
								PigZombie pig = (PigZombie) entity;
								Location loc = pig.getLocation();
								pig.remove();
								loc.getWorld().spawnEntity(loc, EntityType.PIG);
							}
						} else if (player.hasPermission("ocarina.play.healing.burn")) {
							if(entity instanceof Enderman) {
								// The Enderman has to be killed in another way
								Enderman enderman = (Enderman) entity;
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.EXTINGUISH, 1);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.EXTINGUISH, 2);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.EXTINGUISH, 3);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.SMOKE, 4);
								enderman.getWorld().playEffect(enderman.getEyeLocation(), Effect.SMOKE, 4);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.BOW_FIRE, 0);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.SMOKE, 3);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.SMOKE, 2);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.SMOKE, 1);
								enderman.getWorld().playEffect(enderman.getLocation(), Effect.SMOKE, 0);

								class CustomRunnable extends BukkitRunnable {
									Enderman ender;
									CustomRunnable(Enderman ender) {
										this.ender = ender;
									}
									@Override
									public void run() {
										ender.setHealth(0);
									}
								}
								new CustomRunnable(enderman).runTaskLater(plugin, 20);
							} else if(entity instanceof Monster) {
								Monster thismob = (Monster) entity;
								thismob.setFireTicks(1000);
								if(thismob instanceof Creeper)
									((Creeper) thismob).setPowered(false);
							}
							
						}
					}
				}
			},
	SONG_OF_STORMS(new ONote[]{ONote.A, ONote.DOWN, ONote.UP, ONote.A, ONote.DOWN, ONote.UP},
			new Byte[]{8, 11, 20, 0, 8, 11, 20, 0, 0, 22, 0, 23, 22, 23, 22, 18, 15},
			null, "storms") {
				public void run(Player player, OcarinaSong plugin) {
					for(Player p : player.getWorld().getPlayers())
						if(p.hasPermission("ocarina.broadcast.storm"))
							p.sendMessage(ChatColor.GREEN + player.getDisplayName() + " has changed the weather!");
					player.getWorld().setStorm(!player.getWorld().hasStorm());
				}
			},
	SONG_OF_TIME(new ONote[]{ONote.RIGHT, ONote.A, ONote.DOWN, ONote.RIGHT, ONote.A, ONote.DOWN},
			new Byte[]{15, 0, 8, 0, 0, 0, 11, 0, 15, 0, 8, 0, 0, 0, 11, 0, 15, 18, 17, 0, 13, 0, 11, 13, 15, 0, 8, 0, 6, 10, 8},
			"Time", "time") {
				public void run(Player player, OcarinaSong plugin) {
					for(Player p : player.getWorld().getPlayers())
						if(p.hasPermission("ocarina.broadcast.time"))
							p.sendMessage(ChatColor.GREEN + player.getDisplayName() + " has changed the time of day!");
					player.getWorld().setTime(player.getWorld().getTime() + 12000);
				}
			},
	ZELDAS_LULLABY(new ONote[]{ONote.LEFT, ONote.UP, ONote.RIGHT, ONote.LEFT, ONote.UP, ONote.RIGHT},
			new Byte[]{17, 0, 0, 0, 20, 0, 15, 0, 0, 0, 0, 0, 17, 0, 0, 0, 20, 0, 15, 0, 0, 0, 0, 0, 17, 0, 0, 0, 20, 0, 24, 0, 0, 0, 22, 0, 20},
			"Zelda", "zelda") {
				public void run(Player player, OcarinaSong plugin) {
					int radius = plugin.getConfig().getInt("CalmRadius");
					for (Entity entity : player.getNearbyEntities(radius, radius, radius))
						if(entity instanceof Wolf) {
							// Calm wolf if angry
							Wolf wolf = (Wolf) entity;
							if (wolf.isAngry()) {
								AnimalTamer owner = wolf.getOwner();
								Location loc = wolf.getLocation();
								wolf.remove();

								wolf = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
								if (owner != null) {
									wolf.setOwner(owner);
									wolf.setTamed(true);
								}
							}
							wolf.setSitting(true);
						}

					if (player.hasPermission("ocarina.play.zelda.tame")) {
						plugin.isTaming.put(player, true);

						player.sendMessage(ChatColor.AQUA + "To tame an Animal, rightclick it with an apple.");
						player.sendMessage(ChatColor.AQUA + "To train a Monster, weaken it, then rightclick it with a bone.");

						class CustomRunnable extends BukkitRunnable {
							OcarinaSong plugin;
							Player player;

							CustomRunnable(Player player, OcarinaSong plugin) {
								this.plugin = plugin;
								this.player = player;
							}

							@Override
							public void run() {
								plugin.isTaming.remove(player);
							}
						}

						new CustomRunnable(player, plugin).runTaskLater(plugin, 200L);
					}
				}
			};

	ONote[] notes;
	Byte[] song;
	String signName;
	String perm;

	Songs(ONote[] notes, Byte[] song, String signName, String perm) {
		this.notes = notes;
		this.song = song;
		this.signName = signName;
		this.perm = perm;
	}

	/**
	 * Get the permission required to play the song
	 * @return Permission
	 */
	public Permission getBasePermission() {
		return new Permission("ocarina.play." + perm);
	}

	/**
	 * Get the song based on an array of {@code ONote[]}
	 * @param playedNotes
	 * @return
	 */
	public static Songs getSongFromNotes(ONote[] playedNotes) {
		if(playedNotes != null) {
			songs:
			for(Songs song : Songs.values()) {
				if(song.notes.length > playedNotes.length)
					continue;

				for(int i = 0; i < song.notes.length; i++)
					if(!song.notes[i].equals(playedNotes[song.notes.length - 1 - i]))
						continue songs;

				return song;
			}
		}
		return null;
	}

	/**
	 * Get song based of the text put on a song detector
	 * @param string Text on {@code Sign}
	 * @return Song
	 */
	public static Songs getSongFromSign(String string) {
		for(Songs song : Songs.values()) {
			if(song.signName != null && song.signName.equalsIgnoreCase(string))
				return song;
		}
		return null;
	}

	/**
	 * Run the game mechanic associated with the song
	 * @param player Player that executed the action
	 * @param plugin The {@code CreeperEventsGui} instance
	 */
	public abstract void run(Player player, OcarinaSong plugin);
}
