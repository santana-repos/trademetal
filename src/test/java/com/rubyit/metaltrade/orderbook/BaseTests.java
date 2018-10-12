package com.rubyit.metaltrade.orderbook;

import static org.junit.Assert.*;
import static com.rubyit.metaltrade.Utils.formatNumber;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.rubyit.metaltrade.Trader;
import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;
import com.rubyit.metaltrade.obj.Pair;

public class BaseTests {
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	protected static final AssetType BRONZE = new AssetType("BRONZE");
	protected static final AssetType SILVER = new AssetType("SILVER");
	protected static final AssetType GOLD = new AssetType("GOLD");
	protected static final AssetType USD = new AssetType("USD");
	
	// {BASE CURRENCY == AMOUNT}/{QUOTE CURRENCY == price} 
	protected static final Pair GOLDxUSD = new Pair(GOLD, USD);//"GOLD/USD" 
	protected static final Pair SILVERxUSD = new Pair(SILVER, USD);//"GOLD/USD" 
	protected static final Pair BRONZExUSD = new Pair(BRONZE, USD);//"GOLD/USD" 
	protected static final Pair GOLDxSILVER = new Pair(GOLD, SILVER);//"GOLD/SILVER" 
	protected static final Pair BRONZExSILVER = new Pair(BRONZE, SILVER);//"BRONZE/SILVER"
	
	protected Double usdBaseTranactionFee = 0.01d;
	
	protected OrderBook execute() {
		
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
		
		return execute(usdAmount, usdUnitPrice, goldAmount, goldUnitPriceInUSD, silverAmount, 
				silverUnitPriceInUSD, bronzeAmount, bronzeUnitPriceInUSD, mockOriginAccountUSD, mockOriginAccountGOLD,
				mockOriginAccountSILVER, mockOriginAccountBRONZE, thiago,
				renata, maria, alice, orderbook);
	}
	
	private OrderBook executeWithFee() {
		
		Double usdAmount = 49.99d;
		Double usdUnitPrice = 1.00d;
		Double goldAmount = 0.24330900d;
		Double goldUnitPriceInUSD = 40.12d;
		Double silverAmount = 22.22d;
		Double silverUnitPriceInUSD = 8.88d;
		Double bronzeAmount = 147.33d;
		Double bronzeUnitPriceInUSD = 1.62d;
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		MockOriginAccount mockOriginAccountUSD =  new MockOriginAccount(USD);
		MockOriginAccount mockOriginAccountGOLD =  new MockOriginAccount(GOLD, USD);
		MockOriginAccount mockOriginAccountSILVER =  new MockOriginAccount(SILVER, USD);
		MockOriginAccount mockOriginAccountBRONZE =  new MockOriginAccount(BRONZE, USD);
		
		TraderType thiago = new MockTrader("Thiago", USD);
		TraderType renata = new MockTrader("Renata", USD);
		TraderType maria =  new MockTrader("Maria", USD);
		TraderType alice =  new MockTrader("Alice", USD);
		
		return execute(usdAmount, usdUnitPrice, goldAmount, goldUnitPriceInUSD, silverAmount, 
				silverUnitPriceInUSD, bronzeAmount, bronzeUnitPriceInUSD, mockOriginAccountUSD, mockOriginAccountGOLD,
				mockOriginAccountSILVER, mockOriginAccountBRONZE, thiago,
				renata, maria, alice, orderbook);
	}
	
