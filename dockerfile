FROM openjdk:16-jdk-alpine
VOLUME /tmp
ADD target/tweetapp-0.0.1-SNAPSHOT.jar tweetapp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","tweetapp-0.0.1-SNAPSHOT.jar"]