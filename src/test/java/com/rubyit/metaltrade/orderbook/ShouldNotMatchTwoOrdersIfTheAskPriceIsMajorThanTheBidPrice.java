package com.rubyit.metaltrade.orderbook;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;

public class ShouldNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice extends BaseTests {
	@Test
	public void shouldNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(){
		MockOriginAccount mockOriginAccountGOLD =  new MockOriginAccount(GOLD);
		TraderType thiago = new MockTrader("Thiago", USD);
		OrderBook orderbook = new OrderBook(USD, GOLDxUSD, SILVERxUSD, BRONZExUSD, GOLDxSILVER, BRONZExSILVER);
		
		executeNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice(orderbook, mockOriginAccountGOLD, thiago);

	}
}
