FROM openjdk:11
VOLUME /tmp
EXPOSE 9085
ADD ./target/bootCoin-0.0.1-SNAPSHOT.jar ms-boot-coin.jar
ENTRYPOINT ["java", "-jar","/ms-boot-coin.jar"]

