package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;

public class ShouldHaveAPairWithNotNullAskOrder extends BaseTests {
	@Test
	public void shouldHaveAPairWithNotNullAskOrder(){
		
		OrderBook orderbook = execute();
		
		AssetType offeredAsset = null;
		Double offeredAmount = null;
		AssetType expectedAsset = null;
		Double expectedAssetUnitPrice = null;
		
		Double assetTotalAmountPriceUsdGold = null;
		MockOriginAccount mockOriginAccountUSD = new MockOriginAccount(USD);
		TraderType thiago = new MockTrader("Thiago", USD);
		
		offeredAsset = USD;
		offeredAmount = 1.33d;
		expectedAsset = GOLD;
		expectedAssetUnitPrice = 48.44d;
		Order order1 = thiago.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(GOLDxUSD, order1.getPair());
		assertNotNull(order1.getAssetTotalAmountPrice());
		assetTotalAmountPriceUsdGold = order1.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(0, order1.getAssetTotalAmountPrice().compareTo(BigDecimal.ZERO));
		assertEquals(formatNumber(assetTotalAmountPriceUsdGold), formatNumber(offeredAmount) );
		
		offeredAsset = USD;
		offeredAmount = 12.55d;
		expectedAsset = GOLD;
		expectedAssetUnitPrice = 7.99d;
		Order order2 = mockOriginAccountUSD.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(GOLDxUSD, order2.getPair());
		assertNotNull(order2.getAssetTotalAmountPrice());
		assetTotalAmountPriceUsdGold = order2.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(0, order2.getAssetTotalAmountPrice().compareTo(BigDecimal.ZERO));
		assertEquals(formatNumber(assetTotalAmountPriceUsdGold), formatNumber(offeredAmount) );

		PairOrders usdXgoldPairOrders = orderbook.retrievePairOrders(GOLDxUSD);
		assertNotNull(usdXgoldPairOrders);
		assertEquals(order1, usdXgoldPairOrders.getAskOrder());
	}
}
