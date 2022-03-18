# Varane-Hazelcast

A java application demonstrating use of hazelcast caching in client-server mode.
Read about it here: https://docs.hazelcast.com/imdg/4.2/overview/topology


- This application uses embedded H2 database, and adds filler data on app startup, so you can just clone and run the app straight away.
    ```
    $ ./gradlew bootRun
    ```
- Start a hazelcast server in the local machine, the application on start-up will detect and connect to the cluster automatically.


**How to start a hazelcast cluster ?**
- You can start one through command line, if your machine is compatible. (https://docs.hazelcast.com/imdg/4.2/getting-started)
- Or you can run a docker image directly. Make sure you enable user code deployment in hazelcast config file. (https://docs.hazelcast.com/imdg/4.2/clusters/deploying-code-from-clients) 
  ```
  $ docker network create hazelcast-network
  $ docker run \
    -it \
    --network hazelcast-network \
    --rm \
    -v <local-path-to hazelcast.yaml>:/opt/hazelcast/hazelcast.yml \
    -e HZ_CLUSTERNAME=dev \
    -e HAZELCAST_CONFIG=hazelcast.yml \
    -p 5701:5701 hazelcast/hazelcast:5.0.2
  ```
  For more cluster members, run again with different exposed port number. They all be brothers, detecting each other's presence (and absence too - when they are killed) automatically.
- Or, there are many other ways.


**Regarding the application**
- The APIs in controller will help understand and demonstrate caching.
- Note that this app uses in-memory H2 database, so we cannot really distinguish between hazelcast cache and h2 db calls. So, to demonstrate the difference every H2 db call has a delay of extra 1 second added to it.
- The controllers demonstrate using hazelcast data structure for adding/querying, create mapping between models and data structure values, SQL predicate, pure SQL usage for querying, and distributed lock.


Note: About 1000 entries are added to db student table on startup. 

Point of contact: Sreeram Maram


# Note 

Hi, this project has a branche name "hazelcast".

Checkout `hazelcast` branch to demonstrate yourself how Hazelcast in **embedded** mode works.