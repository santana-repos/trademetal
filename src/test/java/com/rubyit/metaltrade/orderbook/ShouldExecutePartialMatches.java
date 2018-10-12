package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;

public class ShouldExecutePartialMatches extends BaseTests {

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
		Double renataOffereddUsdAmount = commomAmount.subtract(formatNumber(0.1d)).doubleValue(); //0.14330900
		Double renataOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		Double mockgoldOfferedGoldAmount = commomAmount.doubleValue(); //0.24330900
		Double mockgoldOfferedGoldUsdPrice = commomGoldUsdPrice.doubleValue();
		
		OrderBook orderbook = new OrderBook(USD, usdBaseTranactionFee, GOLDxUSD);
		
		//// Renata want to give 5.74955708 USD to get 0.14330900 GOLD
		//// She used her USD to BUY GOLD at 40.12000000 USD per 1 USD
		Order renataBuyOrder = Renata.createOrder(orderbook, USD, /*0.14330900*/renataOffereddUsdAmount, GOLD, /*40.12000000*/renataOfferedGoldUsdPrice);
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
		
		//// MockGold want to give 0.24330900 GOLD to get 9.76155708 USD   
		//// He SELLed his GOLD to got USD at 40.12000000 USD per 1 USD
		Order mockgoldSellOrder = MockGold.createOrder(orderbook, GOLD, /*0.24330900*/mockgoldOfferedGoldAmount, USD, /*40.12000000*/mockgoldOfferedGoldUsdPrice);
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
		
		//// Renata got all the Gold she wanted
		assertEquals(expectedRenataGoldAmount, Renata.getWalletAsset(GOLD).getBalance());//0.14330900
		assertEquals(expectedRenataUsdAmount, Renata.getWalletAsset(USD).getBalance());//254.68044292
		assertEquals(formatNumber(0d), Renata.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0d), Renata.getWalletAsset(USD).getBlockedBalance());
		assertEquals(expectedMockgoldGoldAmount, MockGold.getWalletAsset(GOLD).getBalance());//499.85669100
		BigDecimal expectedMockgoldGoldBlockedBalance = formatNumber(commomAmount.subtract(expectedRenataGoldAmount));
		assertEquals(expectedMockgoldGoldBlockedBalance, MockGold.getWalletAsset(GOLD).getBlockedBalance());//0.10000000
		//// MockGold just got partial USD he wanted
		assertEquals(expectedMockgoldUsdAmount, MockGold.getWalletAsset(USD).getBalance());//505.73955708
		assertEquals(formatNumber(0.1d), MockGold.getWalletAsset(GOLD).getBlockedBalance());
		assertEquals(formatNumber(0.01d), MockGold.getWalletAsset(USD).getBlockedBalance());
	}
}
