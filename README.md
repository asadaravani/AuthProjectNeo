# Neobis Authentication Project

This Spring Boot application leverages Hibernate and PostgreSQL for efficient database management. Integrated with Spring Boot starter Mail, it seamlessly sends confirmation links to users upon registration. Upon authorization, the application generates JSON Web Tokens (JWT) for secure authentication.

Featuring three essential endpoints (*register, login, confirmToken*), it facilitates user registration and authentication processes. The API documentation is meticulously crafted using SwaggerUI.

This project has been instrumental in honing my Java programming skills, particularly in mastering the Spring Framework, proficient data handling, and effective API management.

## Prerequisites

- Java Development Kit (JDK) version 17
- Spring Framework version 3.2.5
- Apache Maven version 4.0.0
- PostgreSQL 14.11

## Installation
1. Prepare your droplet by installing __Prerequisites__ and the `git`.
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   sudo apt install maven
   sudo apt install postgresql postgresql-contrib
   sudo apt install git
   ```
2. Clone the repository to the ready droplet:
   ```bash
   git clone https://github.com/asadaravani/Neobis_Auth_Project.git
   ```
3. Prepare configurations(`application.yml`) for your machine.
4. Also, check `dropletApi` field in the `AppUserAuthenticationServiceImpl.class` to manage the confirmation link that will be sent to users.
5. __cd__ to the __root__ directory of the repo in your droplet and create __jar__:
   ```bash
   mvn clean package
   ``` 
6. __cd__ to the __target__ directory and execute the *jar*:
   ```bash
   java -jar AuthProject-0.0.1-SNAPSHOT.jar
   ```
  ## And it is running)))
