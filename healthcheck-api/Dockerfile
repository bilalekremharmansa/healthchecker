FROM openjdk:8-jre-alpine

ENV APPLICATION_USER healthchecker
RUN addgroup -S $APPLICATION_USER && adduser -S $APPLICATION_USER -G $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER:$APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/healthcheck-api.jar /app/healthcheck-api.jar
WORKDIR /app

CMD ["java", "-server", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "healthcheck-api.jar" ]