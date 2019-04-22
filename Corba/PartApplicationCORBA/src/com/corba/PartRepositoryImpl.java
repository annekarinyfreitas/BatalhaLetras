package com.corba;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.ORB;
import PPDCorba.*;

public class PartRepositoryImpl extends PartRepositoryPOA {
	List <PartInfo> listPartInfo = new ArrayList <PartInfo>();
	int currentCode = 0;

	@Override
	public String name() {
		return "";
	}

	@Override
	public int numParts() {
		return listPartInfo.size();
	}

	@Override
	public Part addPart(String name, String description) {
		System.out.println("Entrou no Add part");
		
		try {
			// Part
			PartLocal partLocal = new PartLocal(Integer.toString(currentCode), name, description);
			System.out.println("Inicializou um PartLocal");
			
			PartInfo partInfo = new PartInfo(partLocal, Integer.toString(currentCode), name, description);
			System.out.println("Inicializou um PartInfo");
			
			listPartInfo.add(partInfo);
			System.out.println("Adicionou no array um PartInfo");
			
			currentCode += 1;

			return (Part) partLocal;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Part getPart(String code) throws NoSuchPart {
		for (PartInfo info: listPartInfo) {
			if (info.code == code) {
				return info.part;
			}
		}
		
		return null;
	}

	@Override
	public PartInfo getPartInfo(String code) throws NoSuchPart {
		for (PartInfo info: listPartInfo) {
			if (info.code == code) {
				return info;
			}
		}
		return null;
	}

	@Override
	public PartListItem[] getPartList() {
		List <PartListItem> listItems = new ArrayList<PartListItem>();
		PartListItem[] items = new PartListItem[listPartInfo.size()];
		
		for (PartInfo info: listPartInfo) {
			PartListItem item = new PartListItem(info.code, info.name);
			listItems.add(item);
		}
		
		return listItems.toArray(items);
	}

}
