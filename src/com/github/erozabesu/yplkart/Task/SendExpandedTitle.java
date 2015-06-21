package com.github.erozabesu.yplkart.Task;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.Utils.PacketUtil;

public class SendExpandedTitle extends BukkitRunnable{
	int life = 0;
	int maxlife = 0;
	Player p;
	String text;
	String expandcharacter;
	int characterposition;
	ChatColor color;
	boolean issubtitle;

	public SendExpandedTitle(Player p, int maxlife, String text, String expandcharacter, int characterposition, ChatColor color, boolean issubtitle){
		this.p = p;
		this.maxlife = maxlife*20;
		this.text = text;
		this.expandcharacter = expandcharacter;
		this.characterposition = characterposition;
		this.color = color;
		this.issubtitle = issubtitle;

		PacketUtil.sendTitle(p, text, 0, 10, 0, color, issubtitle);
	}

	@Override
	public void run(){
		life++;

		if(maxlife < life){
			this.cancel();
			return;
		}

		if(life % 2 == 0){
			text = text.substring(0, this.characterposition) + this.expandcharacter + text.substring(this.characterposition, this.text.length());

			PacketUtil.sendTitle(p, text, 0, 10, 5, color, issubtitle);
		}
	}
}
