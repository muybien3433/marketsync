# MarketSync

MarketSync is a backend service solution designed to empower users with real-time market insights
and personalized notifications for stock market subscriptions. MarketSync fetch data and compare
targeted subscriptions in real time. Application offers a dynamic platform for users to stay
informed and manage their cryptocurrency interests effortlessly.

## Features

- **User Authentication**: OAuth2 Authorizaiton.
- **Subscription Management**: Add or remove currency subscriptions.
- **Wallet**: Personal customer wallet manage assets.
- **Assets**: Add or remove assets to wallet.
- **Notifications**: Send e-mail to user when subscription reach upper or lower bound.

## Coming Soon
- Frontend
- More stocks

## Tech Stack

- **Backend**: Java 23, SpringBoot 6
- **Database**: PostgreSQL (Relational Database)
- **Authentication**: OAuth2
- **Testing**: JUnit 5 for unit tests
- **Build Tool**: Maven for dependency management

## Prerequisites

Ensure you have the following installed before running the project:

- **Java**: 23
- **PostgreSQL**
- **Maven**

## API Endpoints

**API will be available at localhost:8080/api/v1**

- *Wallet*: `/wallet`
    - `GET /` – Display all customer assets in wallet.

- *Asset*: `/asset`
    - `POST /` – Create new asset and add it to wallet.
    - `DELETE /{id}` – Delete asset from wallet.

- *Subscription*: `/subscriptions`
    - `GET /` – Display all customer subscriptions.
    - `POST /{uri}` – Create new subscription (params: upperValueInPercent, lowerValueInPercent).
    - `DELETE /{uri}/{id}` – Delete subscription.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/muybien3433/marketsync.git
   ```
3. Create application.properties and provide following:
   ```bash
   spring.jpa.hibernate.ddl-auto=update
   spring.datasource.url=jdbc:postgresql://localhost:5432/your-db-name
   spring.datasource.username=your-username
   spring.datasource.password=your-password

   spring.security.oauth2.client.registration.google.client-id=your-client-id
   spring.security.oauth2.client.registration.google.client-secret=your-client-secret
   spring.security.oauth2.client.registration.google.scope=openid, email, profile

   crypto.api.uri=https://api.coincap.io/v2/assets/

   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@mail.com
   spring.mail.password=your-password (it is not gmail account password)
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true 
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

