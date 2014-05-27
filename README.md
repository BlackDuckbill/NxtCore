NxtCore
=======

NxtCore provides support for accessing the Nxt API.  It communicates with a Nxt node using an HTTP connection to the API port.  Your secret phrase is never sent to the API server, so it is safe to use with a remote node.


Build
=====

I use the Netbeans IDE but any build environment with Maven and the Java compiler available should work.  The documentation is generated from the source code using javadoc.

Here are the steps for a manual build.  You will need to install Maven 3 and Java SE Development Kit 7 if you don't already have them.

  - Create the executable: mvn clean install
  - [Optional] Create the documentation: mvn javadoc:javadoc
  - [Optional] Copy target/NxtCore-v.r.jar to wherever you want to store the executable.

  
Tutorial
========

Refer to the NxtCore Javadoc for a detailed description of the classes and methods.

Before any NxtCore function can be used, the library must be initialized.  The Nxt.init() method sets the NRS host name/address (usually "localhost") and the server port (usually 7876).

    Nxt.init(String serverHost, int serverPort)

To send Nxt, you need to create and broadcast a payment transaction.  NxtCore will create and sign the transaction locally and then send it to the NRS node for broadcast to the network.

    TransactionType txType = TransactionType.Payment.ORDINARY;
    Transaction tx = new Transaction(txType, sendAddress, sendAmount, sendFee, deadline, null, null, secretPhrase);
    long txId = Nxt.broadcastTransaction(tx);
    
To send a message, you need to create and broadcast a messaging transaction.  The message is an arbitrary byte array provided by the application.  NxtCore does not inspect or modify the contents of the message.

    TransactionType txType = TransactionType.Messaging.ARBITRARY_MESSAGE;
    Attachment attachment = new ArbitraryMessage(messageBytes);
    Transaction tx = new Transaction(txType, sendAddress, 0, Nxt.MINIMUM_TX_FEE, deadline, null, attachment, secretPhrase);
    long txId = Nxt.broadcastTransaction(tx);

To assign an alias, you need to create and broadcast a messaging transaction.  The alias name must consist of letters and numbers with no embedded spaces (leading and trailing spaces will be removed).  The alias URI is an arbitrary string provided by the application.  NxtCore does not inspect or modify the URI.

    TransactionType txType = TransactionType.Messaging.ALIAS_ASSIGNMENT;
    Attachment attachment = new AliasAssignment(aliasName, aliasURI);
    Transaction tx = new Transaction(txType, Nxt.GENESIS_ACCOUNT_ID, 0, Nxt.MINIMUM_TX_FEE, deadline, null, attachment, secretPhrase);
    long txId = Nxt.broadcastTransaction(tx);

To set the account information, you need to create and broadcast a messaging transaction.

    TransactionType txType = TransactionType.Messaging.ACCOUNT_INFO;
    Attachment attachment = new AccountInfo(accountName, accountDescription);
    Transaction tx = new Transaction(txType, Nxt.GENESIS_ACCOUNT_ID, 0, Nxt.MINIMUM_TX_FEE, deadline, null, attachment, secretPhrase);
    long txId = Nxt.broadcastTransaction(tx);

