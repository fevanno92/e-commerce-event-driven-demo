# 🛒 E-commerce Event-Driven Platform (Spring Boot + AWS FIFO)

## 📌 Description

Cette plateforme démontre une architecture microservices distribuée, robuste et scalable, mettant l'accent sur la cohérence des données et la gestion avancée des erreurs transactionnelles.

### Technologies clés :
* **Spring Boot 4.0+** (Java 25)
* **Messaging** : AWS SNS/SQS en mode **FIFO** (garantie d'ordre et dédoublonnement) ou Kafka (suivant profil choisi)
* **Outbox Pattern** : Publication fiable des événements via une table de base de données dédiée
* **Saga Pattern** : Orchestration chorégraphiée par le service `Order` pour garantir la cohérence inter-services
* **Observabilité** : Tracing distribué avec **Zipkin**, metrics avec **Prometheus** et tableaux de bord **Grafana**
* **Infrastructure** : LocalStack (local), CloudFormation (AWS), Docker
* **Architecture** : architecture hexagonale et design DDD

## 🏗️ Architecture globale

```mermaid
flowchart TD
    subgraph Clients
        API[Postman / HTTP Client]
    end

    subgraph Service_Order [Order Service]
        OrderDB[(Order DB)]
        OrderSvc[Order Logic]
    end

    subgraph Service_Product [Product Service]
        ProductDB[(Product DB)]
        ProductSvc[Product Logic]
    end

    subgraph Service_Stock [Stock Service]
        StockDB[(Stock DB)]
        StockSvc[Stock Logic]
    end

    subgraph Service_Payment [Payment Service]
        PaymentDB[(Payment DB)]
        PaymentSvc[Payment Logic]
    end

    subgraph Messaging [AWS SNS/SQS FIFO]
        SNS((SNS Topics .fifo))
        SQS[[SQS Queues .fifo]]
    end

    API -->|REST| OrderSvc
    API -->|REST| ProductSvc

    OrderSvc -->|OrderCreated| SNS
    SNS -->|Fan-out| SQS
    SQS --> StockSvc

    StockSvc -->|StockReserved| SNS
    SNS -->|Fan-out| SQS
    SQS --> OrderSvc
    
    OrderSvc -->|OrderValidated| SNS
    SNS -->|Fan-out| SQS
    SQS --> PaymentSvc

    PaymentSvc -->|OrderPaid| SNS
    SNS -->|Fan-out| SQS
    SQS --> StockSvc
```

## 🔄 Workflow métier (Saga Choreography)

Le projet utilise une Saga chorégraphiée où le service `Order` agit comme coordinateur principal du cycle de vie. L'utilisation de **SNS/SQS FIFO** garantit que toutes les étapes pour une même commande sont traitées séquentiellement.

```mermaid
sequenceDiagram
    participant User
    participant Order
    participant Stock
    participant Payment

    User->>Order: POST /orders
    Order->>Order: Write to Outbox (OrderCreated)
    Order->>Stock: SNS: OrderCreatedEvent

    Stock->>Stock: Soft Reserve Items
    Stock->>Order: SNS: StockReservedEvent

    Order->>Order: Validate Order Status
    Order->>Payment: SNS: OrderValidatedEvent

    Payment->>Payment: Process Transaction
    Payment->>Order: SNS: PaymentProcessedEvent

    Order->>Order: Mark Paid
    Order->>Stock: SNS: OrderPaidEvent
    
    Stock->>Stock: Finalize Stock Deduction (Confirm)

    Note over Order,Stock: En cas d'erreur (ex: PaymentFailed),<br/>des événements de compensation<br/>libèrent le stock réservé.
```

## 🧠 Gestion des erreurs & Fiabilité
* **SQS Error Handler** : Gestionnaire d'erreurs centralisé détectant récursivement les exceptions non-re-tentables (ex: `JacksonException`, `IllegalArgumentException`).
* **DLQ (Dead Letter Queues)** : Redirection automatique vers des files `-dlq.fifo` après 3 tentatives infructueuses pour les erreurs re-tentables.
* **Idempotence** : Chaque consommateur vérifie si l'événement a déjà été traité pour éviter les doubles débits/réservations.

## 📊 Observabilité

Le projet inclut une stack complète d'observabilité accessible en local :

* **Zipkin** : [http://localhost:9411](http://localhost:9411) - Visualisez le tracing distribué de chaque commande.
* **Prometheus** : [http://localhost:9090](http://localhost:9090) - Explorez les metrics techniques.
* **Grafana** : [http://localhost:3000](http://localhost:3000) - Tableaux de bord pré-configurés (Login: `admin / admin`).

## 🐳 Lancer en local (Docker Compose)

1. **Prérequis** : Docker Desktop, Java 25, Maven.
2. **Lancement de l'infrastructure** (Kafka, Postgres, LocalStack, Metrics) :
   ```bash
   docker-compose up -d
   ```
3. **Lancement des Microservices** (via profil `app`) :
   ```bash
   docker-compose --profile app up --build
   ```

## 🚀 CI/CD

Un workflow **GitHub Actions** (`Manual Docker Build`) est disponible pour valider la construction des images Docker. Il utilise `Buildx` pour optimiser la mise en cache des dépendances Maven.

---
Projet réalisé dans le cadre d’un portfolio backend/cloud engineering.