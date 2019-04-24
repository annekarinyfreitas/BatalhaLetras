package com.corba;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

import PPDCorba.NoSuchPart;
import PPDCorba.Part;
import PPDCorba.PartInfo;
import PPDCorba.PartListItem;
import PPDCorba.PartRepository;
import PPDCorba.PartRepositoryHelper;

public class Client {
	PartRepository currentPartRepository;

	public static void main(String args[]) {
		Client c = new Client();

		try {
			while (true) {
				System.out.println("*********       Terminal do Cliente      ***************");
				System.out.println("bind - Faz o cliente conectar-se ao servidor e pega a referência do PartRepository (Ex.: bind Rep1)");
				System.out.println("inforep - Lista informações do repositório (nome e num de pecas) (Ex.: inforep)");
				System.out.println("listp - Lista as peças do repositório (Ex.: listp)");
				System.out.println("getp - Busca a peça por código (Ex.: getp 0)");
				System.out.println("showp - Mostra os atributos da peça (Ex.: showp 0)");
				System.out.println("addp - Adiciona uma peça ao repositório corrente (Ex.: addp A Descricao da peca A)");
				System.out.println("quit - Encerrar");
				System.out.println("Digite um comando: ");

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String s = br.readLine();

				List<String> splited = Arrays.asList(s.split(" "));

				switch (splited.get(0)) {

				// Conectar a outro servidor
				case "bind":
					if (splited.size() > 1) {
						c.connectToServer(args, splited.get(1));
					} else {
						System.out.println("Comando sem os argumentos necessários!");
					}

					break;

					// Listar as peças do repositório
				case "listp": 
					c.listRepository();
					break;

					// Buscar peça	
				case "getp":
					if (splited.size() > 1) {
						c.getPartByCode(splited.get(1));
					} else {
						System.out.println("Comando sem os argumentos necessários!");
					}

					break;

					// Mostrar atributos da peça	
				case "showp":
					if (splited.size() > 1) {
						c.showPartInfoByCode(splited.get(1));
					} else {
						System.out.println("Comando sem os argumentos necessários!");
					}

					break;

					// Adicionar peça
				case "addp":
					if (splited.size() > 2) {

						// Nome
						String name = splited.get(1);

						//Descricao
						String description = String.join(" ", splited.subList(2, splited.size()));

						c.addPartToRepository(name, description);
					} else {
						System.out.println("Comando sem os argumentos necessários!");
					}

					break;

				case "inforep":
					c.printInfoFromRepository();
					break;

				case "quit":
					System.exit(0);
					break;

				default:
					System.out.println("Essa opcao não existe!");
					break;
				}

				System.out.println("\n");
			}

		} catch (Exception e) {
			System.out.println("Erro no Menu do Cliente : " + e);
			e.printStackTrace(System.out);
		}
	}

	// CONEXAO COM SERVIDOR
	private void connectToServer(String args[], String serverName) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");

			NamingContext naming = NamingContextHelper.narrow(obj);
			NameComponent[] name = { new NameComponent(serverName, "") };

			org.omg.CORBA.Object objRef = naming.resolve(name);
			currentPartRepository = PartRepositoryHelper.narrow(objRef);

			System.out.println("Repositório atual: "+ currentPartRepository.name() + "\n");

		} catch (Exception e) {
			System.out.println("Servidor com o nome " + serverName + " não encontrado!");
		}
	}

	// LISTA AS PECAS NO REPOSITORIO
	private void listRepository() {
		if (currentPartRepository == null) {
			System.out.println("O cliente não está conectado a nenhum repositório. Use bind ServerName");
		} else {
			PartListItem[] items = currentPartRepository.getPartList();

			System.out.println("Peças do Repositório: ");
			for (PartListItem item: items) {
				System.out.println(item.code + " " + item.name);
			}
		}
	}

	// ADICIONAR PECA
	private void addPartToRepository(String name, String description) {
		if (currentPartRepository == null) {
			System.out.println("O cliente não está conectado a nenhum repositório. Use bind ServerName");
		} else { 
			Part part = currentPartRepository.addPart(name, description);
			System.out.println("Peça "+ part.name() + " adicionada!");
		}
	}

	// BUSCAR PECA PELO CODIGO
	private void getPartByCode(String code) {
		if (currentPartRepository == null) { 
			System.out.println("O cliente não está conectado a nenhum repositório. Use bind ServerName");
		} else {
			try {
				Part part = currentPartRepository.getPart(code);
				System.out.println("Peça encontrada: " + part.code() + " " + part.name());

			} catch (NoSuchPart e) {
				System.out.println("A peça com o codigo " + e.partCode + " não existe no repositório!");
			}
		}
	}

	// MOSTRAR ATRIBUTOS PECA DE PELO CODIGO
	private void showPartInfoByCode(String code) {
		if (currentPartRepository == null) { 
			System.out.println("O cliente não está conectado a nenhum repositório. Use bind ServerName");
		} else { 
			try {
				PartInfo info = currentPartRepository.getPartInfo(code);

				System.out.println("Codigo: "+ info.code);
				System.out.println("Nome: "+ info.name);
				System.out.println("Descricao: "+ info.description);

			} catch (NoSuchPart e) {
				System.out.println("A peça com o codigo " + code + " não existe no repositório!");
			}
		}

	}

	// MOSTRA ATRIBUTOS DO REPOSITORIO
	private void printInfoFromRepository() {
		if (currentPartRepository == null) {
			System.out.println("O cliente não está conectado a nenhum repositório. Use bind ServerName");
		} else {
			System.out.println("Nome: " + currentPartRepository.name() + " Número de peças: "+ currentPartRepository.numParts());
		}
	}
}
