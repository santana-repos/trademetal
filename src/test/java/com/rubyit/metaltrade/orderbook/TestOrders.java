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

public class TestOrders {
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	private static final AssetType BRONZE = new AssetType("BRONZE");
	private static final AssetType SILVER = new AssetType("SILVER");
	private static final AssetType GOLD = new AssetType("GOLD");
	private static final AssetType USD = new AssetType("USD");
	
	// {BASE CURRENCY == AMOUNT}/{QUOTE CURRENCY == price} 
	private static final Pair GOLDxUSD = new Pair(GOLD, USD);//"GOLD/USD" 
	private static final Pair SILVERxUSD = new Pair(SILVER, USD);//"GOLD/USD" 
	private static final Pair BRONZExUSD = new Pair(BRONZE, USD);//"GOLD/USD" 
	private static final Pair GOLDxSILVER = new Pair(GOLD, SILVER);//"GOLD/SILVER" 
	private static final Pair BRONZExSILVER = new Pair(BRONZE, SILVER);//"BRONZE/SILVER"
	
	Double usdBaseTranactionFee = 0.01d;
	
	@Test
	public void shouldExecutPerfectMatch() {
		
		MockOriginAccount MockGold =  new MockOriginAccount(GOLD, USD);
		TraderType Renata = new MockTrader("Renata", USD);
		
		BigDecimal mockgoldInitialUSDBalance = formatNumber(500d); 
		BigDecimal mockgoldInitialGOLDBalance = formatNumber(500d); 
		BigDecimal renataInitialUSDBalance = formatNumber(260.44d); 
		BigDecimal renataInitialGOLDBalance = formatNumber(0d);
		
		assertEquals(mockgoldInitialUSDBalance, MockGold.getWalletAsset(USD).getBalance());
		assertEquals(mockgoldInitialGOLDBalance, MockGold.getWalletAsset(GOLD).getBalance());
		assertEquals(renataInitialUSDBalance, Renata.getWalletAsset(USD).getBalance());
		assertEquals(renataInitialGOLDBalance, Renata.getWalletAsset(GOLD).getBalance());
		
		BigDecimal commomGoldUsdPrice = formatNumber(40.12d);
		BigDecimal commomGOLDxUSD = formatNumber(9.76155708d);
		BigDecimal commomAmount = formatNumber(0.243309d);
		Double renataOffereddUsdAmount = commomAmount.doubleValue();
		Double renataOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		Double mockgoOfferedldGoldAmount = commomAmount.doubleValue();
		Double mockgoldOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		BigDecimal expectedRenataGoldAmount = formatNumber(renataInitialGOLDBalance.add(commomAmount));
		BigDecimal expectedRenataUsdAmount = formatNumber(renataInitialUSDBalance.subtract(commomGOLDxUSD).subtract(formatNumber(usdBaseTranactionFee)));
		BigDecimal expectedMockgoldGoldAmount = formatNumber(mockgoldInitialGOLDBalance.subtract(formatNumber(mockgoOfferedldGoldAmount)));
		BigDecimal expectedMockgoldUsdAmount = formatNumber(mockgoldInitialUSDBalance.add(commomGOLDxUSD).subtract(formatNumber(usdBaseTranactionFee)));
		
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD);
		
		Order renataBuyOrder = Renata.createOrder(orderbook, USD, renataOffereddUsdAmount, GOLD, renataOfferedGoldUsdPrice); 
		Order mockgoldSellOrder = MockGold.createOrder(orderbook, GOLD, mockgoOfferedldGoldAmount, USD, mockgoldOfferedGoldUsdPrice);
		
