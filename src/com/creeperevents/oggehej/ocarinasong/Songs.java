package com.creeperevents.oggehej.ocarinasong;

import java.util.EnumSet;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

public enum Songs
{
	EPONAS_SONG(new ONote[]{ONote.UP, ONote.LEFT, ONote.RIGHT, ONote.UP, ONote.LEFT, ONote.RIGHT},
			new Byte[]{20, 0, 17, 0, 15, 0, 0, 0, 20, 0, 17, 0, 15, 0, 0, 0, 20, 0, 17, 0, 15, 0, 0, 0, 17, 0, 0, 0, 15},
			null, "epona")
			{
				public void run(Player player, OcarinaSong plugin)
				{
					LivingEntity livent = null;

					for(int i = 0; i < player.getLocation().getWorld().getEntities().size(); i++)
						if(player.getLocation().getWorld().getEntities().get(i).hasMetadata("tamed")
								&& player.getLocation().getWorld().getEntities().get(i).getMetadata("tamed").get(0).asString().equals(player.getUniqueId().toString()))
						{
							livent = (LivingEntity) player.getLocation().getWorld().getEntities().get(i);
							livent.teleport(player.getLocation().add(1.0D, 0.0D, 1.0D));
						}

					if(livent != null)
						player.sendMessage(ChatColor.AQUA + "Your pets are coming!");
					else
						player.sendMessage(ChatColor.RED + "It seems like your pet is non-existent");
				}
			},
	SONATA_OF_AWAKENING(new ONote[]{ONote.UP, ONote.LEFT, ONote.UP, ONote.LEFT, ONote.A, ONote.RIGHT, ONote.A},
			new Byte[]{20, 17, 20, 17, 0, 8, 0, 15, 0, 0, 0, 8, 0, 0, 0, 0, 20, 17, 20, 17, 0, 8, 0, 15},
			"Awaken", "awaken")
			{
				public void run(Player player, OcarinaSong plugin) {}
			},
	SONG_OF_HEALING(new ONote[]{ONote.LEFT, ONote.RIGHT, ONote.DOWN, ONote.LEFT, ONote.RIGHT, ONote.DOWN},
			new Byte[]{17, 0, 15, 0, 11, 0, 0, 0, 17, 0, 15, 0, 11, 0, 0, 0, 17, 0, 15, 0, 10, 8, 10},
			null, "healing")
			//{17, 0, 15, 0, 10, 8, 10, 0, 0, 0, 0, 17, 0, 15, 0, 11, 0, 17, 0, 15, 11}
			{
				public void run(Player player, OcarinaSong plugin)
				{
					if (player.getHealth() <= 18)
						player.setHealth(player.getHealth() + 2);
					else
						player.setHealth(20);

					int radius = plugin.getConfig().getInt("HealingRadius");
					for (Entity entity : player.getNearbyEntities(radius, radius, radius))
					{
						if(player.hasPermission("ocarina.play.healing.animal")
								&& (entity instanceof Animals || entity instanceof Player || entity instanceof Squid))
						{
							Damageable animal = (Damageable) entity;
							
							if (animal.getHealth() <= animal.getMaxHealth() - 2)
								animal.setHealth(animal.getHealth() + 2);
							else
								animal.setHealth(animal.getMaxHealth());
						}
						else if(entity instanceof PigZombie)
						{
							if(player.hasPermission("ocarina.play.healing.pig"))
							{
								PigZombie pig = (PigZombie) entity;
								Location loc = pig.getLocation();
								pig.remove();
								loc.getWorld().spawnEntity(loc, EntityType.PIG);
							}
						}
						else if (player.hasPermission("ocarina.play.healing.burn"))
						{
							if(entity instanceof Enderman)
							{
								Enderman thismob = (Enderman) entity;
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.EXTINGUISH, 1);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.EXTINGUISH, 2);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.EXTINGUISH, 3);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.SMOKE, 4);
								thismob.getWorld().playEffect(thismob.getEyeLocation(), Effect.SMOKE, 4);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.BOW_FIRE, 0);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.SMOKE, 3);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.SMOKE, 2);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.SMOKE, 1);
								thismob.getWorld().playEffect(thismob.getLocation(), Effect.SMOKE, 0);
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
								new CustomRunnable(thismob).runTaskLater(plugin, 20);
							}
							else if(entity instanceof Monster)
							{
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
			null, "storms")
			//{8, 11, 20, 0, 8, 11, 20, 0, 0, 22, 0, 23, 22, 23, 22, 18, 15, 0, 15, 0, 8, 0, 11, 13, 15, 0, 0, 15, 0, 8, 0, 11, 13, 10}
			{
				public void run(Player player, OcarinaSong plugin)
				{
					for(Player p : player.getWorld().getPlayers())
						if(p.hasPermission("ocarina.broadcast.storm"))
							p.sendMessage(ChatColor.GREEN + player.getDisplayName() + " has changed the weather!");
					player.getWorld().setStorm(!player.getWorld().hasStorm());
				}
			},
	SONG_OF_TIME(new ONote[]{ONote.RIGHT, ONote.A, ONote.DOWN, ONote.RIGHT, ONote.A, ONote.DOWN},
			new Byte[]{15, 0, 8, 0, 0, 0, 11, 0, 15, 0, 8, 0, 0, 0, 11, 0, 15, 18, 17, 0, 13, 0, 11, 13, 15, 0, 8, 0, 6, 10, 8},
			"Time", "time")
			{
				public void run(Player player, OcarinaSong plugin)
				{
					for(Player p : player.getWorld().getPlayers())
						if(p.hasPermission("ocarina.broadcast.time"))
							p.sendMessage(ChatColor.GREEN + player.getDisplayName() + " has changed the time of day!");
					player.getWorld().setTime(player.getWorld().getTime() + 12000);
				}
			},
	ZELDAS_LULLABY(new ONote[]{ONote.LEFT, ONote.UP, ONote.RIGHT, ONote.LEFT, ONote.UP, ONote.RIGHT},
			new Byte[]{17, 0, 0, 0, 20, 0, 15, 0, 0, 0, 0, 0, 17, 0, 0, 0, 20, 0, 15, 0, 0, 0, 0, 0, 17, 0, 0, 0, 20, 0, 24, 0, 0, 0, 22, 0, 20},
			"Zelda", "zelda")
			//{17, 0, 0, 0, 20, 0, 15, 0, 0, 0, 13, 15, 17, 0, 0, 0, 20, 0, 15, 0, 0, 0, 0, 0, 17, 0, 0, 20, 0, 24, 0, 0, 22, 0, 20, 0, 0, 18, 0, 17, 0, 15}
			{
				public void run(Player player, OcarinaSong plugin)
				{
					int radius = plugin.getConfig().getInt("CalmRadius");
					for (Entity entity : player.getNearbyEntities(radius, radius, radius))
						if(entity instanceof Wolf)
						{
							Wolf wolf = (Wolf) entity;
							if (wolf.isAngry())
							{
								AnimalTamer owner = wolf.getOwner();
								Location loc = wolf.getLocation();
								wolf.remove();

								wolf = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
								if (owner != null)
								{
									wolf.setOwner(owner);
									wolf.setTamed(true);
								}
							}
							wolf.setSitting(true);
						}

					if (player.hasPermission("ocarina.play.zelda.tame"))
					{
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
						
						new CustomRunnable(player, plugin).runTaskLater(plugin, 200l);
					}
				}
			};

	ONote[] notes;
	Byte[] song;
	String signName;
	String perm;

	Songs(ONote[] notes, Byte[] song, String signName, String perm)
	{
		this.notes = notes;
		this.song = song;
		this.signName = signName;
		this.perm = perm;
	}
	
	public Permission getBasePermission()
	{
		return new Permission("ocarina.play." + perm);
	}

	public static Songs getSongFromNotes(ONote[] playedNotes)
	{
		if(playedNotes != null)
		{
			for(Songs song : EnumSet.allOf(Songs.class))
			{
				boolean match = true;
				for(int i = 0; i < song.notes.length; i++)
					if(song.notes.length > playedNotes.length || !song.notes[i].equals(playedNotes[song.notes.length - 1 - i]))
					{
						match = false;
						break;
					}

				if(match)
					return song;
			}
		}
		return null;
	}

	public static Songs getSongFromSign(String string)
	{
		for(Songs song : EnumSet.allOf(Songs.class))
		{
			if(song.signName != null && song.signName.equalsIgnoreCase(string))
				return song;
		}
		return null;
	}

	public abstract void run(Player player, OcarinaSong plugin);
}
