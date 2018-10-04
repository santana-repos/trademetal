package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.getGson;

import java.util.LinkedHashMap;
import java.util.Map;

import com.rubyit.metaltrade.orderbook.Order.Status;

public class TraderOrder {

	private Order order;
	private Status status;
	
	public TraderOrder(Order order, Status status) {
		this.order = order;
		this.status = status;
	}

	public Order getOrder() {
		return order;
	}

	public Status getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("content", order);
		json.put("status", status);
		return getGson().toJson(json);
	}
}
