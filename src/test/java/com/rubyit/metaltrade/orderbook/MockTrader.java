package com.rubyit.metaltrade.orderbook;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.test.util.ReflectionTestUtils;

import com.rubyit.metaltrade.Asset;
import com.rubyit.metaltrade.Trader;
import com.rubyit.metaltrade.obj.AssetType;

/**
 * As a role, a Mock account is the only one that
 *  can give balance to itseft. 
 * @author mineruser
 *
 */
final class MockTrader extends Trader {
	
	public static final Double INITIAL_BALANCE = 260.44;
	public MockTrader(String name, AssetType usd) {
		super(name);
		Asset myAsset = new Asset(usd);
		ReflectionTestUtils.setField(myAsset, "balance", new BigDecimal(INITIAL_BALANCE, new MathContext(8, RoundingMode.HALF_EVEN)));
		Set<Asset> assets = new HashSet<>(Arrays.asList(myAsset));
		ReflectionTestUtils.setField(myWallet, "assets", assets);
	}
}