# Base64 Diff Application
REST Application to find the differences between two sets of base64 encoded data.

_Author: Júlia Zottis Moreira_

## Available Endpoints

| Operation | Path | Description |
|-----------|------|-------------|
| POST | \<host>/v1/diff/<id>/left | Adds or Updates data to Left side of the diff on given ID |
| POST | \<host>/v1/diff/<id>/right | Adds or Updates data to Right side of the diff on given ID |
| GET | \<host>/v1/diff/<id> | Gets the result of the diff operation for a given ID |

### Requirements
* Payload must be on JSON format
* Data must be encoded in Base64 

## Deployment
### Prerequisites
* Java JDK 1.8
* Maven 3.3.9

### Running Unit Tests
There are 32 unit tests that set the Line and Branch coverage at 100%.

To run only unit tests:
```
$ mvn clean test -Dtest=\!Base64DiffAppIntTest
```

Unit Test coverage is provided by JaCoCo, you can find the report at `./target/jacoco-coverage/index.html`.

### Running Integration Tests
There are 21 integration tests that set the Line and Branch coverage at 95%.

All integration tests are located in class [Base64DiffAppIntTest](./src/test/java/org/juliazo/diff/Base64DiffAppIntTest.java)

To run only integration tests:
```
$ mvn clean test -Dtest=Base64DiffAppIntTest
```

Integration Test coverage is provided by JaCoCo, you can find the report at `./target/jacoco-coverage/index.html`.

### Running Application
This is a Maven Spring Boot application, to run it, follow these steps:
#### 1) Build the code
```
$ mvn clean package
```
* All unit and integration tests will be executed;
* Application will not compile if there is any test failure;
* Coverage for all tests is available at `./target/jacoco-coverage/index.html`;
#### 2) Run application
```
$ mvn spring-boot:run
```
#### 3) Have fun!
You can access the app at `http://localhost:8080`.

### Examining Logs
Log4J was used to provide logs for this application. All logs are being shown in the console and printed out to a file.

Log level can be changed at [Application.properties](./src/main/resources/application.properties). Default level is INFO 
for classes inside [juliazo](./src/test/java/org/juliazo) package and ERROR for spring framework related classes.

Log files can be found in `./logs/application.log`.

## Examples

### POST to \<host>/v1/diff/\<id>/left
Case: Sending data for the first time with id 1

```
REQUEST
POST /v1/diff/1/left HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
	"data": "YWxnbG1hIGNvdXphIGFv"
}

```

```
RESPONSE
HTTP/1.1 201 CREATED
Content-Type: application/json
{
    "id": "1",
    "leftData": "YWxnbG1hIGNvdXphIGFv",
    "rightData": ""
}
```

Case: Sending empty data with id 2

```
REQUEST
POST /v1/diff/2/left HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
	"data": ""
}

```

```
RESPONSE
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json
{
    "errorCode": "400",
    "message": "Field data is required"
}
```

### POST to \<host>/v1/diff/\<id>/right
Case: Updating data with id 3

```
REQUEST
POST /v1/diff/3/right HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
	"data": "dG9sZXRpYmV1"
}

```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
    "id": "3",
    "leftData": "",
    "rightData": "dG9sZXRpYmV1"
}
```

Case: Sending invalid data with id 4

```
REQUEST
POST /v1/diff/4/right HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
	"data": "dG9sZ\\XRpYmV1"
}

```

```
RESPONSE
HTTP/1.1 400 BAD REQUEST
Content-Type: application/json
{
    "errorCode": "400",
    "message": "Input must use valid Base64 characters"
}
```

### GET to \<host>/v1/diff/\<id>
Case: Get diff with id 5, data is equal

```
REQUEST
GET /v1/diff/5 HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
    "id": "5",
    "equalSize": true,
    "equals": true
}
```

Case: Get diff with id 6, data does not have the same size

```
REQUEST
GET /v1/diff/6 HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
    "id": "6",
    "equalSize": false
}
```

Case: Get diff with id 7, there are differences in the data

```
DATA USED
{
    "id": "7",
    "leftData": "YnVsaWxpYXo=",
    "rightData": "Ym9saW5oYXM="
}
```

```
REQUEST
GET /v1/diff/7 HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
    "id": "7",
    "equalSize": true,
    "equals": false,
    "differences": [
        {
            "offset": 1,
            "length": 1
        },
        {
            "offset": 4,
            "length": 2
        },
        {
            "offset": 7,
            "length": 1
        }
    ]
}
```

## Assumptions
* Data persistence was not required, in-memory storage is being used;
* Differences on the data are being portrayed as which bytes differ from one side of the diff to the other;
* It was not defined how to handle a second POST to the same id on the same side of the diff, so I chose to use a 
different response code for this scenario.

## Improvements
* Data persistence could be added to improve reliability;
* The ID could be returned on the error messages for easy tracking;
* Logs could be separated into different files, to facilitate the analysis of the data;
* Logs and error messages could be better formatted to improve autonomous indexing and analysis;
* Create a mechanism to inform the user in a more explicit way which version of each side of the diff is being used
for a given GET diff operation.

## Support
If you have any question, please send an [email](mailto:juliazottis@hotmail.com).

