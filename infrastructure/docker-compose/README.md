# Docker Compose - E-Commerce Infrastructure

## Lancer uniquement PostgreSQL

Pour le développement local (lancer l'app manuellement) :

```bash
docker-compose up -d
```

Puis lancer le Product Service avec le profil `postgres` :

```bash
cd ../../product-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Lancer PostgreSQL + Product Service

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
- **PostgreSQL** : localhost:5432
  - Database : `productdb`
  - User : `postgres`
  - Password : `postgres`
