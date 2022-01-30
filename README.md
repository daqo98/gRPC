# gRPC


- To generate a package (.jar) and then copy it to the local Maven repository: ```mvn clean install```
- To create executable .jar with dependencies: ```mvn clean compile assembly:single```
- To execute the .jar with dependencies: ```java -jar target/user-service-1.0-SNAPSHOT-jar-with-dependencies.jar```
