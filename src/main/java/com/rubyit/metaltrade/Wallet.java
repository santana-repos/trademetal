package com.rubyit.metaltrade;

import static com.rubyit.metaltrade.Utils.getGson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.rubyit.metaltrade.obj.AssetType;

public class Wallet {
	
	private String ID;
	private Set<Asset> assets;
	
	public Wallet() {
		ID = UUID.randomUUID().toString();
		assets = new HashSet<>();
	}
	public Asset getAsset(final AssetType asset) {
		
		for (Asset myAsset : assets) {
			if (myAsset.getAsset().equals(asset)) {
				return myAsset;
			}
		}
		
		Asset a = new Asset(asset);
		assets.add(a);
		return a;
	}
	public List<Asset> getAssets() {
		return new ArrayList<>(assets);
	}
	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("ID", ID);
		json.put("assets", assets);
		return getGson().toJson(json);
	}
}

