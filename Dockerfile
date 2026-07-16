FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app
COPY StajUygulamasi.java .
COPY postgresql-42.7.3.jar .

RUN javac -cp postgresql-42.7.3.jar StajUygulamasi.java

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/StajUygulamasi*.class ./
COPY --from=builder /app/postgresql-42.7.3.jar .

EXPOSE 8000

CMD ["java", "-cp", ".:postgresql-42.7.3.jar", "StajUygulamasi"]
