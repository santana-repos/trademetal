package com.rubyit.metaltrade.obj;

import static com.rubyit.metaltrade.Utils.getGson;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pair {

	private final AssetType amountAsset;
	private final AssetType priceAsset;
	private final String currencyPair; 
	
	public Pair(AssetType amountAsset, AssetType priceAsset) {
		if (amountAsset == null || priceAsset == null) {
			throw new RuntimeException("ERROR: unable to create a currency pair with invalid fields: {amountAsset=" + amountAsset + ", priceAsset=" + priceAsset + "}") ;
		}
		this.amountAsset = amountAsset;
		this.priceAsset = priceAsset;
		this.currencyPair = amountAsset.getName() + "/" + priceAsset.getName(); 
	}

	public AssetType getAmountAsset() {
		return amountAsset;
	}

	public AssetType getPriceAsset() {
		return priceAsset;
	}

	public String getPairName() {
		return currencyPair;
	}

	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("currencyPair", currencyPair);
		json.put("amountAsset", amountAsset);
		json.put("priceAsset", priceAsset);
		return getGson().toJson(json);
	}
}