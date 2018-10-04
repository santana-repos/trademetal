package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.getGson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.rubyit.metaltrade.obj.Pair;

public class PairOrders {

	private Pair pair;
	private Set<Order> sellOrders;
	private Set<Order> buyOrders;

	public PairOrders(Pair pair) {
		this.pair = pair;
		this.sellOrders = new TreeSet<>();
		this.buyOrders = new TreeSet<>();
	}

	public Pair getPair() {
		return pair;
	}

	public void addSellOrder(Order order) {
		sellOrders.add(order);
	}

	public void removeSellOrder(Order order) {
		for (Order o : sellOrders) {
			if (o.getID().equals(order.getID())) {
				sellOrders.remove(o);
			}
		}
	}

	public void addBuyOrder(Order order) {
		buyOrders.add(order);

	}

	public void removeBuyOrder(Order order) {
		for (Order o : buyOrders) {
			if (o.getID().equals(order.getID())) {
				buyOrders.remove(o);
			}
		}
	}

	public List<Order> retrieveSellOrders() {
		return new ArrayList<>(sellOrders);
	}

	public List<Order> retrieveBuyOrders() {
		return new ArrayList<>(buyOrders);
	}

	public Order getBidOrder() {
		List<Order> sellOrders = retrieveSellOrders();
		return (sellOrders.size() > 0) ? sellOrders.get(0) : null;
	}
	
	public Order getAskOrder() {
		List<Order> buyOrders = retrieveBuyOrders();
		return (buyOrders.size() > 0) ? buyOrders.get(buyOrders.size() - 1) : null;
	}

	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("pair", pair);
		json.put("sellOrders", retrieveSellOrders());
		json.put("buyOrders", retrieveBuyOrders());
		return getGson().toJson(json);
	}
}
