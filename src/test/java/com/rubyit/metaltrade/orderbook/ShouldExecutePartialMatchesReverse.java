package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;

public class ShouldExecutePartialMatchesReverse extends BaseTests {

	@Test
	public void shouldExecutePartialMatchesReverse() {
		
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
		BigDecimal commomAmount = formatNumber(0.143309d);
		Double renataOffereddUsdAmount = commomAmount.add(formatNumber(0.1d)).doubleValue(); //0.24330900
		Double renataOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		Double mockgoldOfferedGoldAmount = commomAmount.doubleValue(); //0.14330900
		Double mockgoldOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD);
		
		
		BigDecimal expectedRenataGoldAmount = renataInitialGOLDBalance.add(formatNumber(0.14330900d));
		BigDecimal expectedRenataUsdAmount = formatNumber(renataInitialUSDBalance.subtract(formatNumber(5.74955708d)).subtract(formatNumber(usdBaseTranactionFee)));
		BigDecimal expectedMockgoldGoldAmount = formatNumber(mockgoldInitialGOLDBalance.subtract(formatNumber(expectedRenataGoldAmount)));
		BigDecimal expectedMockgoldUsdAmount = formatNumber(mockgoldInitialUSDBalance.add(formatNumber(5.74955708d)).subtract(formatNumber(usdBaseTranactionFee)));
		
		//// MockGold want to give 0.14330900 GOLD to get 5.74955708 USD   
		//// He SELLed his GOLD to got USD at 40.12000000 USD per 1 USD
		Order mockgoldSellOrder = MockGold.createOrder(orderbook, GOLD, /*0.14330900*/mockgoldOfferedGoldAmount, USD, /*40.12000000*/mockgoldOfferedGoldUsdPrice);
		assertEquals(Order.Type.SELL, mockgoldSellOrder.getType());
		assertEquals(Order.Status.CREATED, mockgoldSellOrder.getStatus());
		
		assertEquals( formatNumber(5.74955708d), mockgoldSellOrder.getAssetTotalAmountPrice() );
		assertEquals( formatNumber(formatNumber(mockgoldOfferedGoldAmount).multiply(formatNumber(mockgoldOfferedGoldUsdPrice))), formatNumber(5.74955708) ); //0.14330900d
		assertEquals( formatNumber(0.14330900d), mockgoldSellOrder.getOfferedAmount() );
		assertEquals( formatNumber(40.12000000d), mockgoldSellOrder.getExpectedAssetUnitPrice() );
		
		
		//// Renata want to give 9.76155708 USD to get 0.24330900 GOLD
		//// She used her USD to BUY GOLD at 40.12000000 USD per 1 USD
		Order renataBuyOrder = Renata.createOrder(orderbook, USD, /*0.24330900*/renataOffereddUsdAmount, GOLD, /*40.12000000*/renataOfferedGoldUsdPrice);
		assertEquals(Order.Type.BUY, renataBuyOrder.getType());
		assertEquals(Order.Status.PARTIAL, renataBuyOrder.getStatus());
		
		Order retrievedRenataBuyOrder;
		try {
			retrievedRenataBuyOrder = orderbook.findOrderBy(Optional.of(GOLDxUSD.getPairName()), renataBuyOrder.getID()).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to search for an order now", e);
		}
		assertEquals(renataBuyOrder, retrievedRenataBuyOrder);
		
		assertEquals(Order.Status.PARTIAL, retrievedRenataBuyOrder.getStatus());
		assertEquals(formatNumber(formatNumber(0.24330900d).subtract(formatNumber(0.1d)))/*0.14330900*/, retrievedRenataBuyOrder.getAssetTotalAmountPrice());
		assertEquals(formatNumber(0.14330900d), retrievedRenataBuyOrder.getAssetTotalAmountPrice() );
		assertEquals(formatNumber( formatNumber(0.24330900d).multiply(formatNumber(40.12000000d)) )/*9.76155708d*/, retrievedRenataBuyOrder.getOfferedAmount() );
		assertEquals(formatNumber(9.76155708d), retrievedRenataBuyOrder.getOfferedAmount() );
		assertEquals(formatNumber(40.12000000d), retrievedRenataBuyOrder.getExpectedAssetUnitPrice());
		
		Order retrievedMockgoldSellOrder;
		try {
			retrievedMockgoldSellOrder = orderbook.findOrderBy(Optional.of(GOLDxUSD.getPairName()), mockgoldSellOrder.getID()).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to search for an order now", e);
		}
		assertEquals(Order.Type.SELL, retrievedMockgoldSellOrder.getType());
		assertEquals(Order.Status.FILLED, retrievedMockgoldSellOrder.getStatus());
		assertEquals(formatNumber(5.74955708d), retrievedMockgoldSellOrder.getAssetTotalAmountPrice());
		assertEquals(formatNumber(0.14330900d), retrievedMockgoldSellOrder.getOfferedAmount());
		assertEquals(formatNumber(40.12000000d), retrievedMockgoldSellOrder.getExpectedAssetUnitPrice());
		
		//// Renata got just partial Gold she wanted
		assertEquals(formatNumber(0.14330900d), expectedRenataGoldAmount);//0.14330900
		assertEquals(expectedRenataGoldAmount, Renata.getWalletAsset(GOLD).getBalance());//0.14330900
		assertEquals(formatNumber(0d), Renata.getWalletAsset(GOLD).getBlockedBalance());
		BigDecimal renataBlockedUSDAmount = formatNumber( formatNumber(formatNumber(9.76155708d).subtract(formatNumber(5.74955708d))).add(formatNumber(0.01d)) );
		assertEquals(renataBlockedUSDAmount, Renata.getWalletAsset(USD).getBlockedBalance()); //4.02200000
		assertEquals(formatNumber(254.68044292d), expectedRenataUsdAmount);//254.68044292
		assertEquals(expectedRenataUsdAmount, Renata.getWalletAsset(USD).getBalance());//254.68044292
		
		
		//// MockGold got all USD he wanted
		assertEquals(expectedMockgoldUsdAmount, MockGold.getWalletAsset(USD).getBalance());//505.73955708
		assertEquals(formatNumber(0d), MockGold.getWalletAsset(USD).getBlockedBalance());
		assertEquals(expectedMockgoldGoldAmount, MockGold.getWalletAsset(GOLD).getBalance());//499.85669100
		assertEquals(formatNumber(0d), MockGold.getWalletAsset(GOLD).getBlockedBalance());
	}
}
