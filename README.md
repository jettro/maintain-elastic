Maintain-elastic
==================

[![Join the chat at https://gitter.im/jettro/maintain-elastic](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/jettro/maintain-elastic?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Application with utilities to maintain Elasticsearch. At the moment the focus is on indexes. You can change some
properties like number_of_replicas, but you can also close/open/optimize/delete the indexes. A very interesting feature
is to copy indexes with a number of options. Check the functions section.

Using the tool
==================

To be able to use the tool you have to build it using maven. Than you have to create a configuration file. Finally run
the created jar file. The steps in more detail.

## build using maven
We use the shade plugin to create a single runnable jar. The command to do this is easy.

```
mvn package
```

The result is a big jar file in the target folder called _dropwizard-elastic-0.1-SNAPSHOT.jar_. In the run section we
explain how to use this big jar to actually run the application.

## Create config.yml

We use a _config.yml_ file to configure the application. In this file we configure the elasticsearch connection and the
location of the file upload storage. Below is an example configuration.

```
elasticsearchHost: localhost:9300
clusterName: jc-play
usernamePassword: jettro:nopiforme
tempUploadFolder: /Users/yourname/temp/uploads/

server:
  applicationConnectors:
  - type: http
    port: 9000
  adminConnectors:
  - type: http
    port: 9001
```

## Run the application

Now it is time to start the application. Create the config.yml file and copy the jar file to the same location. Than
start the application using the following command.

```
java -jar dropwizard-elastic-0.1-SNAPSHOT.jar server config.yml
```

The output should end with the following two sentences

```
INFO  [2015-01-07 12:05:22,224] org.eclipse.jetty.server.ServerConnector: Started application@42039326{HTTP/1.1}{0.0.0.0:8080}
INFO  [2015-01-07 12:05:22,224] org.eclipse.jetty.server.ServerConnector: Started admin@33308786{HTTP/1.1}{0.0.0.0:8081}
```

Technology
==================

The application makes heave use of Dropwizard, the rest api and an AngularJS front-end.
