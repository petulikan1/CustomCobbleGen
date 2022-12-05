/**
 * CustomCobbleGen By @author Philip Flyvholm
 * Database.java
 */
package me.phil14052.CustomCobbleGen.databases;

import me.phil14052.CustomCobbleGen.CustomCobbleGen;
import me.phil14052.CustomCobbleGen.Managers.BlockManager;
import me.phil14052.CustomCobbleGen.Managers.TierManager;
import me.phil14052.CustomCobbleGen.Utils.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Philip
 *
 */
public abstract class PlayerDatabase {

	protected CustomCobbleGen plugin;
	protected Map<UUID, PlayerData> playerData;
	protected BlockManager blockManager;
	protected TierManager tierManager;
	
	public PlayerDatabase() {
		playerData = new HashMap<>();
		plugin = CustomCobbleGen.getInstance();
		blockManager = BlockManager.getInstance();
		tierManager = TierManager.getInstance();
	}
	
	public abstract Response<String> establishConnection();
	public abstract void reloadConnection();
	public abstract void closeConnection();
	public abstract boolean isConnectionEstablished();
	
	public Map<UUID, PlayerData> getAllPlayerData() {
		return this.playerData;
	}

	protected abstract void addToDatabase(PlayerData data);
	
	public PlayerData getPlayerData(UUID uuid) {
		return this.getPlayerData(uuid, true, true);
	}

	protected PlayerData getPlayerData(UUID uuid, boolean loadFromDatabase, boolean saveToDatabaseIfNull) {
		PlayerData playerData = this.getAllPlayerData().getOrDefault(uuid, null);
		if(playerData == null) {
			if(loadFromDatabase) {
				this.loadFromDatabase(uuid);
				return this.getPlayerData(uuid, false, saveToDatabaseIfNull);
			}
			playerData = new PlayerData(uuid);
			if (saveToDatabaseIfNull) this.addToDatabase(playerData);
		}
		return playerData;
	}

	protected boolean containsPlayerData(UUID uuid, boolean loadFromDatabase){
		return this.getPlayerData(uuid, loadFromDatabase, false) != null;
	}

	public boolean containsPlayerData(UUID uuid) {
		return this.getPlayerData(uuid) != null;
	}
	
	public void setPlayerData(PlayerData data) {
		if(data == null) return;
		if(!this.containsPlayerData(data.getUUID())) {
			this.addToDatabase(data);
		}
		this.playerData.put(data.getUUID(), data);
	}
	public void setAllPlayerData(Map<UUID, PlayerData> playerData){
		this.playerData = playerData;
	}
	
	public abstract void loadEverythingFromDatabase();
	public abstract void loadFromDatabase(UUID uuid);
	public abstract void saveToDatabase(UUID uuid);
	public abstract void saveToDatabase(PlayerData data);
	public void saveEverythingToDatabase(){
		if (!this.isConnectionEstablished()) return;
		for (PlayerData data : this.getAllPlayerData().values()) {
			this.saveToDatabase(data);
		}
	}
	
	public abstract void savePistonsToDatabase(UUID uuid);
	public abstract void loadPistonsFromDatabase(UUID uuid);
	
	public abstract String getType();
	
}
