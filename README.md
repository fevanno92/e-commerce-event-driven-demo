# e-commerce-event-driven-demo
Conception et implémentation d’une plateforme e-commerce event-driven basée sur microservices (Spring Boot), déployée sur AWS avec ECS Fargate, SNS/SQS et Terraform, incluant gestion des transactions distribuées via Saga pattern.

Travail en cours, la description ci-dessous est l'objectif final

# 🛒 E-commerce Event-Driven Platform (AWS + Spring Boot + React)

## 📌 Description

Ce projet est une plateforme e-commerce basée sur une architecture microservices event-driven, construite avec :

* Backend : Spring Boot (Java)

* Frontend : React + TypeScript

* Messaging : AWS SNS/SQS (ou Kafka en local)

* Cloud : AWS (ECS Fargate, RDS, DynamoDB, S3)

* Infrastructure : Terraform

🎯 Objectif : démontrer des compétences en :

* Architecture microservices distribuée

* Event-driven design

* AWS cloud engineering

* CI/CD & containerisation

## 🏗️ Architecture globale

```mermaid
flowchart LR
    UI[React Frontend]

    UI -->|REST API| OrderService
    UI -->|REST API| ProductService

    OrderService -->|OrderCreated| SNS

    SNS --> StockService
    SNS --> PaymentService

    StockService -->|StockReserved / StockUnavailable| SNS
    PaymentService -->|PaymentProcessed / PaymentFailed| SNS

    SNS --> NotificationService
```

## 🔄 Workflow métier (Saga Pattern)

```mermaid
sequenceDiagram
participant User
participant OrderService
participant StockService
participant PaymentService
participant NotificationService

User->>OrderService: Create Order
OrderService->>SNS: OrderCreated

StockService->>StockService: Check & reserve stock

alt Stock disponible
    StockService->>SNS: StockReserved
    PaymentService->>PaymentService: Process payment
    
    alt Paiement OK
        PaymentService->>SNS: PaymentProcessed
        StockService->>StockService: Confirm stock deduction
        NotificationService->>User: Success notification
    else Paiement KO
        PaymentService->>SNS: PaymentFailed
        StockService->>StockService: Release stock
        NotificationService->>User: Payment failed
    end

else Stock insuffisant
    StockService->>SNS: StockUnavailable
    NotificationService->>User: Out of stock
end
```

## ⚙️ Microservices
### Product Service

* Gestion du catalogue produits

* Base de données : PostgreSQL

### Order Service

* Création des commandes

* Publie OrderCreated

### Stock Service

* Réserve le stock (soft reserve)

* Publie :

    * StockReserved

    * StockUnavailable

* Gère rollback via PaymentFailed

### Payment Service

* Traite les paiements

* Consomme StockReserved

* Publie :

    * PaymentProcessed

    * PaymentFailed

### Notification Service

* Envoie des notifications utilisateur

* Consomme tous les événements métier

## 📨 Exemple d’événements

### OrderCreated
```json
{
  "orderId": "123",
  "items": [{"productId": "p1", "quantity": 2}]
}
````

### StockReserved
```json
{
  "orderId": "123",
  "status": "RESERVED"
}
````

### PaymentFailed
```json
{
  "orderId": "123",
  "reason": "Card declined"
}
```

## 🧠 Gestion des erreurs

✔️ Pas de survente

✔️ Pas de paiement inutile

✔️ Rollback automatique

Stratégie utilisée :

* Saga pattern (event-driven)

* Soft stock reservation

* Compensation via événements (PaymentFailed)

## 🐳 Lancer en local (Docker Compose)
```bash
docker-compose up --build
````

Accès :

* Frontend → http://localhost:3000

* Product API → http://localhost:8081

* Order API → http://localhost:8082

## ☁️ Déploiement AWS
### Services utilisés :

* ECS Fargate → microservices

* RDS → bases PostgreSQL

* DynamoDB → notifications

* SNS/SQS → messaging

* S3 + CloudFront → frontend

* CloudWatch → logs

### Déploiement :
```bash
terraform init
terraform apply
````

## 🚀 CI/CD

GitHub Actions :

* Build

* Tests

* Docker build

* Push vers ECR

* Déploiement ECS

## ✨ Bonus (améliorations possibles)

* Dashboard temps réel des événements

* Observabilité (Prometheus + Grafana)

* Retry + Dead Letter Queue (DLQ)

* Authentification (JWT / Cognito)

## 📌 Points forts du projet

✔️ Architecture réaliste (comme en entreprise)

✔️ Event-driven + microservices


✔️ Gestion des erreurs avancée (Saga)

✔️ Full AWS + Terraform

✔️ Déployable et scalable

## 👨‍💻 Auteur

Projet réalisé dans le cadre d’un portfolio backend/cloud engineering.