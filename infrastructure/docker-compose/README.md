# Docker Compose - E-Commerce Infrastructure

## Lancer uniquement les instances de PostgreSQL/Kafka

Pour le développement local (lancer l'app manuellement) :

```bash
docker-compose up -d
```

Puis lancer les différents microservices avec le profil `postgres`, par exemple pour le Product Service :

```bash
cd ../../product-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Lancer PostgreSQL/Kafka + tous les microservices

Pour tout lancer ensemble :

```bash
docker-compose --profile app up -d
```

## Arrêter les services

```bash
# Arrêter sans supprimer les volumes
docker-compose --profile app down

# Arrêter et supprimer les volumes (reset de la BDD)
docker-compose --profile app down -v
```

## Accès

- **Product Service** : http://localhost:8081
- **Order Service** : http://localhost:8082
- **PostgreSQL - Product Service** : localhost:5432
  - Database : `productdb`
  - User : `postgres`
  - Password : `postgres`
- **PostgreSQL - Order Service** : localhost:5433
  - Database : `orderdb`
  - User : `postgres`
  - Password : `postgres`
