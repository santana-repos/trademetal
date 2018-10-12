package com.rubyit.metaltrade.orderbook;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;

public class ShouldThrowExceptionWhenCreateOrderWhenBalancePlusBlockbalanceIsNotEnough extends BaseTests {
	/**
	 * That feature will prevent a Trader to create a sell/buy order if an existing asset 
	 * balance is already compromised with a previous created order.
	 */
	@Test
	public void shouldThrowExceptionWhenCreateOrderWhenBalancePlusBlockbalanceIsNotEnough(){
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(
				"ERROR: unable to create order for the asset {asset=GOLD} "
				+ "offering the amount {offeredAmount=2.01} and expecting "
				+ "for {expectedAssetUnitPrice=1.0} having a balance "
				+ "{balance=4.11000000} minus blocked balance "
				+ "{blockedBalance=3.11000000} lower than "
				+ "assetTotalAmountPrice {assetTotalAmountPrice=2.010}.");
		
		Double usdAmount = 49.99d;
		Double usdUnitPrice = 1.00d;
		Double goldAmount = 4.11d;
		Double goldUnitPriceInUSD = 40.12d;
		Double silverAmount = 22.22d;
		Double silverUnitPriceInUSD = 8.88d;
		Double bronzeAmount = 147.33d;
		Double bronzeUnitPriceInUSD = 1.62d;
		OrderBook orderbook = new OrderBook(USD, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		MockOriginAccount mockOriginAccountUSD =  new MockOriginAccount(USD);
		MockOriginAccount mockOriginAccountGOLD =  new MockOriginAccount(GOLD);
		MockOriginAccount mockOriginAccountSILVER =  new MockOriginAccount(SILVER);
		MockOriginAccount mockOriginAccountBRONZE =  new MockOriginAccount(BRONZE);
		
		TraderType thiago = new MockTrader("Thiago", USD);
		TraderType renata = new MockTrader("Renata", USD);
		TraderType maria =  new MockTrader("Maria", USD);
		TraderType alice =  new MockTrader("Alice", USD);
		
		execute(usdAmount, usdUnitPrice, goldAmount, goldUnitPriceInUSD, silverAmount, 
				silverUnitPriceInUSD, bronzeAmount, bronzeUnitPriceInUSD, mockOriginAccountUSD, mockOriginAccountGOLD,
				mockOriginAccountSILVER, mockOriginAccountBRONZE, thiago,
				renata, maria, alice, orderbook);
		
		PairOrders goldXusdPairOrders = orderbook.retrievePairOrders(GOLDxUSD);
		assertEquals(0, goldXusdPairOrders.retrieveBuyOrders().size());
		assertEquals(0, goldXusdPairOrders.retrieveSellOrders().size());
		PairOrders silverXusdPairOrders = orderbook.retrievePairOrders(SILVERxUSD);
		assertEquals(0, silverXusdPairOrders.retrieveBuyOrders().size());
		assertEquals(0, silverXusdPairOrders.retrieveSellOrders().size());
		PairOrders bronzeXusdPairOrders = orderbook.retrievePairOrders(BRONZExSILVER);
		assertEquals(0, bronzeXusdPairOrders.retrieveBuyOrders().size());
		assertEquals(0, bronzeXusdPairOrders.retrieveSellOrders().size());
		
		System.out.println(renata.getWalletAllAssets());
		AssetType offeredAsset = null;
		Double offeredAmount = null;
		AssetType expectedAsset = null;
		Double expectedAssetUnitPrice = null;
		

		//SELL
		offeredAsset = GOLD;
		offeredAmount = 3.11d;
		expectedAsset = USD;
		expectedAssetUnitPrice = 1.00d;
		Order order2 = renata.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		Double assetTotalAmountPriceGoldUsd = order2.getAssetTotalAmountPrice().doubleValue();

		//SELL
		offeredAsset = GOLD;
		offeredAmount = 2.01d;
		expectedAsset = USD;
		expectedAssetUnitPrice = 1.00d;
		renata.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		
	}
}
