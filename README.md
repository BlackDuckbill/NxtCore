NxtCore
=======

NxtCore provides support for accessing the Nxt API.  It communicates with a Nxt node using an HTTP connection to the API port.  Your secret phrase is never sent to the API server, so it is safe to use with a remote node.

The API server is polled every 2 minutes to update the account/transaction status.

A compiled version of NxtCore is available here: https://drive.google.com/folderview?id=0B1312_6UqRHPYjUtbU1hdW9VMW8&usp=sharing.  Download the desired archive file and extract the files to a directory of your choice.  If you are building from the source, the dependent jar files can also be obtained here.  The files are signed with the GPG key for Ronald.Hoffman6@gmail.com (D6190F05).


Build
=====

I use the Netbeans IDE but any build environment with Maven and the Java compiler available should work.  The documentation is generated from the source code using javadoc.

Here are the steps for a manual build.  You will need to install Maven 3 and Java SE Development Kit 7 if you don't already have them.

  - Create the executable: mvn clean install
  - [Optional] Create the documentation: mvn javadoc:javadoc
  - [Optional] Copy target/NxtCore-v.r.jar to wherever you want to store the executable.
