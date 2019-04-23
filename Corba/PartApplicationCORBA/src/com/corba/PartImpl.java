package com.corba;
import PPDCorba.*;

public class PartImpl extends PartPOA {
	String code;
	String name;
	String description;
	
	public PartImpl(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String description() {
		return description;
	}
}
