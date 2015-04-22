package com.creeperevents.oggehej.ocarinasong;

import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener
{
	OcarinaSong plugin;
	PlayerListener(OcarinaSong instance)
	{
		plugin = instance;
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event)
	{
		if(plugin.playingPlayers.containsKey(event.getPlayer()))
			plugin.stopPlaying(event.getPlayer());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				&& player.hasPermission("ocarina")
				&& player.getItemInHand().hasItemMeta()
				&& player.getItemInHand().getItemMeta().hasDisplayName()
				&& player.getItemInHand().getItemMeta().getDisplayName().equals("Ocarina")
				&& player.getItemInHand().getItemMeta().hasLore())
		{
			if(!plugin.playingPlayers.containsKey(player))
			{
				plugin.playingPlayers.put(player, new SizedStack<ONote>());
				player.sendMessage(ChatColor.YELLOW + "You started to play the ocarina");
			}
			else
				plugin.stopPlaying(player);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		if(event.getRightClicked() instanceof LivingEntity && !(event.getRightClicked() instanceof Player))
		{
			Player player = event.getPlayer();
			LivingEntity entity = (LivingEntity) event.getRightClicked();

			if(entity.hasMetadata("tamed"))
			{
				if(entity.getMetadata("tamed").get(0).asString().equals(player.getUniqueId().toString()))
				{
					if(entity.getPassenger() == player)
					{
						entity.eject();
						player.sendMessage(ChatColor.AQUA + "You dismount your pet!");
					}
					else if(entity.getPassenger() == null)
					{
						entity.setPassenger(player);
						player.sendMessage(ChatColor.AQUA + "You mount your pet!");
					}
					else if(player.hasPermission("ocarina.tugjockey"))
					{
						Random generator = new Random();
						Integer inty = Integer.valueOf(generator.nextInt(9));
						if (inty.intValue() == 0)
						{
							player.sendMessage(ChatColor.RED + "You have dismounted the Jockey!");
							if ((entity.getPassenger() instanceof Player))
								((Player)entity).sendMessage(ChatColor.RED + player.getName() + " has dismounted you from your pet!");
							entity.eject();
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You tug at the Jockey!");
							// TODO: Knockback
						}
					}
					event.setCancelled(true);
				}
			}
			else if(plugin.isTaming.containsKey(player))
			{
				event.setCancelled(true);
				if (player.getItemInHand().getType() == Material.APPLE)
				{
					if(entity instanceof Creature && !(entity instanceof Tameable))
					{
						entity.setMetadata("tamed", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
						player.sendMessage(ChatColor.AQUA + "You tame the majestic creature!");
						if (player.getItemInHand().getAmount() != 1)
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						else
							player.getInventory().clear(player.getInventory().getHeldItemSlot());
					}
					else
						player.sendMessage(ChatColor.RED + "You cannot tame this with an apple!");
				}
				else if(player.getItemInHand().getType() == Material.BONE)
				{
					if(entity instanceof Monster)
					{
						if(entity.getHealth() < 8)
						{
							entity.setMetadata("tamed", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
							player.sendMessage(ChatColor.AQUA + "You tame the wild beast!");
							if (player.getItemInHand().getAmount() != 1)
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							else
								player.getInventory().clear(player.getInventory().getHeldItemSlot());
						}
						else
							player.sendMessage(ChatColor.RED + "The monster is too strong to be tamed!");
					}
					else
						player.sendMessage(ChatColor.RED + "You cannot tame this with a bone!");
				}
				else
					player.sendMessage(ChatColor.RED + "You cannot tame this with a " + player.getItemInHand().getType().toString());
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event)
	{
		if (event.getEntity().hasMetadata("tamed")
				&& (event.getTarget() instanceof Player)
				&& event.getEntity().getMetadata("tamed").get(0).asString().equals(event.getTarget().getUniqueId().toString()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if(plugin.playingPlayers.containsKey(event.getPlayer()))
			plugin.stopPlaying(event.getPlayer());
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (event.getEntity().hasMetadata("tamed"))
		{
			Player rider = Bukkit.getServer().getPlayer(UUID.fromString(event.getEntity().getMetadata("tamed").get(0).asString()));

			if (rider != null && rider.isOnline())
				rider.sendMessage(ChatColor.RED + "Your pet died!");
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		if(event.getBlock().getType() == Material.WALL_SIGN)
		{
			String content = event.getLine(0).replace("§b", "");

			if(!content.replaceAll("\\[(.*?)\\]", "").equals(""))
				return;

			Songs song = Songs.getSongFromSign(content.replaceAll("\\[|\\]", ""));
			if(song == null)
				return;

			if(!event.getPlayer().hasPermission("ocarina.sign.create." + song.signName.toLowerCase()))
			{
				event.getPlayer().sendMessage(ChatColor.BLUE + "You don't have permission to create that sign!");
				event.getBlock().breakNaturally();
				event.setCancelled(true);
				return;
			}

			try {
				if(Integer.parseInt(event.getLine(2)) > plugin.getConfig().getInt("DetectorRadius"))
				{
					event.getPlayer().sendMessage(ChatColor.BLUE + "That block radius is higher than the maximum!\n"
							+ "You can change that in the configuration file");
					event.getBlock().breakNaturally();
					event.setCancelled(true);
					return;
				}
			} catch(Exception e) {}

			event.setLine(0, "§b[" + song.signName + "]");
			event.getPlayer().sendMessage(ChatColor.AQUA + "Created " + ChatColor.GRAY
					+ WordUtils.capitalizeFully(song.toString().toLowerCase().replace('_', ' ')) + ChatColor.AQUA + " Detector!");
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Location from = event.getFrom();
		Location to = event.getTo();
		if(plugin.playingPlayers.containsKey(event.getPlayer())
				&& (from.getX() != to.getX() || from.getZ() != to.getZ()))
		{
			Player player = event.getPlayer();
			player.teleport(from);
			
			if(!plugin.cooldown.containsKey(player) && !plugin.isReplay.containsKey(player))
			{
				plugin.cooldown.put(player, true);

				class CustomRunnable extends BukkitRunnable {
					private Player player;
					CustomRunnable(Player player) {
						this.player = player;
					}
					@Override
					public void run() {
						plugin.cooldown.remove(player);
					}
				}
				new CustomRunnable(player).runTaskLater(plugin, 6L);

				double angle = Math.atan2(-(to.getX() - from.getX()), to.getZ() - from.getZ()) * 180D / Math.PI;
				int facing = (int) Math.round((angle - player.getLocation().getYaw()) / 45D % 8D);
				if (facing < 0)
					facing += 8;

				switch (facing)
				{
				case 0:
					plugin.playNote(player, ONote.UP);
					break;
				case 2:
					plugin.playNote(player, ONote.RIGHT);
					break;
				case 4:
					plugin.playNote(player, ONote.DOWN);
					break;
				case 6:
					plugin.playNote(player, ONote.LEFT);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		if(event.isSneaking()
				&& plugin.playingPlayers.containsKey(event.getPlayer())
				&& !plugin.isReplay.containsKey(event.getPlayer()))
		{
			plugin.playNote(event.getPlayer(), ONote.A);
			event.setCancelled(true);
		}
	}
}
