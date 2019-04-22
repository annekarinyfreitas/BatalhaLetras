package com.corba;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

import PPDCorba.Part;
import PPDCorba.PartListItem;
import PPDCorba.PartRepository;
import PPDCorba.PartRepositoryHelper;

public class Client {
	PartRepository currentPartRepository;
	
	public static void main(String args[]) {
		ORB orb = ORB.init(args, null);
		Client c = new Client();
		
		try {
			while (true) {
				System.out.println("Terminal do Cliente");
				System.out.println("bind - Faz o cliente conectar-se ao servidor e pega a referência do PartRepository");
				System.out.println("listp - Lista as peças do repositório");
				System.out.println("getp - Busca a peça por código");
				System.out.println("showp - Mostra os atributos da peça");
				System.out.println("addp - Adiciona uma peça ao repositório corrente");
				System.out.println("quit - Encerrar");
				System.out.println("Digite um comando: ");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String s = br.readLine();
				String[] splited = s.split(" ");
				
				switch (splited[0]) {
				
				// Conectar a outro servidor
				case "bind":
					c.connectToServer(orb, splited[1]);
					break;
				
				// Listar as peças do repositório
				case "listp": 
					c.listRepository();
					break;
					
				// Buscar peça	
				case "getp":
					break;
				
				// Mostrar atributos da peça	
				case "showp":
					break;
				
				// Adicionar peça
				case "addp":
					c.addPartToRepository(splited[1], splited[2]);
					break;
					
				default:
					System.exit(0);
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("Outro Erro : " + e);
			e.printStackTrace(System.out);
		}
	}
	
	// CONEXAO COM SERVIDOR
	private void connectToServer(ORB orb, String serverName) {
		try {
			org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");

			NamingContext naming = NamingContextHelper.narrow(obj);
			NameComponent[] name = { new NameComponent(serverName, "") };

			org.omg.CORBA.Object objRef = naming.resolve(name);
			currentPartRepository = PartRepositoryHelper.narrow(objRef);
			
			System.out.println("Repositório atual: "+ currentPartRepository.name() + "\n\n");
			
		} catch (Exception e) {
			System.out.println("Outro Erro : " + e);
			e.printStackTrace(System.out);
		}
	}
	
	// LISTA AS PECAS NO REPOSITORIO
	private void listRepository() {
		PartListItem[] items = currentPartRepository.getPartList();
		
		System.out.println("Peças do Repositório: ");
		for (PartListItem item: items) {
			System.out.println(item.code + " " + item.name);
		}
	}
	
	// ADICIONAR PECA
	private void addPartToRepository(String name, String description) {
		currentPartRepository.addPart(name, description);
		//System.out.println("Peça "+ part.name() + "adicionada!"); //-> Retorna null
	}
}
