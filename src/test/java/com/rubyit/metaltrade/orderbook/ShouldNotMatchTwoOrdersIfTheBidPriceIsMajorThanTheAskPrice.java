package com.rubyit.metaltrade.orderbook;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;

public class ShouldNotMatchTwoOrdersIfTheBidPriceIsMajorThanTheAskPrice extends BaseTests {
	
	@Test
	public void shouldNotMatchTwoOrdersIfTheBidPriceIsMajorThanTheAskPrice(){
		
		MockOriginAccount mockOriginAccountGOLD =  new MockOriginAccount(GOLD);
		TraderType thiago = new MockTrader("Thiago", USD);
		
		OrderBook orderbook = new OrderBook(USD, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		
		AssetType offeredAsset = null;
		Double offeredAmount = null;
		AssetType expectedAsset = null;
		Double expectedAssetUnitPrice = 13.33d;
		
		//BUY
		offeredAsset = USD;
		offeredAmount = 3.33d;
		expectedAsset = GOLD;
		expectedAssetUnitPrice = expectedAssetUnitPrice;
		Order order1 = thiago.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		Double assetTotalAmountPriceUsdGold = order1.getAssetTotalAmountPrice().doubleValue();
		
		//SELL
		offeredAsset = GOLD;
		offeredAmount = 0.99d;
		expectedAsset = USD;
		expectedAssetUnitPrice = expectedAssetUnitPrice + 0.55d;
		Order order2 = mockOriginAccountGOLD.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		Double assetTotalAmountPriceGoldUsd = order2.getAssetTotalAmountPrice().doubleValue();
		
		PairOrders goldXusdPairOrders = executeNotMatch(mockOriginAccountGOLD, thiago, orderbook, order1, order2);
		
		assertEquals(1, goldXusdPairOrders.getBidOrder().getExpectedAssetUnitPrice().compareTo(goldXusdPairOrders.getAskOrder().getExpectedAssetUnitPrice()));
	}
}
