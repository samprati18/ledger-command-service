# Ledger Command Service

## Documentation

For the given use case of the ledger requirement, a microservice is created using the latest Java and Spring Boot versions. The implementation follows the CQRS pattern, where the Ledger is divided into two microservices: ledger-command-service and ledger-query-service.

In the ledger-command-service, the following features of the ledger are implemented:

1. **Move Assets Between Wallets**: Allows clients to move assets from one wallet to another.
2. **Support for Multiple Asset Movements in Single Request**: Supports multiple movements of assets in a single request.
3. **Lifecycle Management of Accounts**: Defines lifecycle states for accounts, such as OPEN and CLOSED. Clients can change the state of an account from one to another.
4. **Change of Previous Postings**: Clients can change postings they have done before.
5. **Broadcast Balance Changes**: Broadcasts any balance changes of any wallet for its clients to listen to.
6. **Broadcast Movements**: Broadcasts any movement happening in the ledger for its clients to listen to.
7. **Publishing Command Changes**: Publishes command events to Kafka topics to update the query tables, ensuring that the query service stays up-to-date with changes from the command service.
8. **Asynchronous Requests**: Broadcast request and Publishing Command changes flow are asynchronous.

## Table Structures

### Account Table (`account`)

| Column Name       | Data Type        | Constraints                      | Description                                              |
|-------------------|------------------|----------------------------------|----------------------------------------------------------|
| `id`              | BIGINT           | PRIMARY KEY, AUTO_INCREMENT     | Primary key of the account.                              |
| `account_number`  | VARCHAR(255)     | UNIQUE NOT NULL                 | Unique identifier for the account.                       |
| `entity_id`       | BIGINT           | FOREIGN KEY (references entity) | Foreign key referencing the associated entity.           |
| `name`            | VARCHAR(255)     | NOT NULL                         | Name of the account.                                     |
| `state`           | ENUM             | NOT NULL                         | State of the account (e.g., OPEN, CLOSED).               |

### Asset Table (`asset`)

| Column Name       | Data Type        | Constraints                      | Description                                              |
|-------------------|------------------|----------------------------------|----------------------------------------------------------|
| `id`              | BIGINT           | PRIMARY KEY, AUTO_INCREMENT     | Primary key of the asset.                                |
| `name`            | VARCHAR(255)     | NOT NULL                         | Name of the asset.                                       |

### Historical Balance Table (`historical_balance`)

| Column Name       | Data Type        | Constraints                      | Description                                              |
|-------------------|------------------|----------------------------------|----------------------------------------------------------|
| `id`              | BIGINT           | PRIMARY KEY, AUTO_INCREMENT     | Primary key of the historical balance entry.            |
| `wallet_id`       | BIGINT           | FOREIGN KEY (references wallet) | Foreign key referencing the associated wallet.          |
| `balance`         | DOUBLE           | NOT NULL                         | Balance amount at the specified timestamp.              |
| `timestamp`       | VARCHAR(255)     | NOT NULL                         | Timestamp of the historical balance entry.              |

### Entity Table (`entity`)

| Column Name  | Data Type    | Constraints                  | Description                          |
|--------------|--------------|------------------------------|--------------------------------------|
| `id`         | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Primary key of the entity.           |
| `name`       | VARCHAR(255) | NOT NULL                     | Name of the entity.                  |

### Movement Table (`movement`)

| Column Name          | Data Type    | Constraints                           | Description                                |
|----------------------|--------------|---------------------------------------|--------------------------------------------|
| `id`                 | BIGINT       | PRIMARY KEY, AUTO_INCREMENT          | Primary key of the movement.               |
| `from_wallet_id`     | BIGINT       | FOREIGN KEY (references wallet)       | Foreign key referencing the source wallet. |
| `to_wallet_id`       | BIGINT       | FOREIGN KEY (references wallet)       | Foreign key referencing the destination wallet. |
| `amount`             | DOUBLE       | NOT NULL                              | Amount of the movement.                    |
| `timestamp`          | VARCHAR(255) | NOT NULL                              | Timestamp of the movement.                 |
| `state`              | ENUM         | NOT NULL                              | State of the movement (e.g., PENDING, CLEARED). |

### Wallet Table (`wallet`)

