package com.rubyit.metaltrade.orderbook;

import static com.rubyit.metaltrade.Utils.getGson;
import static com.rubyit.metaltrade.Utils.formatNumber;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rubyit.metaltrade.obj.AssetType;

public class TransactionFee {

	private BigDecimal transactionFeeValue;
	private AssetType transactionFeeAssetType;
	
	
	public TransactionFee(Double transactionFeeValue, AssetType transactionFeeAssetType) {
		this.transactionFeeValue = formatNumber(transactionFeeValue);
		this.transactionFeeAssetType = transactionFeeAssetType;
	}

	public BigDecimal getTransactionFeeValue() {
		return formatNumber(transactionFeeValue);
	}


	public void setTransactionFeeValue(BigDecimal transactionFeeValue) {
		this.transactionFeeValue = transactionFeeValue;
	}


	public AssetType getTransactionFeeAssetType() {
		return transactionFeeAssetType;
	}


	public void setTransactionFeeAssetType(AssetType transactionFeeAssetType) {
		this.transactionFeeAssetType = transactionFeeAssetType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transactionFeeAssetType == null) ? 0 : transactionFeeAssetType.hashCode());
		result = prime * result + ((transactionFeeValue == null) ? 0 : transactionFeeValue.hashCode());
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
		TransactionFee other = (TransactionFee) obj;
		if (transactionFeeAssetType == null) {
			if (other.transactionFeeAssetType != null)
				return false;
		} else if (!transactionFeeAssetType.equals(other.transactionFeeAssetType))
			return false;
		if (transactionFeeValue == null) {
			if (other.transactionFeeValue != null)
				return false;
		} else if (!transactionFeeValue.equals(other.transactionFeeValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("transactionFeeAssetType", transactionFeeAssetType);
		json.put("transactionFeeValue", getTransactionFeeValue().toPlainString());
		return getGson().toJson(json);
	}
}
