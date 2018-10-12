package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.rubyit.metaltrade.Trader;
import com.rubyit.metaltrade.TraderType;

public class ShouldThrowExceptionWhenTryToCreateAnOrderWithNotEnoughBalanceToPayTheTransactionFee extends BaseTests {
	
	@Test
	public void shouldThrowExceptionWhenTryToCreateAnOrderWithNotEnoughBalanceToPayTheTransactionFee(){
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
}
