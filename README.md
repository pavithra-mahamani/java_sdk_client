This is a collection aware Java SDK client for Couchbase Server. Use it to populate and manipulate docs to your clusters.
  
Usage:

      1. mvn -f java_sdk_client/collections/pom.xml clean install
      2. java -jar target/javaclient/javaclient.jar
          #Connections params
                      -i <node IP> -u <username> -p <password> -b <bucket> -s <scope> -c <collection>
          #CRUD params
                      -n <num ops> -pc <percent create> -pu <percent update> -pd <percent delete>
                      -l <load pattern> -fu <fields to update> -ac <all collections> -sd <shuffle docs>
          #Doc params
                      -dpx <doc prefix> -dsx <doc suffix> -dsn <doc sequence start> -dt <doc template>
                      -ds <doc op start index> -de <doc op end index> -ln <locale>

      To run inside docker container:

      1. Modify params in java_sdk_client/collections/configure.sh
      2. chmod 777 java_sdk_client/collections/configure.sh
      3. mvn -f java_sdk_client/collections/pom.xml clean install
      4. docker build -t jsc java_sdk_client/collections
      5. Feed runtime params corresponding to configure.sh : docker run -e CLUSTER=<> -e BUCKET=<> ... cb:latest