	protected OrderBook execute(Double usdAmount,  Double usdUnitPrice,  Double goldAmount,
			 Double goldUnitPriceInUSD,  Double silverAmount,  Double silverUnitPriceInUSD,
			 Double bronzeAmount,  Double bronzeUnitPriceInUSD,  MockOriginAccount mockOriginAccountUSD,
			 MockOriginAccount mockOriginAccountGOLD, MockOriginAccount mockOriginAccountSILVER,
			 MockOriginAccount mockOriginAccountBRONZE, TraderType thiago,
			 TraderType renata, TraderType maria, TraderType alice, OrderBook orderbook) {
		
		usdAmount = (usdAmount == null) ? 49.99 : usdAmount;
		usdUnitPrice = (usdUnitPrice == null) ? 1.00 : usdUnitPrice;
		goldAmount = (goldAmount == null) ? 4.11 : goldAmount;
		goldUnitPriceInUSD = (goldUnitPriceInUSD == null) ? 40.12 : goldUnitPriceInUSD;
		silverAmount = (silverAmount == null) ? 22.22 : silverAmount;
		silverUnitPriceInUSD = (silverUnitPriceInUSD == null) ? 8.88 : silverUnitPriceInUSD;
		bronzeAmount = (bronzeAmount == null) ? 147.33 : bronzeAmount;
		bronzeUnitPriceInUSD = (bronzeUnitPriceInUSD == null) ? 1.62 : bronzeUnitPriceInUSD;
		
		mockOriginAccountUSD = (mockOriginAccountUSD == null) ? new MockOriginAccount(USD) : mockOriginAccountUSD;
		mockOriginAccountGOLD = (mockOriginAccountGOLD == null) ? new MockOriginAccount(GOLD) : mockOriginAccountGOLD;
		mockOriginAccountSILVER = (mockOriginAccountSILVER == null) ? new MockOriginAccount(SILVER) : mockOriginAccountSILVER;
		mockOriginAccountBRONZE = (mockOriginAccountBRONZE == null) ? new MockOriginAccount(BRONZE) : mockOriginAccountBRONZE;
		
		thiago = (thiago == null) ? new MockTrader("Thiago", USD) : thiago;
		renata = (renata == null) ? new MockTrader("Renata", USD) : renata;
		maria = (maria == null) ? new MockTrader("Maria", USD) : maria;
		alice = (alice == null) ? new MockTrader("Alice", USD): alice;

		assertNotNull(mockOriginAccountUSD.getWalletAsset(USD));
		assertNotNull(mockOriginAccountUSD.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE), mockOriginAccountUSD.getWalletAsset(USD).getBalance());
		assertNotNull(mockOriginAccountGOLD.getWalletAsset(GOLD));
		assertNotNull(mockOriginAccountGOLD.getWalletAsset(GOLD).getBalance());
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE), mockOriginAccountGOLD.getWalletAsset(GOLD).getBalance());
		assertNotNull(mockOriginAccountSILVER.getWalletAsset(SILVER));
		assertNotNull(mockOriginAccountSILVER.getWalletAsset(SILVER).getBalance());
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE), mockOriginAccountSILVER.getWalletAsset(SILVER).getBalance());
		assertNotNull(mockOriginAccountBRONZE.getWalletAsset(BRONZE));
		assertNotNull(mockOriginAccountBRONZE.getWalletAsset(BRONZE).getBalance());
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE), mockOriginAccountBRONZE.getWalletAsset(BRONZE).getBalance());
		
		assertNotNull(thiago.getWalletAsset(USD));
		assertNotNull(thiago.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE), thiago.getWalletAsset(USD).getBalance());
		assertNotNull(renata.getWalletAsset(USD));
		assertNotNull(renata.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE), renata.getWalletAsset(USD).getBalance());
		assertNotNull(maria.getWalletAsset(USD));
		assertNotNull(maria.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE), maria.getWalletAsset(USD).getBalance());
		assertNotNull(alice.getWalletAsset(USD));
		assertNotNull(alice.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE), alice.getWalletAsset(USD).getBalance());
		
		
		AssetType offeredAsset = null;
		Double offeredAmount = null;
		AssetType expectedAsset = null;
		Double expectedAssetUnitPrice = null;
		
		offeredAsset = GOLD;
		offeredAmount = formatNumber(goldAmount).doubleValue(); // 9.76155717
		expectedAsset = USD;
		expectedAssetUnitPrice = formatNumber(goldUnitPriceInUSD).doubleValue();  // 40.12
		//SELL
		Order order1 = mockOriginAccountGOLD.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(GOLDxUSD, order1.getPair());
		assertNotNull(order1.getAssetTotalAmountPrice());
		Double assetTotalAmountPriceGoldUsd = order1.getAssetTotalAmountPrice().doubleValue();  // 164.8932
		assertNotEquals(formatNumber(0d), order1.getAssetTotalAmountPrice());
		assertEquals(formatNumber(formatNumber(offeredAmount).multiply(formatNumber(expectedAssetUnitPrice))), formatNumber(assetTotalAmountPriceGoldUsd) );
		
		offeredAsset = USD;
		offeredAmount = formatNumber(goldAmount).doubleValue();
		expectedAsset = GOLD;
		expectedAssetUnitPrice = formatNumber(goldUnitPriceInUSD).doubleValue(); // 40.12
		//BUY
		Order order4 = renata.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(GOLDxUSD, order4.getPair());
		assertNotNull(order4.getAssetTotalAmountPrice());
		Double assetTotalAmountPriceUsdGold = order4.getAssetTotalAmountPrice().doubleValue(); // 0.24330900
		assertNotEquals(formatNumber(0d), order4.getAssetTotalAmountPrice());
		assertEquals(formatNumber(offeredAmount), formatNumber(assetTotalAmountPriceUsdGold));
		
		
		offeredAsset = SILVER;
		offeredAmount = formatNumber(silverAmount).doubleValue();
		expectedAsset = USD;
		expectedAssetUnitPrice = silverUnitPriceInUSD;
		Order order2 = mockOriginAccountSILVER.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(SILVERxUSD, order2.getPair());
		assertNotNull(order2.getAssetTotalAmountPrice());
		Double assetTotalAmountPriceSilverUsd = order2.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(formatNumber(0d), order2.getAssetTotalAmountPrice());
		assertEquals(formatNumber(formatNumber(offeredAmount).multiply(formatNumber(expectedAssetUnitPrice))), formatNumber(assetTotalAmountPriceSilverUsd) );
		
		offeredAsset = USD;
		offeredAmount = formatNumber(silverAmount).doubleValue();
		expectedAsset = SILVER;
		expectedAssetUnitPrice = formatNumber(silverUnitPriceInUSD).doubleValue();
		Order order5 = maria.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(SILVERxUSD, order5.getPair());
		assertNotNull(order5.getAssetTotalAmountPrice());
		Double assetTotalAmountPriceUsdSilver = order5.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(formatNumber(0d), order5.getAssetTotalAmountPrice());
		assertEquals(formatNumber(offeredAmount), formatNumber(assetTotalAmountPriceUsdSilver) );
		
		offeredAsset = BRONZE;
		offeredAmount = formatNumber(bronzeAmount).doubleValue();
		expectedAsset = USD;
		expectedAssetUnitPrice = formatNumber(bronzeUnitPriceInUSD).doubleValue();
		Order order3 = mockOriginAccountBRONZE.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(BRONZExUSD, order3.getPair());
		assertNotNull(order3.getAssetTotalAmountPrice());
		Double assetTotalAmountPriceBronzeUsd = order3.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(formatNumber(0d), order2.getAssetTotalAmountPrice());
		assertEquals(formatNumber(formatNumber(offeredAmount).multiply(formatNumber(expectedAssetUnitPrice))), formatNumber(assetTotalAmountPriceBronzeUsd) );
		
		offeredAsset = USD;
		offeredAmount = formatNumber(bronzeAmount).doubleValue();
		expectedAsset = BRONZE;
		expectedAssetUnitPrice = formatNumber(bronzeUnitPriceInUSD).doubleValue();
		Order order6 = alice.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		assertEquals(BRONZExUSD, order6.getPair());
		assertNotNull(order6.getAssetTotalAmountPrice());
		Double assetTotalAmountPriceUsdBronze = order6.getAssetTotalAmountPrice().doubleValue();
		assertNotEquals(0, order6.getAssetTotalAmountPrice().compareTo(formatNumber(0d)));
		assertEquals(formatNumber(offeredAmount), formatNumber(assetTotalAmountPriceUsdBronze));
		
		
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE), thiago.getWalletAsset(USD).getBalance());
		assertNotNull(renata.getWalletAsset(GOLD));
		assertNotNull(renata.getWalletAsset(GOLD).getBalance());
		assertEquals(formatNumber(goldAmount), renata.getWalletAsset(GOLD).getBalance()); //4.11
		assertNotNull(maria.getWalletAsset(SILVER));
		assertNotNull(maria.getWalletAsset(SILVER).getBalance());
		assertEquals(formatNumber(silverAmount), maria.getWalletAsset(SILVER).getBalance());
		assertNotNull(alice.getWalletAsset(BRONZE));
		assertNotNull(alice.getWalletAsset(BRONZE).getBalance());
		assertEquals(formatNumber(bronzeAmount), alice.getWalletAsset(BRONZE).getBalance());
		
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE).subtract(formatNumber(assetTotalAmountPriceUsdGold)), mockOriginAccountGOLD.getWalletAsset(GOLD).getBalance());
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE).subtract(formatNumber(assetTotalAmountPriceUsdSilver)), mockOriginAccountSILVER.getWalletAsset(SILVER).getBalance());
		assertEquals(formatNumber(MockOriginAccount.INITIAL_BALANCE).subtract(formatNumber(assetTotalAmountPriceUsdBronze)), mockOriginAccountBRONZE.getWalletAsset(BRONZE).getBalance());
		
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE), thiago.getWalletAsset(USD).getBalance());
		assertNotNull(renata.getWalletAsset(USD));
		assertNotNull(renata.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE).subtract(formatNumber(assetTotalAmountPriceGoldUsd)), renata.getWalletAsset(USD).getBalance());
		assertNotNull(maria.getWalletAsset(USD));
		assertNotNull(maria.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE).subtract(formatNumber(assetTotalAmountPriceSilverUsd)), maria.getWalletAsset(USD).getBalance());
		assertNotNull(alice.getWalletAsset(USD));
		assertNotNull(alice.getWalletAsset(USD).getBalance());
		assertEquals(formatNumber(MockTrader.INITIAL_BALANCE).subtract(formatNumber(assetTotalAmountPriceBronzeUsd)), alice.getWalletAsset(USD).getBalance());
		
		
		return orderbook;
	}

	protected PairOrders executeNotMatch(MockOriginAccount mockOriginAccountGOLD, TraderType thiago, OrderBook orderbook,
			Order order1, Order order2) {
		assertNotNull(mockOriginAccountGOLD.getWalletAsset(GOLD));
		assertNotNull(mockOriginAccountGOLD.getWalletAsset(GOLD).getBalance());
		assertEquals(0, mockOriginAccountGOLD.getWalletAsset(GOLD).getBalance().compareTo(BigDecimal.valueOf(MockOriginAccount.INITIAL_BALANCE)));
		
		assertNotNull(thiago.getWalletAsset(USD));
		assertNotNull(thiago.getWalletAsset(USD).getBalance());
		assertEquals(0, thiago.getWalletAsset(USD).getBalance().compareTo(BigDecimal.valueOf(MockTrader.INITIAL_BALANCE)));
		
		
		PairOrders goldXusdPairOrders = orderbook.retrievePairOrders(GOLDxUSD);
		assertTrue(goldXusdPairOrders.retrieveBuyOrders().contains(order1));
		assertTrue(goldXusdPairOrders.retrieveSellOrders().contains(order2));
		assertEquals(order2, goldXusdPairOrders.getBidOrder());
		assertEquals(order1, goldXusdPairOrders.getAskOrder());
		
		return goldXusdPairOrders;
	}

	protected PairOrders executeNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(OrderBook orderbook, MockOriginAccount mockOriginAccountGOLD, TraderType thiago) {
		
		AssetType offeredAsset = null;
		Double offeredAmount = null;
		AssetType expectedAsset = null;
		Double expectedAssetUnitPrice = null;
		
		//BUY
		offeredAsset = USD;
		offeredAmount = 3.33d;
		expectedAsset = GOLD;
		expectedAssetUnitPrice = 13.33d;
		Order order1 = thiago.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		Double assetTotalAmountPriceUsdGold = order1.getAssetTotalAmountPrice().doubleValue();
		
		//SELL
		offeredAsset = GOLD;
		offeredAmount = 0.99d;
		expectedAsset = USD;
		expectedAssetUnitPrice = expectedAssetUnitPrice - 0.55d;
		Order order2 = mockOriginAccountGOLD.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
		Double assetTotalAmountPriceGoldUsd = order2.getAssetTotalAmountPrice().doubleValue();
		
		PairOrders goldXusdPairOrders = executeNotMatch(mockOriginAccountGOLD, thiago, orderbook, order1, order2);
		
		assertEquals(-1, goldXusdPairOrders.getBidOrder().getExpectedAssetUnitPrice().compareTo(goldXusdPairOrders.getAskOrder().getExpectedAssetUnitPrice()));
		
		return goldXusdPairOrders;
	}
}