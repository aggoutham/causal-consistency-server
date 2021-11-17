Causal Consistency Implementation
github :- https://github.com/aggoutham/causal-consistency-server



Steps to instantiate the environment :- 

1. Clone the git repository “causal-consistency-server” or unzip the source code file “causal-consistency-server.zip”.
2. In the repo's directory run “mvn clean install” to create a new  “target” directory. (Maven is used as a build tool here to automate the dependencies and source packaging)
3. The jar “causal-consistency-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar” within target folder is the executable that will now be used to instantiate the entire system.
4. We need multiple hosts to simulate multiple Data Centers and Clients. For ex :- Lets assume we need 3 Client servers and 3 Data Center servers. 
5. Copy “causal-consistency-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar” to all the hosts that needs to be part of the system [Data center (or) client]. Create a file “config.properties” in the same directory as the executable. More details on what configurations to be used are explained below in detail.
6. To start a DATA CENTER, run the following command - java -cp causal-consistency-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar server.StartServer ./config.properties 1
7. To start a CLIENT SERVER run the following command - java -cp causal-consistency-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar client.StartClient ./config.properties 1

Now the system is initiated with required number of Data Centers and Client servers.

Configuration File :-
Following is the explanation to each parameter in the config.properties file -

1. authToken - A sample plain text password to authenticate every communication in the system. In real-world distributed system, this shall be more sophisticated.

The following configs are relevant only if it is the Data Center server :-

2. serverIP - The data center server’s public IP address that is required to identify the host.
3. serverPort - The port that needs to be used to bring up the server’s process for accepting socket connections.
4. alldcs - Comma separated list of all data centers that are present in the distributed system including the current server itself. Each DC is represented with its IP address and port. So, this configuration will be of the form : “IP1:Port1,IP2:Port2,IP3:Port3,…..”
5. respectiveDelays - Comma separated list of times (in seconds) that simulate the delay between “this” DC server and the corresponding DC server in the previous configuration alldcs.


The following configs are relevant only if it is the Client server :-

6. clientId - The current client’s ID. Any string can be given here (has to be unique from other client ids).
7. connectedDC - The data center that this client needs to stay connected to. The format would be “IP:Port”. This client will always be connected to that particular DC server.
