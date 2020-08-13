# Javalin Demo API

Application that demonstrates the essential functionalities about Javalin Micro Framework.

## Features 

This demo shows us some simple use cases most common in API:

   * Javalin basic configuration 
   * Application events (started, stoped)
   * Interceptor handler
   * Get path parameter
   * Asynchronous requests
   * Exception handler
   * Open API documentation via Plugin
   * Read external configuration via Json loader
   * Optimize Jetty runtime with custom Thread pool

## Endpoints

| Method | Path     | Description   |
|--------|----------|--------------------------------------|
| GET    | /        | Basic response                       |
| GET    | /:name   | Pass an attribute by request URI     |
| GET    | /async   | Demonstrates an asynchronous handler | 
| POST   | /user    | Gets a JSON object from request body |
| GET    | /docs    | Demonstrates a basic OpenAPI documentation |
| GET    | /swagger | Swagger json information about API   |

## Running Locally

To run the application, just use **gradle** on console. The application will start on *http://localhost:8080*.

```shell script
gradle run
```

PS. You must provide a token to make requests. The token can be any value on **Authorization** header, like example below:
 
 ```shell script
curl -X POST --url http://localhost:8080/user \
  -H 'authorization: k123-xyz987' \
  -H 'content-type: application/json' \
  -d '{ "name": "Keuller", "email": "keuller.magalhaes@kestraa.com", "password": "12345" }'
```

## Generate Uber JAR

```shell script
 gradle clean build
```

## Generate Docker Image

```shell script
docker build . -t javalin-demo
```

## Running Docker Image

```shell script
docker run --rm --name=javalinapp -p 8080:8080 javalin-demo
```
# Javalin-demo
