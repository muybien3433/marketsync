# MarketSync

MarketSync is a solution that delivers real-time market insights, enabling personalized notifications and wallet 
management. It helps users track assets and receive timely updates on their subscriptions based on market changes.

The platform fetches and compares targeted assets in real time, providing dynamic insights to keep users 
informed about market fluctuations. It simplifies the management of cryptocurrency interests and subscriptions, 
while also offering the ability to create and manage personal wallets that calculate profits in real time, 
based on the user's preferred currency. Users can track assets, view detailed information, 
and calculate real-time profits across various asset types.

## Features

- **User Authentication**: Secure and robust authentication via Keycloak, 
enabling OAuth2-based login for seamless user access.
- **Subscriptions**: Effortlessly add or remove currency subscriptions with custom price and currencies thresholds.
- **Real-Time Notifications**: Receive timely email notifications when subscribed assets hit user-defined price targets, 
ensuring that users stay informed about market changes in real-time.
- **Wallet Management**: Manage and track personal customer wallets, 
with the ability to store and view assets across multiple types, 
including real-time values, calculated profit and transaction history.
- **Live Assets and Currencies Pricing**: Access up-to-the-minute pricing data for 
various assets (over 10k crypto), stocks coming soon.
- **Multiple Currency Support**: Choose between currencies (USD, EUR, GBP, PLN) 
for subscription management and wallet display. Users can select their preferred currency for wallet management, 
asset tracking and subscription pricing.
- **Scalable Architecture**: Designed with scalability in mind, the system can handle growing user 
bases and asset types as market demands evolve.

## Tech Stack

- **Frontend**: Angular 18
- **Backend**: Java 23, SpringBoot 6, Kafka
- **Database**: PostgreSQL for SQL and MongoDB for NoSQL
- **Authentication**: OAuth2, Keyclock
- **Cloud**: Docker, Eureka
- **Scrap**: Selenium, JSoup
- **Testing**: JUnit 5
- **Build Tool**: Maven, Docker

## Prerequisites

Ensure you have the following installed before running the project:

- **Java 23**
- **Maven**
- **Docker**

## API Endpoints

**API will be available at localhost:9999/api/v1**

- *Finance*: `/finances`
    - `GET /{asset-type}/{uri}` – Fetch the current price for specified type and uri with default currency.
    - `GET /{asset-type}/{uri}/{currency}` – Fetch the current price for specified type and uri and calculate desired by currency.
    - `GET /{asset-type}` – Display available uris to assets in specified type as list.
    - `GET /currencies/{from}/{to}` – Fetch the current exchange for two currencies.

- *Subscription*: `/subscriptions`
    - `GET /` – List all active subscriptions for the customer.
    - `POST /increase` – Add a new subscription with a condition: actualValue > value.
    - `POST /decrease` – Add new subscription with condition actualValue < value.
    - `DELETE /` – Delete subscription.

- *Wallet*: `/wallets/assets`
    - `GET /{currency}` – View all assets in the customer’s wallet as list in desired currency.
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
   
3. Provide environment variables for SMTP in config-server microservice:
      - {EMAIL_USERNAME} 
      - {EMAIL_PASSWORD}

4. Build the project (order: config-server -> discovery -> allOthers)
   ```bash
   mvn clean install
   ```
   
5. Run tests
   ```bash
   mvn clean test
   ```

## Contributing
Contributions are welcome! Feel free to submit issues and pull requests.

## License
This project is licensed under the MIT License.
