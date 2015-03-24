NxtCore
=======

NxtCore provides support for accessing the Nxt API.  It communicates with a Nxt node using an HTTP/HTTPS connection to the API port.  Your secret phrase is never sent to the API server, so it is safe to use with a remote node.

The SLF4J logging facility (slf4j-api) is used to log messages.  The application is responsible for providing and configuring the appropriate logger implementation (for example, slf4j-jdk).

The NxtMint, NxtMonitor and NxtWallet projects provide examples of how to configure and use the NxtCore library.


Build
=====

I use the Netbeans IDE but any build environment with Maven and the Java compiler available should work.  The documentation is generated from the source code using javadoc.

Here are the steps for a manual build.  You will need to install Maven 3 and Java SE Development Kit 8 if you don't already have them.

  - Create the executable: mvn clean install
  - [Optional] Create the documentation: mvn javadoc:javadoc
  - [Optional] Copy the .jar files from the target directory to wherever you want to store the executables.

  
Tutorial
========

Refer to the NxtCore Javadoc for a detailed description of the classes and methods.  The NxtMint, NxtMonitor and NxtWallet projects provide examples of how to use the various API methods.  The following are some common usages.    

Before any NxtCore function can be used, the library must be initialized.  The Nxt.init() method sets the NRS host name/address (usually "localhost") and the server port (usually 7876).

    Nxt.init(String serverHost, int serverPort)

To send Nxt, you need to create and broadcast a payment transaction.  NxtCore will create and sign the transaction locally and then send it to the NRS node for broadcast to the network.

    long txId = Nxt.sendNxt(recipientId, amount, fee, deadline, null, secretPhrase);
    
To send a message, you need to create and broadcast a messaging transaction.  The message is an arbitrary byte array provided.  NxtCore does not inspect or modify the contents of the message.

    long txId = Nxt.sendMessage(recipientId, message, fee, deadline, null, secretPhrase);

To assign an alias, you need to create and broadcast a messaging transaction.  The alias name must consist of letters and numbers with no embedded spaces (leading and trailing spaces will be removed).  The alias URI is an arbitrary string.  NxtCore does not inspect or modify the URI.

    long txId = Nxt.assignAlias(aliasName, aliasUri, fee, deadline, null, secretPhrase);

To set the account information, you need to create and broadcast a messaging transaction.

    long txId = Nxt.setAccountInfo(accountName, accountDescription, fee, deadline, null, secretPhrase);

