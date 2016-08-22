To start application:
 - git clone https://github.com/Angry1980/infobip.git
 - cd ./infobip
 - gradlew build
 - java -jar build/libs/shortener-1.0-SNAPSHOT.jar

 To run integration tests you should use command: gradlew clean build integrationTest.

 To have a possibility to change settings of hystrix circuit breaker without restarting of the application you should use command:
 java -Darchaius.configurationSource.additionalUrls=file:///work/infobip/archaius.properties -jar build/libs/shortener-1.0-SNAPSHOT.jar.

 Where archaius.configurationSource.additionalUrls contains full path to file archaius.properties (in root of the project).