| Column Name          | Data Type    | Constraints                           | Description                                |
|----------------------|--------------|---------------------------------------|--------------------------------------------|
| `id`                 | BIGINT       | PRIMARY KEY, AUTO_INCREMENT          | Primary key of the wallet.                 |
| `name`               | VARCHAR(255) | NOT NULL                              | Name of the wallet.                        |
| `account_id`         | BIGINT       | FOREIGN KEY (references account)      | Foreign key referencing the associated account. |
| `asset_id`           | BIGINT       | FOREIGN KEY (references asset)        | Foreign key referencing the associated asset. |
| `balance`            | DOUBLE       | NOT NULL                              | Current balance of the wallet.             |


## Kafka Integration

To maintain synchronization between the command and view data, Kafka is utilized for event-driven communication. Command events are published to Kafka, and the ledger-query service functions as a command listener, consuming events from multiple topics and updating the corresponding query tables. Below are the Kafka topics used for this purpose:

| Kafka Topic                     | Description                                                                     |
|--------------------------------|---------------------------------------------------------------------------------|
| `ledger-account-state-change`  | Listens to events related to changes in account states. Updates the `account_view` table accordingly.                                       |
| `ledger-asset-movement-event`  | Listens to events related to asset movements. Updates the `asset_view` table accordingly.                                                      |
| `ledger-account-wallet-event`  | Listens to events related to changes in wallet details associated with accounts. Updates the `wallet_view` table accordingly.                 |
| `ledger-historical-balance-event` | Listens to events related to changes in historical balances. Updates the `historical_balance_view` table accordingly.               |


## Endpoint Descriptions

1. **Manage Account Lifecycle**
   - **Endpoint**: `/ledger/manageAccountLifecycle/{accountId}?newState=CLOSED` (PUT)
   - **Description**: This endpoint facilitates clients in altering the lifecycle state of an account. Upon providing the `accountId` and `newState` as parameters, the endpoint updates the account's state in the database. Asynchronous event publication to the Kafka topic "ledger-account-state-change" ensures timely synchronization with the query table "account_view", enabling clients to access the most up-to-date account information.

2. **Move Asset from One Wallet to Another**
   - **Endpoint**: `/ledger/moveAsset` (POST)
   - **Description**: In this endpoint, clients provide the `sourceWalletId`, `destinationWalletId`, and `amount`. The   endpoint first validates the presence of the source and destination wallets in the database. If they are available, it proceeds to determine the movement state. If the amount available in the source wallet is less than the given amount, the movement state is set to FAILED. Conversely, if this condition is met, the movement state is set to CLEARED. In case of any exceptions, the movement state is set to PENDING.

       Upon setting the movement state to CLEARED, the movement record is saved in the database. Additionally, historical balances are updated in the "historical_balance" table.

       Subsequently, Kafka events are published to synchronize movement records with the query table "movement_view", source and destination wallet records with the "wallet_view" table, and historical balance records with the "HistoricalBalanceView" table.

		Furthermore, wallet balance and movement changes are broadcasted to clients using the ApplicationEventPublisher from the Spring Boot library. The broadcast events, namely BalanceChangeEvent and AssetMovementChange, are assumed to be picked up by consumers and sent to the UI using websockets for real-time updates.

3. **Move Multiple Assets**
   - **Endpoint**: `/ledger/moveMultipleAssets` (POST)
   - **Description**: Allows clients to move multiple assets between wallets in a single request. Clients provide a list of `assetMovementRequest` objects in the request body, each containing `sourceWalletId`, `destinationWalletId`, and `amount`. The endpoint processes the asset movements using parallel stream operations.

4. **Manage Lifecycle of Postings**
   - **Endpoint**: `/ledger/{postingId}/setState?newState=CLEARED` (PUT)
   - **Description**: Enables clients to change the state of a posting. Clients provide the `postingId` or `movementStateId` and `newState` as parameters. The endpoint validates the ID's availability, updates the state in the database, broadcasts movement changes, and publishes Kafka events to keep asset movement changes in sync with the "movement_view" table.

## How to Run

1. Use H2 database for ledger-command-service.
2. The data.sql file, located in the application.resources directory, contains predefined    SQL queries to insert data into the database tables. These queries are executed when the    application starts up, ensuring that initial data is available in the tables.
3. Ensure Kafka is running locally or on a chosen platform.
4. Build the project using Gradle: `gradle clean build`.
5. Run the Spring Boot application.

## Assumptions

- For broadcasting balance change events and asset movement changes, `ApplicationEventPublisher` from the Spring Boot library is used to broadcast the `BalanceChangeEvent` and `AssetMovementChangeEvent`. It is assumed that the consumer of these events will pick up the event and send it to the UI using WebSocket.

