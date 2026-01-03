# Auth Service Quick Tests

## 1) Register
POST http://localhost:8080/auth/register
Body (JSON):
{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "password": "Secret123!",
  "role": "ATHLETE"
}

## 2) Login
POST http://localhost:8080/auth/login
Body (JSON):
{
  "username": "john.doe",
  "password": "Secret123!"
}
Response contains:
{
  "token": "<JWT_TOKEN>",
  "type": "Bearer",
  "username": "john.doe",
  "role": "ROLE_ATHLETE"
}

## 3) Me (secured)
GET http://localhost:8080/auth/me
Headers:
Authorization: Bearer <JWT_TOKEN_FROM_LOGIN>

---

## Curl equivalents

# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"john.doe",
    "email":"john.doe@example.com",
    "password":"Secret123!",
    "role":"ATHLETE"
  }'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username":"john.doe",
    "password":"Secret123!"
  }'

# Me (replace TOKEN)
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer TOKEN"
