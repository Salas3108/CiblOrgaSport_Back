# Postman - Ticket API Tests

- Import `Ticket.postman_collection.json` into Postman.
- Set environment variable `baseUrl` (default: `http://localhost:3000`).
- Run the requests in order:
  1) List Tickets
  2) Create Ticket
  3) Get Ticket by ID
  4) Update Ticket
  5) Delete Ticket
  6) Calculate Price by Category
- Use the Collection Runner to execute all tests and view assertions.

Notes:
- The tests expect fields: `id`, `category`, `price` in Ticket JSON.
- Adjust endpoint paths if your controller uses different routes (e.g., `/api/tickets`).
- The price endpoint assumes a GET returning a number or `{ "price": <number> }`.
