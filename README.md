# healthchecker

healthchecker is an open-source monitoring tool for services backed by network connections.

There are some built-in healthchecker implementations 
  - Check accessibility of TCP port
  - Check response of an http server by providing regex pattern
  - Check 'JSON' response alongside status code and headers of http server

Since implementation of HealthChecker interface is effortless, you can always implement your own healthchecker to meet your needs.
  

### Modules

healthchecker have two modules currently.

##### healthchecker-core
Provides main functionality of healthchecker
  - HealthChecker interface
  - HttpClient API (built-in OkHttp implementation)
  - HealthService interface which run inside in a Kotlin coroutine to check availability and accessibility of an http server

Inside test module of healthchecker-core there is a mock http-server implementation for testing purposes. Response can be route with various options, like JSONResponse, StringResponse, EchoResponse and LambdaResponse which can always be extended for certain requirements.

##### healthchecker-api
Exposes healthchecker-core functionality as an API, which is build with Ktor.


| method | resource | description |
| ------ | ------ | ------ |
| POST | create/ | create an health checker service |
| DELETE | destroy/{name} | destroy health service of given `name` |
| PUT | start/{name} | starts health service of given `name` |
| PUT | stop/{name} | stops health service of given `name` |
| GET | status/{name} | returns health of given service `name`  |


### Build and Installation

healthchecker requires Kotlin 1.3.71 and Java 8.

maven is main build tool for healthchecker. Building can be done in healthchecker-parent.

```sh
$ cd healthchecker-parent
$ mvn clean package
```

### Development

Want to create your own health service ?

Open your IDE and implement HealthChecker interface;

First Tab:
```kotlin
class CustomHealthChecker(): HealthChecker() {

    override fun check(): Health {
        // implementation
    }
```

then you need a class for configuration 

```
@HealthCheckerConfiguration(typeName = "custom")
class CustomHealthChecker (val args...): HealthCheckerProperties<CustomHealthChecker>() {
    override fun createChecker(): CustomHealthChecker = CustomHealthChecker()
}
```

### Usage

Install and deploy healthchecker in a Docker container.

By default, the application will expose port 8080, so change this within the Dockerfile if necessary. Also, you can set ```server.port``` JVM option to change port.

For build a container image
```sh
$ cd healthchecker-api
$ docker build -t healthchecker:${version} .
```

You're ready to run a container
```sh
$ docker run -d --rm -p 10000:8080 healthchecker:${version}
```

Let's create a service definition and check availability of a service

```sh
$ curl -XPOST 127.0.0.1:10000/create -d '{"name": "sample", "interval": 2000, "checker": { "type": "tcp", "ip": "127.0.0.1", "port": 20000, "timeout": 1000}}' -H "Content-Type: application/json"
```

Container is running now... We can query health status of the service by
```sh
$ curl 127.0.0.1:10000/status/sample -H "Content-Type: application/json"
{"data":"UNHEALTHY","status":true}
```

UNHEALTHY as expected. Now let netcat listens on TCP port `20000` which healthchecker is currently checking

```sh
$ nc -kl 20000
```

If we query now healthchecker we will see a HEALTHY service

```sh
$ curl 127.0.0.1:10000/status/sample -H "Content-Type: application/json"
{"data":"HEALTHY","status":true}
```

### Todos

 - Store healthcheck service statuses in a database to watch in such a period 
 - Develop a UI interface and interact with a database, graphs would be helpfull
 - Measure response time

License
----

MIT
