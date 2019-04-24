Para rodar o Cliente e Servidor da pasta Executaveis, é preciso seguir os seguintes passos:

# 1 Rodar no terminal o comando
 tnameserv

# 2 De dentro da pasta Executaveis, rodar o servidor
java --add-modules java.corba -jar Server.jar -ORBInitialHost localhost Repositorio1

# 3 Rodar o cliente
java --add-modules java.corba -jar Client.jar