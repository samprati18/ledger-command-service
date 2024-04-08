-- Insert data into EntityCommand table
INSERT INTO entity (name) VALUES ('Organization');
INSERT INTO entity (name) VALUES ('Corporation');

-- Insert data into AssetCommand table
INSERT INTO asset (name) VALUES ('Fiat Currency');
INSERT INTO asset (name) VALUES ('Crypto');
INSERT INTO asset (name) VALUES ('Stock');
INSERT INTO asset (name) VALUES ('Bond');

-- Insert data into AccountCommand table
INSERT INTO account (entity_id, account_number, name, state) VALUES (1, '12345678', 'Savings Account', 'OPEN');
INSERT INTO account (entity_id, account_number, name, state) VALUES (1, '87654321', 'Investment Account', 'CLOSED');


-- Insert data into WalletCommand table
INSERT INTO wallet (name,account_id, asset_id, balance) VALUES ('Savings Wallet',1, 1, 1000.00);
INSERT INTO wallet (name,account_id, asset_id, balance) VALUES ('Checking Wallet',1, 2, 0.5);
INSERT INTO wallet (name,account_id, asset_id, balance) VALUES ('Investment Wallet',2, 3, 500.00);
INSERT INTO wallet (name,account_id, asset_id, balance) VALUES ('Travel Wallet',2, 4, 1000.00);


-- Insert data into LedgerEntryCommand table (Sample historical balance data)
INSERT INTO ledger_entry (wallet_id, timestamp, balance) VALUES (1, '2022-01-01 00:00:00', 1000.00);
INSERT INTO ledger_entry (wallet_id, timestamp, balance) VALUES (2, '2022-01-01 00:00:00', 0.5);
INSERT INTO ledger_entry (wallet_id, timestamp, balance) VALUES (3, '2022-01-01 00:00:00', 500.00);
INSERT INTO ledger_entry (wallet_id, timestamp, balance) VALUES (4, '2022-01-01 00:00:00', 1000.00);

-- Insert data into HistoricalBalanceCommand table (Sample historical balance data)
INSERT INTO historical_balance (wallet_id, timestamp, balance) VALUES (1, '2022-01-01 00:00:00', 1000.00);
INSERT INTO historical_balance (wallet_id, timestamp, balance) VALUES (2, '2022-01-01 00:00:00', 0.5);
INSERT INTO historical_balance (wallet_id, timestamp, balance) VALUES (3, '2022-01-01 00:00:00', 500.00);