		assertEquals(expectedRenataGoldAmount, Renata.getWalletAsset(GOLD).getBalance());//0.24330900
		assertEquals(expectedRenataUsdAmount, Renata.getWalletAsset(USD).getBalance());//250.66844292
		assertEquals(formatNumber(0d), Renata.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0d), Renata.getWalletAsset(USD).getBlockedBalance());
		assertEquals(expectedMockgoldGoldAmount, MockGold.getWalletAsset(GOLD).getBalance());//499.75669100
		assertEquals(expectedMockgoldUsdAmount, MockGold.getWalletAsset(USD).getBalance());//509.75155708
		assertEquals(formatNumber(0d), MockGold.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0d), MockGold.getWalletAsset(USD).getBlockedBalance());
	}
	
	@Test
	public void shouldExecutePartialMatches() {
		
		MockOriginAccount MockGold =  new MockOriginAccount(GOLD, USD);
		TraderType Renata = new MockTrader("Renata", USD);
		
		BigDecimal mockgoldInitialUSDBalance = formatNumber(500d); 
		BigDecimal mockgoldInitialGOLDBalance = formatNumber(500d); 
		BigDecimal renataInitialUSDBalance = formatNumber(260.44d); 
		BigDecimal renataInitialGOLDBalance = formatNumber(0d);
		
		assertEquals(mockgoldInitialUSDBalance, MockGold.getWalletAsset(USD).getBalance());
		assertEquals(mockgoldInitialGOLDBalance, MockGold.getWalletAsset(GOLD).getBalance());
		assertEquals(renataInitialUSDBalance, Renata.getWalletAsset(USD).getBalance());
		assertEquals(renataInitialGOLDBalance, Renata.getWalletAsset(GOLD).getBalance());
		
		BigDecimal commomGoldUsdPrice = formatNumber(40.12d);
		BigDecimal commomAmount = formatNumber(0.243309d);
		BigDecimal commomGOLDxUSD = formatNumber(9.76155708d);
		Double renataOffereddUsdAmount = commomAmount.subtract(formatNumber(0.1d)).doubleValue();
		Double renataOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		Double mockgoldOfferedGoldAmount = commomAmount.doubleValue();
		Double mockgoldOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD);
		
		Order renataBuyOrder = Renata.createOrder(orderbook, USD, renataOffereddUsdAmount, GOLD, renataOfferedGoldUsdPrice);
		assertEquals(Order.Type.BUY, renataBuyOrder.getType());
		assertEquals(Order.Status.CREATED, renataBuyOrder.getStatus());
		
		
		BigDecimal expectedRenataGoldAmount = renataInitialGOLDBalance.add(formatNumber(0.14330900d));
		BigDecimal expectedRenataUsdAmount = formatNumber(renataInitialUSDBalance.subtract(formatNumber(5.74955708d)).subtract(formatNumber(usdBaseTranactionFee)));
		BigDecimal expectedMockgoldGoldAmount = formatNumber(mockgoldInitialGOLDBalance.subtract(formatNumber(expectedRenataGoldAmount)));
		BigDecimal expectedMockgoldUsdAmount = formatNumber(mockgoldInitialUSDBalance.add(formatNumber(5.74955708d)).subtract(formatNumber(usdBaseTranactionFee)));

		assertEquals( formatNumber(0.14330900d), renataBuyOrder.getAssetTotalAmountPrice() );
		assertEquals( formatNumber(formatNumber(renataOffereddUsdAmount).multiply(formatNumber(renataOfferedGoldUsdPrice))), formatNumber(5.74955708d) ); //0.14330900d
		assertEquals( formatNumber(5.74955708d), renataBuyOrder.getOfferedAmount() );
		assertEquals( formatNumber(40.12000000d), renataBuyOrder.getExpectedAssetUnitPrice() );
		
		Order mockgoldSellOrder = MockGold.createOrder(orderbook, GOLD, mockgoldOfferedGoldAmount, USD, mockgoldOfferedGoldUsdPrice);
		assertEquals(Order.Type.SELL, mockgoldSellOrder.getType());
		
		Order retrievedMockgoldSellOrder;
		try {
			retrievedMockgoldSellOrder = orderbook.findOrderBy(Optional.of(GOLDxUSD.getPairName()), mockgoldSellOrder.getID()).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to search for an order now", e);
		}
		assertEquals(Order.Type.SELL, retrievedMockgoldSellOrder.getType());
		assertEquals(Order.Status.PARTIAL, mockgoldSellOrder.getStatus());
		assertEquals(mockgoldSellOrder, retrievedMockgoldSellOrder);
		assertEquals(Order.Status.PARTIAL, retrievedMockgoldSellOrder.getStatus());
		assertEquals(formatNumber(formatNumber(9.76155708d).subtract(formatNumber(5.74955708d)))/*4.01200000*/, retrievedMockgoldSellOrder.getAssetTotalAmountPrice());
		assertEquals(formatNumber(4.01200000d), retrievedMockgoldSellOrder.getAssetTotalAmountPrice() );
		assertEquals(formatNumber( formatNumber(4.01200000d).divide(formatNumber(40.12000000d)) )/*0.1d*/, retrievedMockgoldSellOrder.getOfferedAmount() );
		assertEquals(formatNumber(0.10000000d), retrievedMockgoldSellOrder.getOfferedAmount() );
		assertEquals(formatNumber(40.12000000d), retrievedMockgoldSellOrder.getExpectedAssetUnitPrice());
		
		Order retrievedRenataBuyOrder;
		try {
			retrievedRenataBuyOrder = orderbook.findOrderBy(Optional.of(GOLDxUSD.getPairName()), renataBuyOrder.getID()).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to search for an order now", e);
		}
		assertEquals(Order.Type.BUY, retrievedRenataBuyOrder.getType());
		assertEquals(Order.Status.FILLED, retrievedRenataBuyOrder.getStatus());
		assertEquals(formatNumber(0.14330900d), retrievedRenataBuyOrder.getAssetTotalAmountPrice());
		assertEquals(formatNumber(5.74955708d), retrievedRenataBuyOrder.getOfferedAmount());
		assertEquals(formatNumber(40.12000000d), retrievedRenataBuyOrder.getExpectedAssetUnitPrice());
		
		assertEquals(expectedRenataGoldAmount, Renata.getWalletAsset(GOLD).getBalance());//0.14330900
		assertEquals(expectedRenataUsdAmount, Renata.getWalletAsset(USD).getBalance());//254.68044292
		assertEquals(formatNumber(0d), Renata.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0d), Renata.getWalletAsset(USD).getBlockedBalance());
		assertEquals(expectedMockgoldGoldAmount, MockGold.getWalletAsset(GOLD).getBalance());//499.85669100
		BigDecimal expectedMockgoldGoldBlockedBalance = formatNumber(commomAmount.subtract(expectedRenataGoldAmount));
		assertEquals(expectedMockgoldGoldBlockedBalance, MockGold.getWalletAsset(GOLD).getBlockedBalance());//0.10000000
		assertEquals(expectedMockgoldUsdAmount, MockGold.getWalletAsset(USD).getBalance());//505.73955708
		assertEquals(formatNumber(0.1d), MockGold.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0.01d), MockGold.getWalletAsset(USD).getBlockedBalance());
	}
	
	@Ignore
	@Test
	public void shouldExecutePartialMatchesSellBuy() {
		
		MockOriginAccount MockGold =  new MockOriginAccount(GOLD, USD);
		TraderType Renata = new MockTrader("Renata", USD);
		
		BigDecimal mockgoldInitialUSDBalance = formatNumber(500d); 
		BigDecimal mockgoldInitialGOLDBalance = formatNumber(500d); 
		BigDecimal renataInitialUSDBalance = formatNumber(260.44d); 
		BigDecimal renataInitialGOLDBalance = formatNumber(0d);
		
		assertEquals(mockgoldInitialUSDBalance, MockGold.getWalletAsset(USD).getBalance());
		assertEquals(mockgoldInitialGOLDBalance, MockGold.getWalletAsset(GOLD).getBalance());
		assertEquals(renataInitialUSDBalance, Renata.getWalletAsset(USD).getBalance());
		assertEquals(renataInitialGOLDBalance, Renata.getWalletAsset(GOLD).getBalance());
		
		BigDecimal commomGoldUsdPrice = formatNumber(40.12d);
		BigDecimal commomAmount = formatNumber(0.243309d);
		BigDecimal commomGOLDxUSD = formatNumber(9.76155708d);
		Double renataOffereddUsdAmount = commomAmount.subtract(formatNumber(0.1d)).doubleValue();
		Double renataOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		Double mockgoldOfferedGoldAmount = commomAmount.doubleValue();
		Double mockgoldOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		
		
		BigDecimal expectedRenataGoldAmount = renataInitialGOLDBalance.add(formatNumber(0.14330900d));
		BigDecimal expectedRenataUsdAmount = formatNumber(renataInitialUSDBalance.subtract(formatNumber(5.74955708d)).subtract(formatNumber(usdBaseTranactionFee)));
		BigDecimal expectedMockgoldGoldAmount = formatNumber(mockgoldInitialGOLDBalance.subtract(formatNumber(expectedRenataGoldAmount)));
		BigDecimal expectedMockgoldUsdAmount = formatNumber(mockgoldInitialUSDBalance.add(formatNumber(5.74955708d)).subtract(formatNumber(usdBaseTranactionFee)));
		
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD);
		
		Order mockgoldSellOrder = MockGold.createOrder(orderbook, GOLD, mockgoldOfferedGoldAmount, USD, mockgoldOfferedGoldUsdPrice);
		assertEquals(Order.Type.SELL, mockgoldSellOrder.getType());
		assertEquals(Order.Status.CREATED, mockgoldSellOrder.getStatus());
		
		Order renataBuyOrder = Renata.createOrder(orderbook, USD, renataOffereddUsdAmount, GOLD, renataOfferedGoldUsdPrice);
		assertEquals(Order.Type.BUY, renataBuyOrder.getType());
		
		assertEquals( formatNumber(0.14330900d), renataBuyOrder.getAssetTotalAmountPrice() );
		assertEquals( formatNumber(formatNumber(renataOffereddUsdAmount).multiply(formatNumber(renataOfferedGoldUsdPrice))), formatNumber(5.74955708d) ); //0.14330900d
		assertEquals( formatNumber(5.74955708d), renataBuyOrder.getOfferedAmount() );
		assertEquals( formatNumber(40.12000000d), renataBuyOrder.getExpectedAssetUnitPrice() );
		
		//throw new UnsupportedOperationException("Not yet implemented test");

		Order retrievedMockgoldSellOrder;
		try {
			retrievedMockgoldSellOrder = orderbook.findOrderBy(Optional.of(GOLDxUSD.getPairName()), mockgoldSellOrder.getID()).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to search for an order now", e);
		}
		assertEquals(Order.Type.SELL, retrievedMockgoldSellOrder.getType());
		assertEquals(Order.Status.PARTIAL, mockgoldSellOrder.getStatus());
		assertEquals(mockgoldSellOrder, retrievedMockgoldSellOrder);
		assertEquals(Order.Status.PARTIAL, retrievedMockgoldSellOrder.getStatus());
		assertEquals(formatNumber(formatNumber(9.76155708d).subtract(formatNumber(5.74955708d)))/*4.01200000*/, retrievedMockgoldSellOrder.getAssetTotalAmountPrice());
		assertEquals(formatNumber(4.01200000d), retrievedMockgoldSellOrder.getAssetTotalAmountPrice() );
		assertEquals(formatNumber( formatNumber(4.01200000d).divide(formatNumber(40.12000000d)) )/*0.1d*/, retrievedMockgoldSellOrder.getOfferedAmount() );
		assertEquals(formatNumber(0.10000000d), retrievedMockgoldSellOrder.getOfferedAmount() );
		assertEquals(formatNumber(40.12000000d), retrievedMockgoldSellOrder.getExpectedAssetUnitPrice());
		
		Order retrievedRenataBuyOrder;
		try {
			retrievedRenataBuyOrder = orderbook.findOrderBy(Optional.of(GOLDxUSD.getPairName()), renataBuyOrder.getID()).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to search for an order now", e);
		}
		assertEquals(Order.Type.BUY, retrievedRenataBuyOrder.getType());
		assertEquals(Order.Status.FILLED, retrievedRenataBuyOrder.getStatus());
		assertEquals(formatNumber(0.14330900d), retrievedRenataBuyOrder.getAssetTotalAmountPrice());
		assertEquals(formatNumber(5.74955708d), retrievedRenataBuyOrder.getOfferedAmount());
		assertEquals(formatNumber(40.12000000d), retrievedRenataBuyOrder.getExpectedAssetUnitPrice());
		
		assertEquals(expectedRenataGoldAmount, Renata.getWalletAsset(GOLD).getBalance());//0.14330900
		assertEquals(expectedRenataUsdAmount, Renata.getWalletAsset(USD).getBalance());//254.68044292
		assertEquals(formatNumber(0d), Renata.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0d), Renata.getWalletAsset(USD).getBlockedBalance());
		assertEquals(expectedMockgoldGoldAmount, MockGold.getWalletAsset(GOLD).getBalance());//499.85669100
		BigDecimal expectedMockgoldGoldBlockedBalance = formatNumber(commomAmount.subtract(expectedRenataGoldAmount));
		assertEquals(expectedMockgoldGoldBlockedBalance, MockGold.getWalletAsset(GOLD).getBlockedBalance());//0.10000000
		assertEquals(expectedMockgoldUsdAmount, MockGold.getWalletAsset(USD).getBalance());//505.73955708
		assertEquals(formatNumber(0.1d), MockGold.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0.01d), MockGold.getWalletAsset(USD).getBlockedBalance());
	}
	
	@Test
	public void shouldAnTraderBeAbleToCancelAnCreatedOrder(){
		throw new UnsupportedOperationException("Not yet Implemented test.");
	}
	
	@Test
	public void shouldNotHaveOrdersInThePairAfterTheMatch(){
		OrderBook orderbook = execute();
		
		PairOrders goldXusdPairOrders = orderbook.retrievePairOrders(GOLDxUSD);
		assertEquals(0, goldXusdPairOrders.retrieveBuyOrders().size());
		assertEquals(0, goldXusdPairOrders.retrieveSellOrders().size());
		PairOrders silverXusdPairOrders = orderbook.retrievePairOrders(SILVERxUSD);
		assertEquals(0, silverXusdPairOrders.retrieveBuyOrders().size());
		assertEquals(0, silverXusdPairOrders.retrieveSellOrders().size());
		PairOrders bronzeXusdPairOrders = orderbook.retrievePairOrders(BRONZExSILVER);
		assertEquals(0, bronzeXusdPairOrders.retrieveBuyOrders().size());
		assertEquals(0, bronzeXusdPairOrders.retrieveSellOrders().size());
	}
	
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

	@Test
	public void shouldNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(){
		MockOriginAccount mockOriginAccountGOLD =  new MockOriginAccount(GOLD);
		TraderType thiago = new MockTrader("Thiago", USD);
		OrderBook orderbook = new OrderBook(USD, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		
		executeNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(orderbook, mockOriginAccountGOLD, thiago);

	}
	
	@Test
	public void shouldPrintAPairWithThreeBuyAndSellOrders(){
		throw new UnsupportedOperationException("Not yet Implemented test.");
	}
	
	@Test
	public void shouldAtraderRemoveFromOrderBookAcreatedOrder(){
		MockOriginAccount mockOriginAccountGOLD =  new MockOriginAccount(GOLD);
		TraderType thiago = new MockTrader("Thiago", USD);
		OrderBook orderbook = new OrderBook(USD, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		
		PairOrders goldXusdPairOrders = executeNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(orderbook, mockOriginAccountGOLD, thiago);
		
		Order ask = goldXusdPairOrders.getAskOrder();
		Order bid = goldXusdPairOrders.getBidOrder();
		assertNotNull(ask);
		assertNotNull(bid);
		
		mockOriginAccountGOLD.removeCreatedOrder(bid, orderbook, goldXusdPairOrders);
		thiago.removeCreatedOrder(ask, orderbook, goldXusdPairOrders);
		assertNull(goldXusdPairOrders.getAskOrder());
		assertNull(goldXusdPairOrders.getBidOrder());
	}
	
	public void shouldThrowExceptionWhenTryToCreateOrderWithputEnoughBalanceToPayAtLeastTheFee(){
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(
				"ERROR: unable to create order. There no enough {asset=USD} balance "
				+ "{balance=0} to pay the transaction fee {transactionFee=0.01}.");
	}
	
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
	
	@Test
	public void shouldThrowExceptionWhenToCreateAnOrderWithNotEnoughBalanceToPayTheTransactionFee(){
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(
				"ERROR: unable to create order. Wallet has not enough fee asset {feeAssetName="
				+ "USD} balance. The minimal balance needed must to be major than "
				+ "{feeAssetValue=0.01000000} but was {feeAssetBalances=0.00000000}");
		
		TraderType Renata = new Trader("Renata");
		
		BigDecimal renataInitialUSDBalance = formatNumber(0d); 
		BigDecimal renataInitialGOLDBalance = formatNumber(0d);
		
		assertEquals(renataInitialUSDBalance, Renata.getWalletAsset(USD).getBalance());
		assertEquals(renataInitialGOLDBalance, Renata.getWalletAsset(GOLD).getBalance());
		
		BigDecimal commomGoldUsdPrice = formatNumber(40.12d);
		BigDecimal commomAmount = formatNumber(0.243309d);
		Double renataOffereddUsdAmount = commomAmount.doubleValue();
		Double renataOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD);
		
		Order renataBuyOrder = Renata.createOrder(orderbook, USD, renataOffereddUsdAmount, GOLD, renataOfferedGoldUsdPrice); 
	}
	
	@Test
	public void createAndAutoeexecuteOrdersWithNonZeroTransactionFeesMultithreading(){
		throw new UnsupportedOperationException("Not yet Implemented test."); //TODO: Need to figure out how to do it
	}
	
	@Test
	public void createAndAutoeexecuteOrdersWithZeroTransactionFees() {
		
		execute();
	}
	
	@Test
	public void shouldThrowExceptionWhenCreateOrderWithNotEnoughBalance() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(
				"ERROR: unable to create order for the asset {asset=GOLD} "
				+ "offering the amount {offeredAmount=4.12} and expecting "
				+ "for {expectedAssetUnitPrice=1.0} having a balance "
				+ "{balance=4.11000000} minus blocked balance "
				+ "{blockedBalance=0.00000000} lower than "
				+ "assetTotalAmountPrice {assetTotalAmountPrice=4.120}.");
		
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
		offeredAmount = 4.12d;
		expectedAsset = USD;
		expectedAssetUnitPrice = 1.00d;
		renata.createOrder(orderbook, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice);
	}
	
	private OrderBook execute() {
		
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
	
	private OrderBook execute(Double usdAmount,  Double usdUnitPrice,  Double goldAmount,
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

	private PairOrders executeNotMatch(MockOriginAccount mockOriginAccountGOLD, TraderType thiago, OrderBook orderbook,
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

	private PairOrders executeNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(OrderBook orderbook, MockOriginAccount mockOriginAccountGOLD, TraderType thiago) {
		
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