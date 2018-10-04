package com.rubyit.metaltrade.obj;

import static com.rubyit.metaltrade.Utils.getGson;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class AssetType {
	private String ID;
	private String name;

	public AssetType(String name) {
		if (name == null || name.trim().equals("")) {
			throw new RuntimeException("ERROR: Cannot create a asset with empty name");
		}

		ID = UUID.randomUUID().toString();
		this.name = name.toUpperCase();
	}

	public String getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		Map<String, Object> json = new TreeMap<String, Object>();
		json.put("ID", ID);
		json.put("name", name);
		return getGson().toJson(json);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AssetType other = (AssetType) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
