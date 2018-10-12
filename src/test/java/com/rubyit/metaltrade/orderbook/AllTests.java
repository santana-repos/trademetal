package com.rubyit.metaltrade.orderbook;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
		{
			ShouldAnTraderBeAbleToCancelAnCreatedOrder.class,
			ShouldAtraderRemoveFromOrderBookAcreatedOrder.class,
			ShouldExecutePartialMatches.class,
			ShouldExecutePartialMatchesReverse.class,
			ShouldExecutePerfectMatch.class,
			ShouldHaveAPairWithNotNullAskOrder.class,
			ShouldHaveAPairWithNotNullBidOrder.class,
			ShouldNotHaveOrdersInThePairAfterTheMatch.class,
			ShouldNotMatchTwoOrdersIfTheAskPriceIsMajorThanTheBidPrice.class,
			ShouldNotMatchTwoOrdersIfTheBidPriceIsMajorThanTheAskPrice.class,
			ShouldPrintAPairWithThreeBuyAndSellOrders.class,
			ShouldThrowExceptionWhenCreateOrderWhenBalancePlusBlockbalanceIsNotEnough.class,
			ShouldThrowExceptionWhenCreateOrderWithNotEnoughBalance.class,
			ShouldThrowExceptionWhenTryToCreateAnOrderWithNotEnoughBalanceToPayTheTransactionFee.class,
			ShoundCreateAndAutoeexecuteOrdersWithNonZeroTransactionFeesMultithreading.class,
			ShoundCreateAndAutoeexecuteOrdersWithZeroTransactionFees.class
		} )
public class AllTests {

}
