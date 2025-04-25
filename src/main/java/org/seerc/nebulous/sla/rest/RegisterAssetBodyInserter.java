package org.seerc.nebulous.sla.rest;

public class RegisterAssetBodyInserter {

	String assetName;
	long timestamp;
	
	
	public RegisterAssetBodyInserter(String assetName, long timestamp) {
		this.assetName = assetName;
		this.timestamp = timestamp;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
