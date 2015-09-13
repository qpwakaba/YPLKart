package com.github.erozabesu.yplkart.object;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * モーション操作を行わない静的な妨害エンティティの設置されているチャンクを読み込み続けるBukkitRunnableを継承したクラス。
 * @author erozabesu
 */
public class ItemStaticJammerEntity extends BukkitRunnable {
    Entity jammerEntity;

    /**
     * コンストラクタ。
     * @param jammerEntity 設置した妨害エンティティ
     */
    public ItemStaticJammerEntity(Entity jammerEntity) {
        this.jammerEntity = jammerEntity;
    }

    @Override
    public void run() {
        if (this.jammerEntity.isDead()) {
            this.cancel();
            return;
        }

        this.jammerEntity.getLocation().getChunk().load();
    }
}
