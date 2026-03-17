# Energy Management Platform 

This project implements a scalable **Microservices Architecture** designed to manage users, smart energy devices, and monitor energy consumption in real-time.

The system has evolved to use **Docker Swarm** for orchestration, ensuring high availability, scalability, and self-healing capabilities. It utilizes **Traefik** as a dynamic Load Balancer and API Gateway, and integrates **AI capabilities** via Google Gemini.

## System Architecture

The application is decoupled into autonomous services that communicate over an encrypted overlay network (`energy-net`).

### Core Microservices

* **User Service** (Spring Boot)
    * **Port**: `8080`
    * **Description**: Manages User CRUD operations and business logic.
* **Device Service** (Spring Boot)
    * **Port**: `8081`
    * **Description**: Manages Devices and their association with users.
* **Auth Service** (Spring Boot)
    * **Port**: `8082`
    * **Description**: Handles Login and JWT Token generation.
* **Monitoring Service** (Spring Boot)
    * **Port**: `8083` (Internally Load Balanced)
    * **Scaling**: **Replicated Service** (Default: 2 instances).
    * **Description**: Consumes sensor data, detects threshold violations, and saves measurements. Supports horizontal scaling.
* **WebSocket Service** (Spring Boot)
    * **Port**: `8084`
    * **Description**: Pushes real-time alerts to the Frontend when energy limits are exceeded.
* **Chat Service** (Spring Boot)
    * **Port**: `8085`
    * **Description**: AI Assistant utilizing **Google Gemini API** to answer user queries regarding energy efficiency.
* **Frontend** (React.js)
    * **Port**: `3000`
    * **Description**: Web interface for Administrators and Clients with real-time charts and chat.
* **Sensor Simulator** (Java)
    * **Description**: Generates energy data and pushes it to RabbitMQ.

### Infrastructure Components

* **Traefik (Reverse Proxy & Load Balancer)**: The single entry point. Routes external HTTP traffic (Port `80`) to internal services and balances load across replicated containers (e.g., Monitoring Service).
* **Docker Swarm**: Orchestration engine managing service replicas, networking, and rolling updates.
* **RabbitMQ**: Message broker for asynchronous communication (Sensor Data, Sync Events).
* **PostgreSQL Databases**:
    * `user-db`, `device-db`, `auth-db`: Business entities.
    * `measurements-db`: Time-series energy data.
    * `chat-db`: Chat history/metadata.

## Prerequisites

Before you begin, ensure you have the following installed:

* **Docker Desktop** (With Kubernetes disabled, Swarm enabled)
* **Java 17 JDK**
* **Maven**

## Project Setup & Deployment

### 1. Initialize Docker Swarm

Since this project uses Swarm features (overlay networks, configs, replicas), you must initialize the swarm mode:

```
docker swarm init

```

### 2. Configure Environment Variables

Create a `.env` file in the root directory to store sensitive configuration and scaling preferences:

```
# .env file content
MONITORING_REPLICAS=2
GEMINI_API_KEY=Your_Google_Gemini_Api_Key_Here

```

### 3. Build the Microservices

Compile the Java applications to generate the JAR files. Run this command in the root/demo of each microservice directory:

```
./mvnw clean package -DskipTests

```

*(Repeat for: user, device, auth, monitoring, chat, websocket, and sensor-simulator)*

### 4. Build Docker Images

Build the images locally before deploying the stack:

```
docker-compose build

```

### 5. Deploy the Stack

Unlike standard Docker Compose, we use `docker stack` to deploy to the Swarm cluster:

```
docker stack deploy -c docker-compose.yml energy-stack

```

### 6. Access the Platform

Once the services are running (verify with `docker service ls`), access the components:

* **Frontend Application**: [http://localhost](https://www.google.com/search?q=http://localhost)
* **Traefik Dashboard**: [http://localhost:8088/dashboard/](https://www.google.com/search?q=http://localhost:8088/dashboard/)
* **RabbitMQ Management**: [http://localhost:15672](https://www.google.com/search?q=http://localhost:15672) *(guest/guest)*

---

## API Routing Strategy (Traefik)

Traefik dynamically routes traffic based on URL prefixes:

* `localhost/` -> **Frontend**
* `localhost/auth/*` -> **Auth Service**
* `localhost/people/*` -> **User Service**
* `localhost/devices/*` -> **Device Service**
* `localhost/monitoring/*` -> **Monitoring Service** (Load Balanced across replicas)
* `localhost/chat/*` -> **Chat Service**
* `localhost/ws/*` -> **WebSocket Service**

## Key Functional Scenarios

### 1. Scalable Monitoring & Load Balancing

The **Monitoring Service** is designed to scale.

* **Configuration**: Defined by `MONITORING_REPLICAS` in `.env`.
* **Behavior**: If set to `3`, Docker Swarm creates 3 containers. Traefik automatically round-robins HTTP requests between them.
* **Message Queueing**: Each replica listens on a unique dynamic queue (e.g., `monitoring-q-1`, `monitoring-q-2`) to avoid processing the same sensor data twice.

### 2. AI-Powered Chat Assistant

* Users can ask questions about energy saving.
* The **Chat Service** acts as a secure proxy to the **Google Gemini API**.
* It injects a system prompt ("You are a helpful energy assistant...") and returns the AI response to the user.

### 3. Real-Time Alerts (WebSockets)

1. **Simulator** sends data -> **RabbitMQ**.
2. **Monitoring Service** processes data. If `current_consumption > max_limit`:
3. It sends a message to the **WebSocket Service**.
4. **WebSocket Service** pushes a notification instantly to the React Frontend.

### 4. Distributed Data Synchronization

When a user is deleted in `user-service`, a RabbitMQ event is fired. The `device-service` consumes this event and unassigns all associated devices, ensuring data consistency across boundaries.

## Maintenance Commands

**Scale a service manually:**

```
docker service scale energy-stack_monitoring-service=4

```

**Force update a service (e.g., after config change):**

```
docker service update --force energy-stack_traefik

```

**Stop and Remove the Stack:**

```
docker stack rm energy-stack

```

