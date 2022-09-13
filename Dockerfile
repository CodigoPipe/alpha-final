FROM openjdk:11.0.14
EXPOSE 8080
ADD target/alpha-post-comments.jar alpha-post-comments.jar
ENTRYPOINT ["java", "-jar", "/alpha-post-comments.jar"]