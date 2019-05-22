package com.space;

import java.util.ArrayList;

import net.jini.core.entry.Entry;

public class Item implements Entry {
	public int id;
	public String description;
	public String author;
	public ArrayList <Offer> offers;
	
	public Item() {
	}
}
