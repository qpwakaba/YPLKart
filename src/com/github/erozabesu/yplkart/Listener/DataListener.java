package com.github.erozabesu.yplkart.Listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.DisplayKartData;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Enum.EnumSelectMenu;
import com.github.erozabesu.yplkart.Enum.Permission;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Task.SendBlinkingTitleTask;
import com.github.erozabesu.yplkart.Utils.PacketUtil;
import com.github.erozabesu.yplkart.Utils.Util;

public class DataListener extends RaceManager implements Listener {

	private YPLKart pl;
	public DataListener(YPLKart plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		pl = plugin;
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		if(!Settings.isEnable(e.getWorld()))return;
		DisplayKartData.respawnKart(e.getChunk());
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent e){
		if(!Settings.isEnable(e.getWorld()))return;
		DisplayKartData.respawnKart(e.getWorld());
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

	//搭乗に成功した場合データ登録
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e){
		if(!Settings.isEnable(e.getEntered().getWorld()))return;
		if(!(e.getEntered() instanceof Player))return;

		//PacketUtil.runPlayerLookingUpdate((Player) e.getEntered());
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

		ArrayList<String> checkpoint = getNearbyCheckpointID(e.getPlayer().getLocation(), checkPointDetectRadius, r.getEntry());
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
				//PacketUtil.runPlayerLookingUpdate(p);

				if(isEntry(p)){
					Scoreboards.showBoard(p);
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

		Player p = e.getPlayer();

		RaceManager.removeCustomMinecart(p);
		p.setWalkSpeed(0.2F);
		Scoreboards.clearBoard(p);

		//ディスプレイカートに搭乗中ログアウトするとディスプレイカートまで削除されてしまうため、
		//ログアウト前に降ろしておく。何故カートが削除されてしまうのかは原因不明
		if(p.getVehicle() != null)
			if(RaceManager.isCustomDisplayMinecart(p.getVehicle()))
				p.leaveVehicle();
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		final Player p = e.getPlayer();
		final Race r = getRace(p);

		Bukkit.getScheduler().runTaskLater(pl, new Runnable(){
			public void run(){
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
				new SendBlinkingTitleTask(p, r.getCharacter().getDeathPenaltySecond(), "DEATH PENALTY", ChatColor.RED).runTaskTimer(YPLKart.getInstance(), 0, 1);
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

	//キラー使用中の窒素ダメージを無効
	//カート搭乗中の落下ダメージを無効
	//エントリー→←スタート の間のダメージを無効
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player) e.getEntity();
		if(!isEntry(p))return;

		if(getRace(p).getUsingKiller() != null){
			if(e.getCause() == DamageCause.SUFFOCATION){
				e.setCancelled(true);
				return;
			}
		}
		if(e.getCause() == DamageCause.FALL)
			if(p.getVehicle() != null)
				if(RaceManager.isCustomMinecart(p.getVehicle()))
					e.setCancelled(true);

		if(!getRace(p).getStart())
			if(e.getCause() != DamageCause.VOID)
				e.setCancelled(true);
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

	//エントリー中の場合、キャラクター・カートが未選択の場合はメニューを閉じさせません
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(!isEntry((Player) e.getPlayer()))return;

		final Player p = (Player) e.getPlayer();
		Race r = getRace(p);
		if(e.getInventory().getName().equalsIgnoreCase("Character Select Menu")){
			if(r.getCharacter() == null){
				Util.sendMessage(p, "#Redキャラクターを選択して下さい");
				Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable(){
					public void run(){
						showCharacterSelectMenu(p);
					}
				});
				return;
			}
			if(r.getKart() == null && Permission.hasPermission(p, Permission.kart_ride, true)){
				Util.sendMessage(p, "#Redカートを選択して下さい");
				Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable(){
					public void run(){
						showKartSelectMenu(p);
					}
				});
				return;
			}
		}else if(e.getInventory().getName().equalsIgnoreCase("Kart Select Menu")){
			if(r.getKart() == null && Permission.hasPermission(p, Permission.kart_ride, true)){
				Util.sendMessage(p, "#Redカートを選択して下さい");
				Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable(){
					public void run(){
						showKartSelectMenu(p);
					}
				});
				return;
			}
			if(r.getCharacter() == null){
				Util.sendMessage(p, "#Redキャラクターを選択して下さい");
				Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable(){
					public void run(){
						showCharacterSelectMenu(p);
					}
				});
				return;
			}
		}
	}

	//エントリー中：cancel
	//インベントリネーム一致：cancel
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if(!Settings.isEnable(e.getWhoClicked().getWorld()))return;
		if(!(e.getWhoClicked() instanceof Player))return;

		//エントリー中はインベントリの操作をさせない
		if(isEntry((Player) e.getWhoClicked())){
			e.setCancelled(true);
			((Player)e.getWhoClicked()).updateInventory();
		}

		Player p = (Player) e.getWhoClicked();
		Race r = getRace(p);
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
				if(isEntry(p)){
					if(r.getCharacter() == null){
						Util.sendMessage(p, "#Redキャラクターを選択して下さい");
					}else{
						p.closeInventory();

						//kart == nullの場合はonInventoryCloseで強制的にメニューが表示される
						if(r.getKart() != null)
							RaceManager.showKartSelectMenu(p);
					}
				}else{
					p.closeInventory();
					RaceManager.showKartSelectMenu(p);
				}
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
				if(isEntry(p)){
					if(r.getKart() == null){
						Util.sendMessage(p, "#Redカートを選択して下さい");
					}else{
						p.closeInventory();

						//character == nullの場合はonInventoryCloseで強制的にメニューが表示される
						if(r.getCharacter() != null)
							RaceManager.showCharacterSelectMenu(p);
					}
				}else{
					p.closeInventory();
					RaceManager.showCharacterSelectMenu(p);
				}
			//カート選択
			}else if(EnumKarts.getKartfromString(clicked) != null){
				RaceManager.setPassengerCustomMinecart(p, EnumKarts.getKartfromString(clicked));
			}
			p.playSound(p.getLocation(), Sound.CLICK, 0.5F, 1.0F);
		}
	}
}
