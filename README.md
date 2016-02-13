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
java -jar web-0.2-SNAPSHOT.jar server config.yml
```

The output should end with the following two sentences

```
INFO  [2015-11-24 21:19:53,473] org.eclipse.jetty.server.ServerConnector: Started application@4b14918a{HTTP/1.1}{0.0.0.0:9000}
INFO  [2015-11-24 21:19:53,474] org.eclipse.jetty.server.ServerConnector: Started admin@6d1ef78d{HTTP/1.1}{0.0.0.0:9001}
```

## Initializing (Creating/copying) a new index
When initializing a new index, you have the option to provide the settings and the mappings as configuration (json) files.

### settings.json
The index settings file needs to be named settings.json and should have a structure as shown in the _example_ below:
```
{
  "number_of_shards" :   1,
  "number_of_replicas" : 0,
    "index": {
        "analysis": {
            "analyzer": {
                "analyzer_keyword": {
                    "tokenizer": "keyword",
                    "filter": "lowercase"
                }
            }
        }
    }
}

```

### type-mappings.json
The index mappings file needs to be named <type>-mappings.json (where type can be any word) and should have a structure as shown in the _example_ below:
```
{
    "financer": {
        "_id": {
            "path": "id"
        },
        "properties": {
            "id": {
                "type": "integer"
            },
            "name": {
                "type": "string",
                "index": "analyzed",
                "analyzer": "analyzer_keyword"
            }
        }
    }
}



```

Technology
==================

The application makes heave use of Dropwizard, the rest api and an AngularJS front-end.
