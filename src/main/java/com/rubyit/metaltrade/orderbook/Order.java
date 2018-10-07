package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.getGson;
import static com.rubyit.metaltrade.Utils.formatNumber;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.rubyit.metaltrade.TraderType;
import com.rubyit.metaltrade.obj.AssetType;
import com.rubyit.metaltrade.obj.Pair;

public class Order implements Comparable<Order> {

	private String ID;
	private String traderID;
	private AssetType offeredAsset;
	private BigDecimal offeredAmount;
	private AssetType expectedAsset;
	private BigDecimal expectedAssetUnitPrice;
	private BigDecimal assetTotalAmountPrice;
	private Pair pair;
	private Type type;
	transient private DateTimeFormatter sdf;
	private LocalDateTime operationDate;
	private TransactionFee transactionFee; 
	private Status status;

	public Order(TraderType trader, AssetType offeredAsset, Double offeredAmount, AssetType expectedAsset,
			Double expectedAssetUnitPrice, Pair pair, Type type, TransactionFee transactionFee) {

		if (trader == null || offeredAsset == null || offeredAmount == null || expectedAsset == null
				|| expectedAssetUnitPrice == null || pair == null || type == null) {
			throw new RuntimeException("ERROR: unable to create an invalid " + "order with fields {trader=" + trader
					+ ", offeredAmount=" + offeredAmount + ", expectedAsset=" + expectedAsset
					+ ", expectedAssetUnitPrice=" + expectedAssetUnitPrice + ", pair=" + pair + ", type=" + type + "}");
		}

		ID = UUID.randomUUID().toString();
		traderID = trader.getID();
		this.offeredAsset = offeredAsset;
		this.expectedAssetUnitPrice = formatNumber(expectedAssetUnitPrice);
		this.expectedAsset = expectedAsset;
		
		if (type.equals(Order.Type.SELL) ) {
			this.offeredAmount = formatNumber(offeredAmount);
			this.assetTotalAmountPrice = formatNumber(this.offeredAmount.multiply(this.expectedAssetUnitPrice));
		} else {
			this.offeredAmount = formatNumber(formatNumber(offeredAmount).multiply(this.expectedAssetUnitPrice));
			this.assetTotalAmountPrice = formatNumber(offeredAmount);
		}
		
		this.pair = pair;
		this.type = type;
		sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		operationDate = LocalDateTime.now();
		this.transactionFee = transactionFee;
		this.status = Status.CREATED;
	}

	public Order(Order order, Type orderType, Order matchedOrder, Status status) {
		this.ID = order.ID;
		this.traderID = order.traderID;
		this.offeredAsset = order.offeredAsset;
		this.expectedAssetUnitPrice = order.expectedAssetUnitPrice;
		this.expectedAsset = order.expectedAsset;
		this.offeredAmount = order.offeredAmount;
		this.pair = order.pair;
		this.sdf = order.sdf;
		this.operationDate = order.operationDate; 
		this.transactionFee = order.transactionFee;
		
		if (orderType.equals(Type.SELL)) {
			this.assetTotalAmountPrice = formatNumber(order.assetTotalAmountPrice.subtract(matchedOrder.getOfferedAmount()));
			this.offeredAmount = formatNumber(this.assetTotalAmountPrice.divide(order.expectedAssetUnitPrice));
		} else {
			this.assetTotalAmountPrice = order.assetTotalAmountPrice;
			this.offeredAmount = order.offeredAmount;			
		}
		
		this.status = status;
		this.type = orderType;
	}

	public String getID() {
		return ID;
	}

	public String getTraderID() {
		return traderID;
	}

	public AssetType getOfferedAsset() {
		return offeredAsset;
	}

	public BigDecimal getOfferedAmount() {
		return formatNumber(offeredAmount);
	}

	public AssetType getExpectedAsset() {
		return expectedAsset;
	}

	public BigDecimal getExpectedAssetUnitPrice() {
		return formatNumber(expectedAssetUnitPrice);
	}

	public BigDecimal getAssetTotalAmountPrice() {
		return formatNumber(assetTotalAmountPrice);
	}

	public Pair getPair() {
		return pair;
	}

	public Type getType() {
		return type;
	}

	public LocalDateTime getOperationDate() {
		return operationDate;
	}
	
	public TransactionFee getTransactionFee() {
		return transactionFee;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((assetTotalAmountPrice == null) ? 0 : assetTotalAmountPrice.hashCode());
		result = prime * result + ((expectedAsset == null) ? 0 : expectedAsset.hashCode());
		result = prime * result + ((expectedAssetUnitPrice == null) ? 0 : expectedAssetUnitPrice.hashCode());
		result = prime * result + ((offeredAmount == null) ? 0 : offeredAmount.hashCode());
		result = prime * result + ((offeredAsset == null) ? 0 : offeredAsset.hashCode());
		result = prime * result + ((operationDate == null) ? 0 : operationDate.hashCode());
		result = prime * result + ((pair == null) ? 0 : pair.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((traderID == null) ? 0 : traderID.hashCode());
		result = prime * result + ((transactionFee == null) ? 0 : transactionFee.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (assetTotalAmountPrice == null) {
			if (other.assetTotalAmountPrice != null)
				return false;
		} else if (!assetTotalAmountPrice.equals(other.assetTotalAmountPrice))
			return false;
		if (expectedAsset == null) {
			if (other.expectedAsset != null)
				return false;
		} else if (!expectedAsset.equals(other.expectedAsset))
			return false;
		if (expectedAssetUnitPrice == null) {
			if (other.expectedAssetUnitPrice != null)
				return false;
		} else if (!expectedAssetUnitPrice.equals(other.expectedAssetUnitPrice))
			return false;
		if (offeredAmount == null) {
			if (other.offeredAmount != null)
				return false;
		} else if (!offeredAmount.equals(other.offeredAmount))
			return false;
		if (offeredAsset == null) {
			if (other.offeredAsset != null)
				return false;
		} else if (!offeredAsset.equals(other.offeredAsset))
			return false;
		if (operationDate == null) {
			if (other.operationDate != null)
				return false;
		} else if (!operationDate.equals(other.operationDate))
			return false;
		if (pair == null) {
			if (other.pair != null)
				return false;
		} else if (!pair.equals(other.pair))
			return false;
		if (status != other.status)
			return false;
		if (traderID == null) {
			if (other.traderID != null)
				return false;
		} else if (!traderID.equals(other.traderID))
			return false;
		if (transactionFee == null) {
			if (other.transactionFee != null)
				return false;
		} else if (!transactionFee.equals(other.transactionFee))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public int compareTo(Order other) {
		int result = expectedAssetUnitPrice.compareTo(other.expectedAssetUnitPrice);

		return (result != 0) ? result : operationDate.compareTo(other.operationDate);
	}
	
	public Status getStatus() {
		return status;
	}

	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("ID", ID);
		json.put("traderID", traderID);
		json.put("offeredAsset", offeredAsset);
		json.put("offeredAmount", getOfferedAmount().toPlainString());
		json.put("expectedAsset", getExpectedAsset());
		json.put("expectedAssetUnitPrice", getExpectedAssetUnitPrice().toPlainString());
		json.put("assetTotalAmountPrice", getAssetTotalAmountPrice().toPlainString());
		json.put("pair", pair);
		json.put("type", type);
		json.put("operationDate", sdf.format(operationDate));
		json.put("transactionFee", transactionFee);
		json.put("status", status);		
		return getGson().toJson(json);
	}

	public enum Type {
		BUY, SELL;
	}
	
	public enum Status {
		CREATED, PARTIAL, FILLED, CANCELED
	}
}
