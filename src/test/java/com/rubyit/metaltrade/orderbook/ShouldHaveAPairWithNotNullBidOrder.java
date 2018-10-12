package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;

public class ShouldHaveAPairWithNotNullBidOrder extends BaseTests {
	@Test
	public void shouldHaveAPairWithNotNullBidOrder(){
		
		TraderType renata = new MockTrader("Renata", USD);
		MockOriginAccount mockOriginAccountGOLD = new MockOriginAccount(GOLD);
		OrderBook orderbook = new OrderBook(USD, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		
		execute(null, null, null, null, null, null, null, null, null, mockOriginAccountGOLD, null,
				null, null, renata, null, null, orderbook);
		
		AssetType offeredAsset = null;
		Double offeredAmount = null;
		AssetType expectedAsset = null;
		Double expectedAssetUnitPrice = null;
		Double assetTotalAmountPriceGoldUsd = null;
		
		offeredAsset = GOLD;
		offeredAmount = 4.11d;
		expectedAsset = USD;
		expectedAssetUnitPrice = 0.02492522d; // 1 / goldUnitPriceInUSD;
		Order order1 = renata.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(GOLDxUSD, order1.getPair());
		assertNotNull(order1.getAssetTotalAmountPrice());
		assetTotalAmountPriceGoldUsd = order1.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(0, order1.getAssetTotalAmountPrice().compareTo(BigDecimal.ZERO));
		assertEquals(formatNumber(assetTotalAmountPriceGoldUsd), formatNumber(formatNumber(offeredAmount).multiply(formatNumber(expectedAssetUnitPrice))) );
		
		offeredAsset = GOLD;
		offeredAmount = 8.22d;
		expectedAsset = USD;
		expectedAssetUnitPrice = 40.12d;
		Order order2 = mockOriginAccountGOLD.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(GOLDxUSD, order2.getPair());
		assertNotNull(order2.getAssetTotalAmountPrice());
		assetTotalAmountPriceGoldUsd = order2.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(0, order2.getAssetTotalAmountPrice().compareTo(BigDecimal.ZERO));
		assertEquals(formatNumber(assetTotalAmountPriceGoldUsd), formatNumber(formatNumber(offeredAmount).multiply(formatNumber(expectedAssetUnitPrice))) );
		
		PairOrders goldXusdPairOrders = orderbook.retrievePairOrders(GOLDxUSD);
		assertNotNull(goldXusdPairOrders);
		assertEquals(order1, goldXusdPairOrders.getBidOrder());
	}
}
