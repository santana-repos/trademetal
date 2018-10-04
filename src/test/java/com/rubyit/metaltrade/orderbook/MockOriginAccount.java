package com.rubyit.metaltrade.orderbook;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import org.springframework.test.util.ReflectionTestUtils;

import com.rubyit.metaltrade.Asset;
import com.rubyit.metaltrade.Trader;
import com.rubyit.metaltrade.Wallet;
import com.rubyit.metaltrade.obj.AssetType;

class MockOriginAccount extends Trader {
	
	public static final Double INITIAL_BALANCE = 500.00;
	
	public MockOriginAccount(AssetType... assets) {
		super("Mock" + assets[0].getName());
		Set<Asset> localAssets = new HashSet<>();
		for (AssetType asset: assets) {
			
			Asset myAsset = new Asset(asset);
			ReflectionTestUtils.setField(myAsset, "balance", new BigDecimal(INITIAL_BALANCE, new MathContext(8, RoundingMode.HALF_EVEN)));
			localAssets.add(myAsset);
		}
		Wallet myWallet = new Wallet();
		ReflectionTestUtils.setField(myWallet, "assets", localAssets);
		this.myWallet = myWallet;
	}
}
