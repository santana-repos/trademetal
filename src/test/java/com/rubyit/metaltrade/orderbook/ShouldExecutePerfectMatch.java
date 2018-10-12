package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;

public class ShouldExecutePerfectMatch extends BaseTests {

	@Test
	public void shouldExecutePerfectMatch() {
		
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
}
