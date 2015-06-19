package main.java.com.github.erozabesu.YPLKart.Listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.com.github.erozabesu.YPLKart.Scoreboards;
import main.java.com.github.erozabesu.YPLKart.YPLKart;
import main.java.com.github.erozabesu.YPLKart.Data.DisplayKartData;
import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumCharacter;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumItem;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumKarts;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumSelectMenu;
import main.java.com.github.erozabesu.YPLKart.Enum.Permission;
import main.java.com.github.erozabesu.YPLKart.Object.Race;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Task.SendBlinkingTitle;
import main.java.com.github.erozabesu.YPLKart.Utils.PacketUtil;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class EventData extends RaceManager implements Listener {

	private YPLKart pl;
	public EventData(YPLKart plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		pl = plugin;
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		if(!Settings.isEnable(e.getWorld()))return;
		DisplayKartData.respawnKart(e.getChunk());
	}

	//ドリフト
	//カートに乗っている間はコマンド以外の手段では搭乗解除不可
	//leaveはゴール時、コマンド実行時のみ
	@EventHandler
	public void onVehicleExit(VehicleExitEvent e){
		if(!(e.getExited() instanceof Player))return;
		if(!Settings.isEnable(e.getExited().getWorld()))return;
		if(!RaceManager.isCustomMinecart(e.getVehicle()))return;

		Player p = (Player) e.getExited();
		if(!getRace(p).getCMDFroceLeave()){
			e.setCancelled(true);
			return;
		}
	}

	//搭乗に成功した場合データ登録+パケット再送
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e){
		if(!Settings.isEnable(e.getEntered().getWorld()))return;
		if(!(e.getEntered() instanceof Player))return;

		PacketUtil.runPlayerLookingUpdate((Player) e.getEntered());
		if(RaceManager.isCustomMinecart(e.getVehicle())){
			EnumKarts kart = EnumKarts.getKartfromEntity(e.getVehicle());
			if(kart == null)return;

			RaceManager.ride((Player) e.getEntered(), kart);
		}
	}

	//カートを右クリック
	@EventHandler
	public void interactKart(PlayerInteractEntityEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(!RaceManager.isCustomMinecart(e.getRightClicked()))return;
		if(!Permission.hasPermission(e.getPlayer(), Permission.kart_ride, false))
			e.setCancelled(true);
	}

	@EventHandler
	public void removeCheckPoint(PlayerMoveEvent e){
		if(!Settings.isEnable(e.getFrom().getWorld()))return;
		Player p = e.getPlayer();
		if(!EnumItem.CheckPoint.isSimilar(p.getItemInHand()))return;

		List<Entity> list = p.getNearbyEntities(1, 1, 1);
		for(Entity entity : list){
			if(isCustomWitherSkull(entity, p.getItemInHand().getItemMeta().getLore().get(0)))
				if(entity.getLocation().distance(p.getLocation()) < 1.5){
					p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
					entity.remove();
					Util.sendMessage(p, "チェックポイントを削除しました");
					break;
				}
		}
	}

	@EventHandler
	public void saveLastStepBlock(PlayerMoveEvent e){
		if(!Settings.isEnable(e.getFrom().getWorld()))return;
		if(!isEntry(e.getPlayer()))return;

		getRace(e.getPlayer()).setLastStepBlock(Util.getStepBlock(e.getFrom()));
	}

	@EventHandler
	public void saveLapcount(PlayerMoveEvent e){
		if(!Settings.isEnable(e.getFrom().getWorld()))return;
		Player p = e.getPlayer();
		if(!isEntry(p))return;
		Race r = getRace(p);
		if(r.getLapStepCool())return;

		int lapcount = r.getLapCount();

		if(r.getLastStepBlock().equalsIgnoreCase(Settings.StartBlock)){
			if(Util.getStepBlock(e.getTo()).equalsIgnoreCase(Settings.GoalBlock)){
				//正常ルート
				if(lapcount + 1 == Settings.NumberOfLaps){
					r.setGoal();
				}else{
					if(lapcount == 0){
						r.setStart(true, e.getFrom(), e.getTo());
					}else{
						Util.sendMessage(p, (lapcount + 1) + "周目突入！");
					}
					r.setLapCount(lapcount + 1);
				}
				r.setCool();
			}
		}else if(r.getLastStepBlock().equalsIgnoreCase(Settings.GoalBlock)){
			if(Util.getStepBlock(e.getTo()).equalsIgnoreCase(Settings.StartBlock)){
				//逆走
				Util.sendMessage(p, "逆走");
				if(lapcount <= 0)
					r.setLapCount(0);
				else{
					r.setLapCount(lapcount - 1);
				}
				r.setCool();
			}
		}
	}

	@EventHandler
	public void RunningRank(PlayerMoveEvent e) {//順位
		if(!Settings.isEnable(e.getFrom().getWorld()))return;
		Player p = e.getPlayer();
		if(!isEntry(p))return;
		if(getRace(p).getLapCount() < 1)return;

		Race r = getRace(p);

		ArrayList<String> checkpoint = getNearbyCheckpointID(e.getPlayer().getLocation(), 20, r.getEntry());
		if(checkpoint == null)return;

		Iterator<String> i = checkpoint.iterator();
		String id;
		String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
		while(i.hasNext()){
			id = i.next();
			r.addPassedCheckPoint(lap + id);
			r.setLastPassedCheckPoint(id);
		}
	}

	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(!(e.getEntity() instanceof Player))return;
		if(!isEntry((Player)e.getEntity()))return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;

		final Player p = e.getPlayer();
		Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
			public void run(){
				PacketUtil.runPlayerLookingUpdate(p);

				if(isEntry(p)){
					p.setWalkSpeed(getRace(p).getCharacter().getWalkSpeed());
					if(getRace(p).getKart() != null)
						try {
							RaceManager.setPassengerCustomMinecart(p, getRace(p).getKart());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
				}
			}
		}, 20);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;

		RaceManager.removeCustomMinecart(e.getPlayer());
		e.getPlayer().setWalkSpeed(0.2F);
		Scoreboards.clearBoard(e.getPlayer());
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(getRace(e.getPlayer()).getCharacter() == null)return;

		Bukkit.getScheduler().runTaskLater(pl, new Runnable(){
			public void run(){
				PacketUtil.runPlayerLookingUpdate(e.getPlayer());
			}
		}, 10);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		final Player p = e.getPlayer();
		final Race r = getRace(p);

		Bukkit.getScheduler().runTaskLater(pl, new Runnable(){
			public void run(){
					PacketUtil.runPlayerLookingUpdate(p);
					p.setSprinting(true);
					if(r.getKart() != null){
						try {
							RaceManager.setPassengerCustomMinecart(p, r.getKart());
							p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
			}
		}, 10);

		if(!isEntry(p))return;

		Bukkit.getScheduler().runTaskLater(pl, new Runnable(){
			public void run(){
				new SendBlinkingTitle(p, r.getCharacter().getDeathPenaltySecond(), "DEATH PENALTY", ChatColor.RED).runTaskTimer(YPLKart.getInstance(), 0, 1);
				p.setWalkSpeed(r.getCharacter().getDeathPenaltyWalkSpeed());
				p.setNoDamageTicks(r.getCharacter().getDeathPenaltyAntiReskillSecond() * 20);
			}
		}, 2);

		r.setDeathPenaltyTask(
			Bukkit.getScheduler().runTaskLater(pl, new Runnable(){
				public void run(){
					p.setWalkSpeed(r.getCharacter().getWalkSpeed());
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
					p.setSprinting(true);
					r.setDeathPenaltyTask(null);
				}
			}, r.getCharacter().getDeathPenaltySecond() * 20 + 3)
		);

		if(r.getLastPassedCheckPoint() != null){
			Location respawn = r.getLastPassedCheckPoint().getLocation().add(0, - checkPointHeight,0);
			e.setRespawnLocation(new Location(respawn.getWorld(), respawn.getX(), respawn.getY(), respawn.getZ(), r.getLastYaw(), 0));
		}

		//if(p.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("true"))return;
		//r.recoveryInventory();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		final Player p = (Player)e.getEntity();

		RaceManager.removeCustomMinecart(p);

		if(!isEntry(p))return;
		Race r = getRace(p);

		p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);
		Util.sendMessage(p, "デスペナルティ！");

		r.setLastYaw(p.getLocation().getYaw());

		if(p.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("true"))return;
		e.getDrops().clear();
		//r.saveInventory();
		e.setKeepInventory(true);

		Bukkit.getScheduler().scheduleSyncDelayedTask(YPLKart.getInstance(), new Runnable(){
			public void run(){
				if(p.isDead())
					PacketUtil.skipRespawnScreen(p);
			}
		});
	}

	//エントリー中：cancel
	//インベントリネーム一致：cancel
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if(!Settings.isEnable(e.getWhoClicked().getWorld()))return;
		if(!(e.getWhoClicked() instanceof Player))return;

		if(isEntry((Player) e.getWhoClicked())){
			e.setCancelled(true);
			((Player)e.getWhoClicked()).updateInventory();
		}

		Player p = (Player) e.getWhoClicked();
		if(e.getInventory().getName().equalsIgnoreCase("Character Select Menu")){
			e.setCancelled(true);
			p.updateInventory();

			ItemStack item = e.getCurrentItem();
			if(item == null)return;

			String clicked = item.hasItemMeta() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : null;
			if(clicked == null)return;

			//キャンセルボタン
			if(EnumSelectMenu.CharacterCancel.equalsIgnoreCase(clicked)){
				p.closeInventory();
			//ランダムボタン
			}else if(EnumSelectMenu.CharacterRandom.equalsIgnoreCase(clicked)){
				RaceManager.character(p, EnumCharacter.getRandomCharacter());
			//ネクストプレビューボタン
			}else if(EnumSelectMenu.CharacterNext.equalsIgnoreCase(clicked) || EnumSelectMenu.CharacterPrev.equalsIgnoreCase(clicked)){
				p.closeInventory();
				RaceManager.showKartSelectMenu(p);
			//キャラクター選択
			}else if(EnumCharacter.getClassfromString(clicked) != null){
				RaceManager.character(p, EnumCharacter.getClassfromString(clicked));
			}
			p.playSound(p.getLocation(), Sound.CLICK, 0.5F, 1.0F);
		}else if(e.getInventory().getName().equalsIgnoreCase("Kart Select Menu")){
			e.setCancelled(true);
			p.updateInventory();

			ItemStack item = e.getCurrentItem();
			if(item == null)return;

			String clicked = item.hasItemMeta() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : null;
			if(clicked == null)return;

			//キャンセルボタン
			if(EnumSelectMenu.KartCancel.equalsIgnoreCase(clicked)){
				p.closeInventory();
			//ランダムボタン
			}else if(EnumSelectMenu.KartRandom.equalsIgnoreCase(clicked)){
				RaceManager.setPassengerCustomMinecart(p, EnumKarts.getRandomKart());
			//ネクストプレビューボタン
			}else if(EnumSelectMenu.KartNext.equalsIgnoreCase(clicked) || EnumSelectMenu.KartPrev.equalsIgnoreCase(clicked)){
				p.closeInventory();
				RaceManager.showCharacterSelectMenu(p);
			//カート選択
			}else if(EnumKarts.getKartfromString(clicked) != null){
				RaceManager.setPassengerCustomMinecart(p, EnumKarts.getKartfromString(clicked));
			}
			p.playSound(p.getLocation(), Sound.CLICK, 0.5F, 1.0F);
		}
	}
}
