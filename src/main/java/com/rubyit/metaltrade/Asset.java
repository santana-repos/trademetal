package com.rubyit.metaltrade;

import static com.rubyit.metaltrade.Utils.getGson;
import static com.rubyit.metaltrade.Utils.formatNumber;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.rubyit.metaltrade.obj.AssetType;

public class Asset {
	
	private AssetType asset;
	private BigDecimal balance;
	private BigDecimal blockedBalance;
	transient private Lock balanceChangeLock;
	transient private Condition sufficientFundsCondition;
	
	public Asset(AssetType asset) {
		this.asset = asset;
		balance = BigDecimal.ZERO;
		blockedBalance = BigDecimal.ZERO;
		balanceChangeLock = new ReentrantLock();
		sufficientFundsCondition = balanceChangeLock.newCondition();
	}
	void deposit(BigDecimal amount) {
		balanceChangeLock.lock();
		try {
			System.out.print("Depositing " + amount);
			BigDecimal newBalance = balance.add(amount);
			System.out.println(", new balance is " + newBalance);
			balance = newBalance;
			sufficientFundsCondition.signalAll();
		} finally {
			balanceChangeLock.unlock();
		}
	}
	void withdraw(BigDecimal amount) throws InterruptedException {
		balanceChangeLock.lock();
		try {
			while (balance.compareTo(amount) < 0) {
				sufficientFundsCondition.await();
			}
			System.out.print("Withdrawing " + amount);
			BigDecimal newBalance = balance.subtract(amount);;
			System.out.println(", new balance is " + newBalance);
			balance = newBalance;
		} finally {
			balanceChangeLock.unlock();
		}
	}
	public BigDecimal getBalance() {
		return formatNumber(balance);
	}
	public BigDecimal getBlockedBalance() {
		return formatNumber(blockedBalance);
	}
	public AssetType getAsset() {
		return asset;
	}
	
	public void blockBalance(final BigDecimal amountToBlock) {
		blockedBalance = blockedBalance.add(amountToBlock); 
	}
	
	public void unblockBalance(final BigDecimal amountToUnblock) {
		blockedBalance = blockedBalance.subtract(amountToUnblock); 
	}
	
	@Override
	public String toString() {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("assetType", asset);
		json.put("balance", getBalance().toPlainString());
		json.put("blockedBalance", getBlockedBalance().toPlainString());
		return getGson().toJson(json);
	}
}
