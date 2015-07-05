package com.github.erozabesu.yplkart.Task;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Utils.PacketUtil;

public class SendCountDownTitleTask extends BukkitRunnable{
	int life = 0;
	int maxlife = 0;
	Player p;
	String text;
	ChatColor color;
	ChatColor subcolor;

	public SendCountDownTitleTask(Player p, int maxlife, String text, ChatColor color, ChatColor subcolor){
		this.p = p;
		this.maxlife = maxlife*20;
		this.text = text;
		this.color = color;
		this.subcolor = subcolor;

		PacketUtil.sendTitle(p, text, 0, 25, 0, color, false);
		PacketUtil.sendTitle(p, "残り時間 : " + maxlife + "秒", 0, 25, 0, subcolor, true);
	}

	@Override
	public void run(){
		life++;

		if(RaceManager.getRace(this.p).getGoal()){
			this.cancel();
			return;
		}

		if(maxlife < life){
			this.cancel();
			return;
		}

		if(life % 20 == 0){
			if(((int)(maxlife-life)/20) != 0){
				PacketUtil.sendTitle(p, text, 0, 25, 0, color, false);
				PacketUtil.sendTitle(p, "残り時間 残り : " + ((int)(maxlife-life)/20) + "秒", 0, 25, 0, subcolor, true);
			}
		}
	}
}
