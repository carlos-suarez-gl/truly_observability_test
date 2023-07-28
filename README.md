# Observability demo spring-boot

## Overview

This example shows how to integrate Prometheus, Grafana, Jaeger and Sentry into a Java Spring Boot project
integrating with Opentracing, Logback and Spring Actuator.

## First-time Setup
1. To start this demo, the first step is to validate if you have docker and docker-compose installed on your machine. You can validate this one, running the command  ***docker --version*** in one console. In case you need to install both of these applications, you can follow the commands in the below pages to install Docker desktop, this package include Docker Compose, Docker Engine, and Docker CLI:

    -[Windows](https://docs.docker.com/desktop/install/windows-install/)

    -[Linux](https://docs.docker.com/desktop/install/linux-install/)

    -[MacOs](https://docs.docker.com/desktop/install/mac-install/)

2. Sentry can use in different ways, like SaaS or running locally in your machine. For this example, we use Sentry like SaaS, with a free account, for test purposes. In case, that you want to run sentry in your local machine, you can follow the steps to run into your local machine on this page [Sentry in your local environment](https://theappsguy.dev/setting-up-sentry-self-hosted) 
3. Update the Sentry DSN to your project's DSN in application.properties
4. Ensure that `SENTRY_AUTH_TOKEN` is set in your ENV variables
5. In the file "application.yaml" we have the configuration for Jaeger, Actuator, and Prometheus. All the hosts and ports are configured, for a local environment deployable by Docker Compose. 

## Run
1. In the folder scripts/monitoring have the base configuration for Grafana and Prometheus, which we are going to use when runing the environment. If you need to change something, like the configuration for Prometheus, you can review the file prometheus.yaml, which will be copied inside the docker, to run the service. 

2. In the same folder, of the below step, have the file docker-compose.yml, in this one you can preview and configure, all the parameters, that you need to run the local environment for this test. Inside this file, you can appreciate 3 services that will be deployed, in order we have Jaeger all in one, Prometheus and Grafana. 

3. To run docker-compose, go to the path in your machine, ${test_path}/script/monitoring in one cmd or console, and run the command "docker-compose up", at this point, the engine starts downloading the containers, and lunch one by one in our machine. 

4. To visit the UI of each service you can go to:
    - Prometheus --   http://localhost:9090
    - Grafana   --    http://localhost:3000
    - Jaeger    --    http://localhost:16686
    To login in grafana at the firstime the credentials are:
        user: admin
        pass: admin
    If you have any problem of login, you can reset the password enter to the docker using the next script:

        ***docker exec -it ${docker id}  grafana-cli admin reset-admin-password ${new_password}***
        
        To obtain the docker id for Grafana, you can run the command <span style="color: green;">docker ps</span> in one console, and use the <span style="color: green;">container id</span>, like <span style="color: green;">docker id</span>
        Replace **${docker id}** for **container id** number and **${new_password}** for the new **password** that you choise

5. If your environment is ready, you need to validate, that Maven was installed and that the variable, for his working is defined. In case you are going to install Maven for the first time on your machine, you can go to the next pages, and follow the steps, to use Maven on your machine.

    -[Windows](https://phoenixnap.com/kb/install-maven-windows) 

    -[Linux](https://www.digitalocean.com/community/tutorials/install-maven-linux-ubuntu) 

    -[MacOs](https://formulae.brew.sh/formula/maven) 

6. Back to the code, the "pom.xml" is ready, to test and create a Jar file. In this case, you need to go the root path of you project, in this case **/truly_observability_test** to run the below commands, depends of the process that you want to follow:
 - mvn spring-boot:run  -- Run the project in your local machine, directly with your IDE
 - mvn package          -- Package your code into a Jar file. 
    The file is alocated in the folder "target" with the name "example-0.0.1-SNAPSHOT.jar"
    to run the jar file, into a tomcat server you can use the next command:
        - java -jar target/example-0.0.1-SNAPSHOT.jar --spring.application.name=Service-1 --server.port=8080
        
7. The application is ready to use, and you can start to enter to the URL "http://localhost:8080/" and watch the message "This is the root url"

8. To review the different endpoints exposed by Spring Actuator, you can enter "http://localhost:8080/actuator", and this is an expected result 

```json
// 20230726105643
// http://localhost:8080/actuator

{
    "_links": {
    "self": {
      "href": "http://localhost:8080/actuator",
      "templated": false
    },
    "health": {
      "href": "http://localhost:8080/actuator/health",
      "templated": false
    },
    "health-path": {
      "href": "http://localhost:8080/actuator/health/{*path}",
      "templated": true
    },
    "prometheus": {
      "href": "http://localhost:8080/actuator/prometheus",
      "templated": false
    },
    "metrics-requiredMetricName": {
      "href": "http://localhost:8080/actuator/metrics/{requiredMetricName}",
      "templated": true
    },
    "metrics": {
      "href": "http://localhost:8080/actuator/metrics",
      "templated": false
    }
  }
}
```


## Primary files
* `Application.java` - this has the main code for the application and shows
how to add tags and extra data via Sentry.configureScope as well as add
MDC data
* `CustomBeforeSendCallback` - All Sentry SDKs support the beforeSend callback method. beforeSend is called immediately before the event is sent to the server, so it’s the final place where you can edit its data. It receives the event object as a parameter, so you can use that to modify the event’s data or drop it completely (by returning null) based on custom logic and the data available on the event.
* `DistributedTracingJaegerApplication` - RestTemplate bean to allow Jaeger to include an interceptor. This then helps to add traces to the outgoing request which will help to trace the entire request. 
* `application.properties` - include the Sentry project properties here, as well
as set the log levels to send to Sentry
* `application.yaml` - add some properties to allow the application to send the traces to the Jaeger, Prometheus and expose the healchecks by Spring Actuator.
* `pom.xml` - including the required libraries for the project

## Sentry Documentation
https://docs.sentry.io/platforms/java/guides/logback/
https://docs.sentry.io/platforms/java/guides/spring-boot/

## Jaeger Documentation
https://www.jaegertracing.io/docs/1.46/