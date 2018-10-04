package com.rubyit.metaltrade;

import static com.rubyit.metaltrade.Utils.getGson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rubyit.metaltrade.obj.AssetType;

class Account {

	protected Wallet myWallet;
	private String ID;

	Account() {
		myWallet = new Wallet();
		ID = UUID.randomUUID().toString();
	}

	public Asset getWalletAsset(AssetType asset) {
		return myWallet.getAsset(asset);
	}

	public List<Asset> getWalletAllAssets() {
		return myWallet.getAssets();
	}

	public String getID() {
		return ID;
	}

	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("ID", ID);
		json.put("myWallet", myWallet);
		return getGson().toJson(json);
	}
}
