package com.rubyit.metaltrade.orderbook;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.rubyit.metaltrade.TraderType;

public class ShouldAtraderRemoveFromOrderBookAcreatedOrder extends BaseTests {
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
}
