FROM adoptopenjdk:8u242-b08-jre-openj9-0.18.1-bionic

COPY build/libs/javalin-demo-all.jar /javalinapp.jar

EXPOSE 8080

CMD ["java", "-jar", "-Xmx128M", "-noverify", "-XX:TieredStopAtLevel=1", "/javalinapp.jar"]
