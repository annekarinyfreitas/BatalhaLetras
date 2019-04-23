package com.corba;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.omg.PortableServer.POA;
import PPDCorba.*;

public class PartRepositoryImpl extends PartRepositoryPOA {
	private String serverName = null;
	private Hashtable<String, PartImpl> parts = new Hashtable<String, PartImpl>();
	private Part part = null;
	private POA poaRef = null;
	private int currentCode = 0;

	// Construtor do repositorio que recebe o nome e o POA
	public PartRepositoryImpl(String name, POA poaRef) {
		super();    
		try {    
			serverName = name; 
			this.poaRef = poaRef;    
		}catch(Exception e) {
			System.out.println("Erro ao inicializar PartRepository");
			e.printStackTrace();
		}
	}

	@Override
	public String name() {
		return serverName;
	}

	@Override
	public int numParts() {
		return parts.size();
	}

	@Override
	public Part addPart(String name, String description) {
		try {
			// Codigo
			String codeStr = Integer.toString(currentCode);
			currentCode += 1;

			// Inicializa um partImpl
			PartImpl partImpl = (PartImpl) parts.get(codeStr);		
			partImpl = new PartImpl(codeStr, name, description);
			
			// Pega a referencia do objeto Part
			org.omg.CORBA.Object partRef = poaRef.servant_to_reference(partImpl);	
			part = PartHelper.narrow(partRef);

			// Salva no dicionario
			parts.put(codeStr, partImpl);

			return part;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Part getPart(String code) throws NoSuchPart {
		try {
			PartImpl partImpl = (PartImpl) parts.get(code);
			
			// Pega a referencia do objeto Part
			org.omg.CORBA.Object partRef = poaRef.servant_to_reference(partImpl);
			part = PartHelper.narrow(partRef);
			
			return part;
			
		} catch (Exception e) {
			System.out.println("Peca nao encontrada no repositorio");
			throw new NoSuchPart(code);
		}
	}

	@Override
	public PartInfo getPartInfo(String code) throws NoSuchPart {
		try {
			part = getPart(code);
			PartInfo partInfo = new PartInfo(part, part.code(), part.name(), part.description());
			
			return partInfo;
			
		} catch (Exception e) {
			System.out.println("Peca nao encontrada no repositorio");
			throw new NoSuchPart(code);
		}
	}

	@Override
	public PartListItem[] getPartList() {
		List <PartImpl> partImpls = new ArrayList<>();
		partImpls.addAll(parts.values());
		
		List <PartListItem> listItems = new ArrayList<PartListItem>();
		PartListItem[] items = new PartListItem[partImpls.size()];

		for (PartImpl part: partImpls) {
			PartListItem item = new PartListItem(part.code, part.name);
			listItems.add(item);
		}

		return listItems.toArray(items);
	}

}
