package com.corba;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class Server {
	public static void main(String args[]) {
		try {
			ORB orb = ORB.init(args, null);

			Object objPoa = orb.resolve_initial_references("RootPOA");
			POA rootPOA = POAHelper.narrow(objPoa);

			String serverName = args[2];

			Object obj = orb.resolve_initial_references("NameService");
			NamingContext naming = NamingContextHelper.narrow(obj);

			PartRepositoryImpl partRepository = new PartRepositoryImpl(serverName, rootPOA);
			Object objRef = rootPOA.servant_to_reference(partRepository);

			NameComponent[] name = { new NameComponent(serverName, "") };
			naming.rebind(name, objRef);
			rootPOA.the_POAManager().activate();

			System.out.println("Servidor "+ serverName +" Pronto ...");
			orb.run();

		} catch (Exception ex) {
			System.out.println("Erro");
			ex.printStackTrace();
		}
	}
}
