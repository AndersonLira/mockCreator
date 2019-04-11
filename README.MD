# Mockcreator
A basic SOAP mock creator that uses origin soap service

Mockcreator was developed in Java and the project uses maven for build.

For create jar and execute just type
```sh
mvn clean install
java -jar target/mockcreator-{version}T-jar-with-dependencies.jar -s
```
### Important

Some variables should be defined on your environment.

  - SERVICE_URL= origin url service
  - AUTH_STRING= Base64 authorization encode user:password example user:1234
  - SERVER_CONTEXT = context of service example mockservice

You also need to create a folder named **payloads** where executable jar is. Remember that folder target will be clean and the best approach is copy executable jar to other place.

## Configuration

The file of configurations is packaged in the jar (config.json). Each key changes mock server behaviour.
- **returnDelay** is used to delay return of methods configured in **delayMethods** property.
- **showErrorServer** if true service fault return will be logged.
- **workingAsProxy** if true mocks will be ignored. All requests will be in origin server.
- **clearCache** methods that need remove cache after some other method call.
- **cacheEvict** methods that should not be cached. These requests will be always in origin server.
- **staticReturn** methods that should have these returns statics. Here is configured the file of return.

It is possible edit file directaly in jar file, but server should not be executing.

**For help, execute jar file without parameters**