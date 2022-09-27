EXPOSE 8080

WORKDIR /usr/local/bin/

COPY /target/tweet-app-0.0.1-SNAPSHOT.jar tweet-app.jar

CMD ["java","-jar","tweet-app.jar"]