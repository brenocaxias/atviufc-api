# Etapa 1: Construção (Usando uma imagem que já tem Maven e Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Roda o Maven direto (sem precisar do wrapper ./mvnw que estava dando erro)
RUN mvn clean package -DskipTests

# Etapa 2: Execução (Uma imagem leve só para rodar o site)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Pega o arquivo .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]