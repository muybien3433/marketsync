# MarketSync

MarketSync is about to
This project implements essential backend services such as fetch market cap or user authentication.
Data storage using modern web technologies such as Hibernate and PostgreSQL.

## Features

- **User Authentication**: Sign in with OAuth2
- **Subscription Management**: Add or remove currency subscriptions.
- **Notifications**: Send e-mail to user when subscription reach upper or lower bound.

## Tech Stack

- **Backend**: Java 23, SpringBoot 6
- **Database**: PostgreSQL (Relational Database)
- **Authentication**: OAuth2 
- **Testing**: JUnit 5 for unit tests
- **Build Tool**: Maven for dependency management

## Prerequisites

Ensure you have the following installed before running the project:

- **Java**: 23
- **PostgreSQL**: 
- **Maven**: For building and running the project

## API Endpoints

**API will be available at localhost:8080/api/v1**

- *Subscription*: `/subscribe`
    - `POST /` – Create new subscription @@@@@@
    - `DELETE /{uri}/{id}` – Delete subscription.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/muybien3433/marketsync.git
   ```
3. Create application.yml and provide following:
   ```bash
   datasource:
    url: "jdbc:mysql://your-db-url/your-schema-name"
    username: "your-db-username"
    password: "your-db-password"

   @@@@@@@
   ```
4. Reload maven project
   ```bash
   mvn clean install
   ```
5. Run tests
   ```bash
   mvn test
   ```
6. Run the application
   ```bash
   mvn spring-boot:run
   ```

## Contributing
Submit issues and pull requests are more than welcome.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

