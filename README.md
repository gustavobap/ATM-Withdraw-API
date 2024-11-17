
# ATM Withdraw API

This repository contains a Spring Boot application developed as part of the Smart Money admission challenge. The API handles ATM withdrawal requests while adhering to the ACID model, ensuring data consistency in multi-threaded or multi-process environments.

---

## Getting Started

### Running the Server

The project includes a Docker container for easy deployment. Alternatively, the provided `.war` file can be deployed manually.

#### **Using Docker**
1. Extract the repository zip file.
2. Navigate to the root folder and execute the following script:
   ```bash
   ./install.sh
   ```
3. The server will start automatically on port `8080`.

4. To stop/restart the server:
   ```bash
   sudo docker start smartmoney_challenge
   ```

#### **Manual Deployment**
- Deploy the `challenge.war` file from the `Docker/` directory to your web server of choice.

---

## API Endpoints

The API consists of two main resources: `User` and `Withdraw`. Each resource provides endpoints for creating, retrieving, and listing data.

---

### **User Resource**

#### Create User
- **Endpoint**: `POST /api/users`
- **Request Body**:
  ```json
  {
    "name": "Test",
    "email": "test@test.com"
  }
  ```
- **Response**:
  ```json
  {
    "code": 101,
    "email": "test@test.com",
    "name": "Test"
  }
  ```

#### Find User
- **Endpoint**: `GET /api/users/{code}`
- **Response**:
  ```json
  {
    "code": 555,
    "email": "test@email.com",
    "name": "test"
  }
  ```

#### List Users
- **Endpoint**: `GET /api/users`
- **Response**:
  ```json
  [
    {
      "code": 1,
      "email": "test@email.com",
      "name": "Test 1"
    },
    {
      "code": 2,
      "email": "test2@email.com",
      "name": "Test 2"
    }
  ]
  ```

---

### **Withdraw Resource**

#### Create Withdraw
- **Endpoint**: `POST /api/withdrawals`
- **Request Body**:
  ```json
  {
    "value": 50,
    "user": {
      "email": "test@email.com"
    }
  }
  ```
- **Response**:
  ```json
  {
    "code": 151,
    "createdDate": "2020-12-01T07:25:53.534Z",
    "value": 50,
    "fee": 1.5,
    "user": {
      "code": 101
    }
  }
  ```

#### Find Withdraw
- **Endpoint**: `GET /api/withdrawals/{code}`
- **Response**:
  ```json
  {
    "code": 151,
    "createdDate": "2020-12-01T07:25:53.534Z",
    "value": 50,
    "fee": 1.5,
    "user": {
      "code": 101
    }
  }
  ```

#### List Withdrawals
- **Endpoint**: `GET /api/withdrawals`
- **Response**:
  ```json
  [
    {
      "code": 151,
      "createdDate": "2021-04-11T01:41:58.927Z",
      "value": 33.34,
      "fee": 1.00020,
      "user": {
        "code": 101
      }
    },
    {
      "code": 201,
      "createdDate": "2021-04-10T03:42:01.967Z",
      "value": 33.34,
      "fee": 1.00020,
      "user": {
        "code": 555
      }
    }
  ]
  ```

---

## Validation Rules

- **Withdraw Value**:
  - Must have a maximum of 2 decimal places.
  - Excess precision will result in a validation error.

- **Fee Calculation**:
  - Returned with a maximum of 5 decimal places.
  - Rounded as necessary for precision.

---

## Technical Notes

- **Database**: The application uses an in-memory **HSQLdb** for quick testing and easy setup.
- **Logging**: SQL statements and API responses are logged for debugging and transparency.
- **Pretty Print**: API responses are formatted for readability.

---

## Uninstalling

To remove the Docker container and image:
```bash
./uninstall.sh
```
