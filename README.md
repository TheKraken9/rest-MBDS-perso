---

## Phase 4 : Architecture Microservices

### Nouveaux modules ajoutés

| Module | Port | Rôle |
|---|---|---|
| `eureka-server` | 8761 | Registre de services (Spring Cloud Netflix Eureka) |
| `config-server` | 8888 | Configuration centralisée (Spring Cloud Config) |
| `dataset-manager-service` | 8081 | Gestion des projets et entités (REST + JPA/H2) |
| `generator-service` | 8082 | Génération et export de données |
| `api-gateway` | 8080 | Point d'entrée unique (Spring Cloud Gateway) |

---

### Schéma de l'architecture

```
Client
  │
  ▼
API Gateway (:8080)          ← point d'entrée unique
  ├─→ dataset-manager-service (:8081)   ← projets & entités
  └─→ generator-service (:8082)         ← génération

Eureka Server (:8761)        ← tous les services s'y enregistrent
Config Server (:8888)        ← distribue les fichiers YAML de config

generator-service ──Feign──→ dataset-manager-service
                              (récupère la définition du projet pour générer)
```

---

### Lancement

```bash
./run.sh
```

Cette commande compile les JARs de tous les modules puis lance l'ensemble via Docker Compose.

**Prérequis :** Java 25, Docker, Docker Compose

---

### Endpoints (via Gateway :8080)

**Projets**
```
POST   /api/projects
GET    /api/projects
GET    /api/projects/{id}
DELETE /api/projects/{id}
```

**Entités**
```
POST   /api/entities/project/{projectId}
GET    /api/entities/project/{projectId}
GET    /api/entities/{id}
PUT    /api/entities/{id}
DELETE /api/entities/{id}
```

**Génération**
```
GET    /api/generator/{projectId}/preview
GET    /api/generator/{projectId}/export?format=json
GET    /api/generator/{projectId}/export?format=csv
GET    /api/generator/{projectId}/export?format=xml
```

**Types de champs disponibles**

`STRING` · `INTEGER` · `FLOAT` · `BOOLEAN` · `DATE` · `ENUM` · `EMAIL` · `AUTOINCREMENT` · `NAME`

---

### Communication inter-services (Feign)

Le `generator-service` appelle le `dataset-manager-service` via **Spring Cloud OpenFeign** pour récupérer la définition d'un projet avant de générer les données.

---

### Circuit Breaker (Resilience4J)

Un circuit breaker protège l'appel Feign du `generator-service` vers le `dataset-manager-service` :

| Paramètre | Valeur |
|---|---|
| Fenêtre glissante | 5 appels |
| Seuil d'ouverture | 50% d'échecs |
| Durée état ouvert | 10 secondes |
| Appels en half-open | 2 |

En cas de panne du `dataset-manager-service`, le `generator-service` retourne une réponse de fallback (`503`).

Le gateway intègre également des circuit breakers sur chaque route, avec redirection vers `/fallback/{service}`.

---

### Tableau de bord Eureka

Accessible sur : http://localhost:8761

Vérifie que les services `DATASET-MANAGER-SERVICE`, `GENERATOR-SERVICE` et `API-GATEWAY` sont bien enregistrés.