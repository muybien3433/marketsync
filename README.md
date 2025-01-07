# MarketSync

MarketSync is a backend microservice solution that empowers users with real-time market insights 
and personalized notifications for stock market and cryptocurrency subscriptions.

It fetches data and compares targeted subscriptions in real time, offering a dynamic platform 
for users to stay informed and effortlessly manage their cryptocurrency interests. 
Users can create personal wallets to track assets, view detailed information, 
and calculate real-time profits for each asset.

## Features

- **User Authentication**: Secure authentication using Keycloak.
- **Subscriptions**: Add or remove currency subscriptions with custom thresholds.
- **Wallet**: Manage personal customer wallets to track assets.
- **Assets**: Add or remove assets from the wallet.
- **Notifications**: Send email notifications when subscription values reach specified thresholds.

## Tech Stack

- **Backend**: Java 23, SpringBoot 6, Kafka
- **Frontend**: Angular 18
- **Database**: PostgreSQL (Relational Database)
- **Authentication**: OAuth2, Keyclock
- **Cloud**: Docker, Eureka
- **Testing**: JUnit 5 for unit testing
- **Build Tool**: Maven, Docker

## Prerequisites

Ensure you have the following installed before running the project:

- **Java**: 23
- **PostgreSQL**
- **Maven**
- **Docker**

## API Endpoints

**API will be available at localhost:9999/api/v1**

- *Finance*: `/finances`
    - `GET /{type}/{uri}` – Fetch the current price for specified type and uri.
    - `GET /{type}` – Display available uri to assets in specified type as list.

- *Subscription*: `/subscriptions`
    - `GET /` – List all active subscriptions for the customer.
    - `POST /increase` – Add a new subscription with a condition: actualValue > value.
    - `POST /decrease` – Add new subscription with condition actualValue < value.
    - `DELETE /{id}` – Delete subscription.

- *Wallet*: `/wallets/assets`
    - `GET /` – View all assets in the customer’s wallet as list.
    - `GET /history` – View all assets addition history.
    - `POST ` – Add a new asset to the wallet.
    - `PUT ` – Edit asset in the wallet.
    - `DELETE /{id}` – Remove an asset from the wallet.
  
- *Customer*: `/customers`
    - `GET /` – Find and extract customer data from auth header.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/muybien3433/marketsync.git
   ```
   
2. Start docker containers
   ```bash
   docker-compose up
   ```
   
3. Provide environment variables in config-server microservice:
      - {DATABASE_USERNAME}
      - {DATABASE_PASSWORD}
      - {EMAIL_USERNAME}
      - {EMAIL_PASSWORD}

4. Build the project (order: config-server -> gateway -> allOthers)
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
Contributions are welcome! Feel free to submit issues and pull requests.

## License
This project is licensed under the MIT License.
