FROM maven:3.8.1-jdk-8

WORKDIR /
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run