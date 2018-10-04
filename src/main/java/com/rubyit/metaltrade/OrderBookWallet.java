package com.rubyit.metaltrade;

import static com.rubyit.metaltrade.Utils.getGson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rubyit.metaltrade.orderbook.Order;

public class OrderBookWallet {
	
	private Wallet wallet;
	private List<Order> executedOrders;

	public OrderBookWallet() {
		wallet = new Wallet();
		executedOrders = new ArrayList<>();
	}
	
	public void payTransactionFee(Order order) {
		if (order.getTransactionFee().getTransactionFeeValue().compareTo(BigDecimal.ZERO) > 0) {
			
			wallet.getAsset(order.getTransactionFee().getTransactionFeeAssetType()).deposit(order.getTransactionFee().getTransactionFeeValue());
		}
		executedOrders.add(order);
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("wallet", wallet);
		json.put("executedOrders", executedOrders);
		return getGson().toJson(json);
	}
}
