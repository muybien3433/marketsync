# MarketSync

MarketSync is a solution that delivers real-time market insights, enabling personalized notifications and wallet 
management. It helps users track assets and receive timely updates on their subscriptions based on market changes.

The platform fetches and compares targeted assets in real time, providing dynamic insights to keep users 
informed about market fluctuations. It simplifies the management of cryptocurrency and stocks interests and subscriptions, 
while also offering the ability to create and manage personal wallets that calculate profits in real time, 
based on the user's preferred currency. Users can track assets, view detailed information, 
and calculate real-time profits across various asset types.

## Features

- **User Authentication**: Secure and robust authentication via Keycloak.
- **Subscriptions**: Effortlessly add or remove currency subscriptions with custom price and currencies thresholds.
- **Real-Time Notifications**: Receive timely email notifications when subscribed assets hit user-defined price targets, 
ensuring that users stay informed about market changes in real-time.
- **Wallet Management**: Manage and track personal customer wallets, 
with the ability to store and view assets across multiple types, 
including real-time values, calculated profit and transaction history.
- **Live Assets and Currencies Pricing**: Access up-to-the-minute pricing data for 
various assets, stocks coming soon.
- **Multiple CurrencyType Support**: Choose between currencies (USD, EUR, GBP, PLN) 
for subscription management and wallet display. Users can select their preferred currency for wallet management, 
asset tracking and subscription pricing.
- **Scalable Architecture**: Designed with scalability in mind, the system can handle growing user 
bases and asset types as market demands evolve.

## Tech Stack

- **Frontend**: Angular 18
- **Backend**: Java 23, SpringBoot 6, Kafka
- **Database**: PostgreSQL for SQL and MongoDB for NoSQL
- **Authentication & Security**: Keyclock, Gateway
- **Cloud**: AWS-SES, Config-server
- **Scraping**: Selenium, JSoup
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
    - `GET /{asset-type}` – Display available assets and price.
    - `GET /{asset-type}/currencies/{currency}` – Display available assets and price with desired currency.
    - `GET /currencies/{from}/{to}` – Fetch the current exchange for two currencies.

- *Subscription*: `/subscriptions`
    - `GET /` – List all active subscriptions for the customer.
    - `POST /` – Add a new subscription.
    - `DELETE /{uri}/{id}` – Delete subscription.

- *Wallet*: `/wallets/assets`
    - `GET /{currency}` – View all aggregated assets in the customer’s wallet.
    - `GET /history` – View all assets addition history.
    - `POST ` – Add a new asset to the wallet.
    - `PATCH /{id} ` – Edit asset in the wallet.
    - `DELETE /{id}` – Remove an asset from the wallet.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/muybien3433/marketsync.git
   ```
   
2. Provide region, access-key and access-secret for AWS-SES in backend/services/config-server/configurations/notification-service-dev.yml


3. Now everything you need to do is to start docker containers
   ```bash
   docker compose -f docker-compose.dev.yml up -d
   ```
   
4. Run tests
   ```bash
   mvn clean test
   ```
   
## Contributing
Contributions are welcome! Feel free to submit issues and pull requests.

## License
This project is licensed under the MIT License.
