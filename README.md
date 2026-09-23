# Banking Service

Spring Boot 2.7 service targeting Java 8 or newer. It uses MySQL in production and H2 for tests.

## Run

Create a MySQL database and set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, or use the defaults in `application.properties`.

```bash
mvn spring-boot:run
```

## API

- `POST /api/accounts` with `{"accountId":"A-1","amount":100.00}`
- `GET /api/accounts/{accountId}`
- `GET /api/accounts/{accountId}/transactions`
- `POST /api/accounts/{accountId}/deposits` with `{"amount":25.00}`
- `POST /api/accounts/{accountId}/withdrawals` with `{"amount":25.00}`
- `POST /api/accounts/transfers` with `{"sourceAccountId":"A-1","destinationAccountId":"A-2","amount":25.00}`

Amounts must be positive for operations and support up to four decimal places. Accounts cannot be overdrawn. Transfers are atomic and create an outgoing and incoming ledger entry.

## Test

```bash
mvn test
```