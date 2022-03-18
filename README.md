# Varane-Hazelcast

A java application demonstrating use of hazelcast caching in embedded mode.
Read about it here: https://docs.hazelcast.com/imdg/4.2/overview/topology


- Uses embedded H2 database, and adds filler data on app startup, so you can just clone and run the app straight away.
    ```
    ./gradlew bootRun
    ```

- To observe hazelcast member cluster in case of multi-instance setup, build jar
  `./gradlew bootJar` and run multiple instances.
    ```
    java -jar <jar_file.jar> --server.port=8081
    java -jar <jar_file.jar> --server.port=8082
    java -jar <jar_file.jar> --server.port=8083
    ```

- Kill/start new instances to observe changes in hazelcast cluster members as well in logs.


**Regarding the application**
- The APIs in controller will help understand and demonstrate caching.
- Note that this app uses in-memory H2 database, so we cannot really distinguish between hazelcast cache and h2 db calls. So, to demonstrate the difference every H2 db call has a delay of extra 1 second added to it.
- The endpoints in Controller demo make use of hazelcast data structures, primarily IMap. SQL querying of Imap is shown in one if that interests you.

**Further**
- Docker compose is added. Generate .war before building, docker image is of tomcat.
    ```
    ./gradlew bootWar
    docker-compose build
    docker-compose up
    ```
- To demo hazelcast on k8s deployment, use above generated image here.
    ```
    kubectl create deployment hazelcast-embedded-demo --image=IMAGE:TAG --replicas=3
    ```
- You may add/delete/kill replicas to demo hazelcast cluster(in logs) and how they sync between each other.


**Listing endpoints**
```
GET http://localhost:8080/student/get?id=8
GET http://localhost:8080/student/all-in-cache
GET http://localhost:8080/student/sql-example
POST http://localhost:8080/varane/student/add?id=2&name=wiraram&contact=8919937557
```
- /student/get?id=8 can help understand improvement in response time once the data is cached (after first call)
- Note: About 1000 student entries are added to db student table on startup.

# Note about Hazelcast in embedded mode:
- The cache is stored within application instances, hence no of separate server.
- Downside: Hazelcast cannot scale independent of application.
- Speeds are better than client/server architecture of caching + no additional cost + superfast setup.
- Hazelcast takes care of data coherency across application instances. Distributed caching.

Point of contact: Sreeram Maram