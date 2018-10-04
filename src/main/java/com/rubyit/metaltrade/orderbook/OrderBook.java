package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.formatNumber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.rubyit.metaltrade.OrderBookWallet;
import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;
import com.rubyit.metaltrade.obj.Pair;
import com.rubyit.metaltrade.orderbook.Order.Status;

public class OrderBook {
	
	private Set<PairOrders> pairs;
	private Set<TraderType> traders;
	private OrderBookWallet bookwallet;
	private TransactionFee transactionFee;
	transient private Lock pairChangeLock;
	
	public OrderBook(AssetType transactionFeeAssetType, Pair... pairs) {
		if (pairs == null || pairs.length == 0) {
			throw new RuntimeException("ERROR: unable to create a orderbook without at least one Pair");
		}
		this.bookwallet = new OrderBookWallet();
		this.pairs = new HashSet<>();
		this.traders = new HashSet<>();
		this.transactionFee = new TransactionFee(0d, transactionFeeAssetType); // Fee ZERO
		for (Pair pair : pairs) {
			this.pairs.add(new PairOrders(pair));
		}
		pairChangeLock = new ReentrantLock();
	}
	
	public OrderBook(AssetType transactionFeeAssetType, Double transactionFeeValue, Pair... pairs) {
		this(transactionFeeAssetType, pairs);
		this.transactionFee = new TransactionFee(transactionFeeValue, transactionFeeAssetType);
	}
	
	public TransactionFee retrieveTransactionFee() {
		return transactionFee;
	}
	
	public List<TraderType> retrieveAllTraders() {
		return new ArrayList<>(traders);
	}
	
	public TraderType retrieveTrader(String traderID) {
		for (TraderType trader : traders) {
			
			if (trader.getID().equals(traderID)) {
				return trader;
			}
		}
		return null;
	}
	
	public TraderType retrieveTrader(Optional<String> traderName) {
		for (TraderType trader : traders) {
			
			if (trader.getName().equals(traderName.get())) {
				return trader;
			}
		}
		return null;
	}

	public Order createOrder(TraderType trader, AssetType offeredAsset, Double offeredAmount, AssetType expectedAsset,
			Double expectedAssetUnitPrice) {
		
		String offeredAssetID = offeredAsset.getID();
		String expectedAssetID = expectedAsset.getID();
		PairOrders pair = findPairBy(offeredAssetID, expectedAssetID);
		Order order = null;

		if (pair == null) {
			
			for (String id : Arrays.asList(offeredAssetID, expectedAssetID)) {
				pair = findPairBy(id);
				if (pair != null) {
					break;
				}
			}
		}
		if (pair == null) {
			throw new RuntimeException("ERROR: unable to find on the orderbook a "
					+ "compatible Pair for Assets with {offeredAsset=" + offeredAsset + 
					", expectedAsset=" + expectedAsset + "}");
		}
		
		String amountAssetID = pair.getPair().getAmountAsset().getID();
		String priceAssetID = pair.getPair().getPriceAsset().getID();
		if (
			(offeredAssetID.equals(amountAssetID))
			&& (expectedAssetID.equals(priceAssetID))
		   ) {
			
			traders.add(trader);
			Order.Type orderType = Order.Type.SELL;
			order = new Order(trader, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice, pair.getPair(), orderType, transactionFee);
			trader.addCreatedOrder(order, this, pair);

			//look to buyOrders
			Order matchedOrder = (pair.retrieveBuyOrders().size() > 0) ? pair.getAskOrder() : null;
			
			return processMatchOrders(trader, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice, pair,
					orderType, matchedOrder, order);
		}
		if (
			(offeredAssetID.equals(priceAssetID))
			&& (expectedAssetID.equals(amountAssetID))
		   ) {
			
			traders.add(trader);
			Order.Type orderType = Order.Type.BUY;
			order = new Order(trader, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice, pair.getPair(), orderType, transactionFee);
			trader.addCreatedOrder(order, this, pair);
			
			//look to sellOrders
			Order matchedOrder = (pair.retrieveSellOrders().size() > 0) ? pair.getBidOrder() : null;
			
			return processMatchOrders(trader, offeredAsset, offeredAmount, expectedAsset, expectedAssetUnitPrice, pair,
					orderType, matchedOrder, order);
		}
		
		throw new RuntimeException("ERROR: that order {offeredAsset=" + offeredAsset + ", expectedAsset=" + expectedAsset+ "} doesn't belongs to that pair {" + pair + "}");
	}

