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
