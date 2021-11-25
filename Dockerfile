FROM maven:3.6.1-jdk-8-alpine AS MAVEN_BUILD 

# copy the pom and src code to the container 
COPY ./ ./  

# package our application code 
RUN mvn clean package 

# the second stage of our build will use open jdk 8 on alpine 3.9 
FROM java:8-jdk

# copy only the artifacts we need from the first stage and discard the rest 
COPY --from=MAVEN_BUILD target/esdb.war /esdb.war 

# set the startup command to execute the jar 
CMD ["java", "-jar", "/esdb.war"]

EXPOSE 8080/tcp