FROM amazoncorretto:17 as build
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN ./gradlew build

FROM amazoncorretto:17
COPY --from=build /home/gradle/source/build/libs/movies-0.0.1.jar /app/
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "movies-0.0.1.jar"]
