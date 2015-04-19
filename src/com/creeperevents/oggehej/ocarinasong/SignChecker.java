package com.creeperevents.oggehej.ocarinasong;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.Button;
import org.bukkit.scheduler.BukkitRunnable;

class SignChecker extends BukkitRunnable
{
	Player player;
	OcarinaSong plugin;
	int radius;
	Songs song;

	SignChecker(Player player, OcarinaSong plugin, Songs song)
	{
		this.plugin = plugin;
		this.player = player;
		this.song = song;
		radius = plugin.getConfig().getInt("DetectorRadius");
	}

	@SuppressWarnings("deprecation")
	public void run()
	{
		int playerX = player.getLocation().getBlockX();
		int playerY = player.getLocation().getBlockY();
		int playerZ = player.getLocation().getBlockZ();

		for(int x = playerX - radius; x < playerX + radius; x++)
			for(int z = playerZ - radius; z < playerZ + radius; z++)
				for(int y = playerY - radius; y < playerY + radius; y++)
				{
					BlockState bs = player.getWorld().getBlockAt(x, y, z).getState();
					if (bs.getType() == Material.WALL_SIGN)
					{
						Sign sign = (Sign) bs;
						if(!sign.getLine(0).equals("§b[" + song.signName + "]"))
							continue;

						int maxDistance = radius;
						try
						{
							int signMax = Integer.parseInt(sign.getLine(1));
							if (signMax > 0)
								maxDistance = signMax;
						}
						catch (NumberFormatException err) {}

						if(!(player.getLocation().distance(sign.getLocation()) < maxDistance))
							continue;

						Block block = null;
						switch (sign.getBlock().getData())
						{
						case 2:
							block = sign.getBlock().getRelative(BlockFace.SOUTH);
							break;
						case 3:
							block = sign.getBlock().getRelative(BlockFace.NORTH);
							break;
						case 4:
							block = sign.getBlock().getRelative(BlockFace.EAST);
							break;
						case 5:
							block = sign.getBlock().getRelative(BlockFace.WEST);
						}

						for(BlockFace face : new BlockFace[] {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST})
						{
							Block mat = block.getRelative(face);
							if(mat.getType() == Material.STONE_BUTTON || mat.getType() == Material.WOOD_BUTTON)
							{
								BlockState state = mat.getState();

								int ticks = 20;
								try
								{
									if (Integer.parseInt(sign.getLine(2)) > 0)
										ticks = Integer.parseInt(sign.getLine(2));
								} catch (NumberFormatException e) {}

								new CustomRunnable((Button) state.getData(), state, ticks).runTask(plugin);
							}
						}
					}
				}
	}

	private class CustomRunnable extends BukkitRunnable {
		Button button;
		int ticks;
		BlockState state;
		CustomRunnable(Button button, BlockState state, int ticks) {
			this.button = button;
			this.ticks = ticks;
			this.state = state;
		}
		@Override
		public void run() {
			button.setPowered(true);
			state.update();
			new UberCustomRunnable(button, state).runTaskLater(plugin, ticks);
		}

		class UberCustomRunnable extends BukkitRunnable {
			Button button;
			BlockState state;
			UberCustomRunnable(Button button, BlockState state) {
				this.button = button;
				this.state = state;
			}
			@Override
			public void run() {
				button.setPowered(false);
				state.update();
			}
		}
	}
}