	private Order processMatchOrders(TraderType trader, AssetType offeredAsset, Double offeredAmount,
			AssetType expectedAsset, Double expectedAssetUnitPrice, PairOrders pair, Order.Type orderType,
			Order matchedOrder, Order order) {
		
		if (matchedOrder == null || matchedOrder.getExpectedAssetUnitPrice().compareTo(order.getExpectedAssetUnitPrice()) != 0) {
			
			return order;
		}
		
		String otherTraderID = matchedOrder.getTraderID();
		if (trader.getID().equals(otherTraderID)) {
			return order;
		}
		
		for (TraderType otherTrader : traders) {
			
			if (otherTrader.getID().equals(otherTraderID)) {
				
				BigDecimal moTotalAmountPrice = formatNumber(matchedOrder.getAssetTotalAmountPrice());
				BigDecimal oOfferedAmount = formatNumber(order.getOfferedAmount());
				
				if (moTotalAmountPrice.compareTo(oOfferedAmount) == 0) {
				
					performPerfectMatch(trader, pair, matchedOrder, order, otherTrader);
				} else if (oOfferedAmount.compareTo(moTotalAmountPrice) > 0) {
					
					// to change matchedOrder from Created/Filled to Partial
					BigDecimal localOfferedAmount = formatNumber(matchedOrder.getOfferedAmount());
					BigDecimal localExpectedUnitPrice = formatNumber(matchedOrder.getExpectedAssetUnitPrice());
					BigDecimal localTotalAmountPrice = formatNumber(matchedOrder.getAssetTotalAmountPrice());
					trader.fillOrder(matchedOrder);
					
					// to change order from Created to Filled
					localOfferedAmount = formatNumber(order.getOfferedAmount());
					localExpectedUnitPrice = formatNumber(order.getExpectedAssetUnitPrice());
					localTotalAmountPrice = formatNumber(order.getAssetTotalAmountPrice());
					otherTrader.fillOrder(order);
					
					bookwallet.payTransactionFee(order);
					bookwallet.payTransactionFee(matchedOrder);
					trader.removeCreatedOrder(order, this, pair);
					otherTrader.removeCreatedOrder(matchedOrder, this, pair);
					
					Order partialFilledOrder = new Order(matchedOrder, Status.PARTIAL);
					trader.addCreatedOrder(partialFilledOrder, this, pair);
					order = partialFilledOrder;
				}
			}
		}
		
		return order;
	}

	private void performPerfectMatch(TraderType trader, PairOrders pair, Order matchedOrder, Order order,
			TraderType otherTrader) {
		
		BigDecimal localOfferedAmount = formatNumber(matchedOrder.getOfferedAmount());
		BigDecimal localExpectedUnitPrice = formatNumber(matchedOrder.getExpectedAssetUnitPrice());
		BigDecimal localTotalAmountPrice = formatNumber(matchedOrder.getAssetTotalAmountPrice());
		trader.fillOrder(matchedOrder);
		
		localOfferedAmount = formatNumber(order.getOfferedAmount());
		localExpectedUnitPrice = formatNumber(order.getExpectedAssetUnitPrice());
		localTotalAmountPrice = formatNumber(order.getAssetTotalAmountPrice());
		otherTrader.fillOrder(order);
		
		bookwallet.payTransactionFee(order);
		bookwallet.payTransactionFee(matchedOrder);
		trader.removeCreatedOrder(order, this, pair);
		otherTrader.removeCreatedOrder(matchedOrder, this, pair);
	}
	
	public PairOrders findPairBy(String offeredAssetID, String expectedAssetID) {
		
		pairChangeLock.lock();
		try {
			for (PairOrders pair : pairs) {
				String amountAssetID = pair.getPair().getAmountAsset().getID();
				String priceAssetID = pair.getPair().getPriceAsset().getID();
				if ((amountAssetID.equals(offeredAssetID)) && (priceAssetID.equals(expectedAssetID))) {
					return pair;
				}
				if ((priceAssetID.equals(offeredAssetID)) && (amountAssetID.equals(expectedAssetID))) {
					return pair;
				}
			}
		} finally {
			pairChangeLock.unlock();
			
		}
		
		return null;
	}
	
	public PairOrders findPairBy(Optional<String> optionalPairName) {
		
		pairChangeLock.lock();
		try {
			for (PairOrders pair : pairs) {
				String pairName = pair.getPair().getPairName();
				if (optionalPairName.get().equals(pairName)) {
					return pair;
				}
			}
		} finally {
			pairChangeLock.unlock();
			
		}
		
		return null;
	}
	
	public PairOrders findPairBy(String assetID) {
		
		pairChangeLock.lock();
		try {
			for (PairOrders pair : pairs) {
				String amountAssetID = pair.getPair().getAmountAsset().getID();
				String priceAssetID = pair.getPair().getPriceAsset().getID();
				if (amountAssetID.equals(assetID)) {
					return pair;//new CurrencyPair(currencyPair);
				}
				if (priceAssetID.equals(assetID)) {
					return pair;//new CurrencyPair(currencyPair);
				}
			}
		} finally {
			pairChangeLock.unlock();
			
		}
		
		return null;
	}

	public PairOrders retrievePairOrders(Pair pair) {
		if (pair == null) {
			throw new RuntimeException("ERROR: unable to perform search with a null Pair.");
		}
		return findPairBy(Optional.of(pair.getPairName()));
	}
	
	public AssetType retrieveTransactionFeeAssetType() {
		return transactionFee.getTransactionFeeAssetType();
	}
	
	public BigDecimal retrieveTransactionFeeValue() {
		return transactionFee.getTransactionFeeValue();
	}
}


