# Equipes
## -ANDRIANIAINA LIANTSOA FEHIZORO
## -ANDRIANAMBININA FENITRA TOKINIAINA

---

# Dataset Generator — Phase 4 : Architecture Microservices

## Lancement rapide

```bash
./run.sh
```

Cette commande :
1. Compile les JARs de chaque module avec Gradle (`bootJar`)
2. Lance tous les conteneurs Docker dans l'ordre correct (`docker compose up --build`)

### Prérequis
- Java 25 (`openjdk-25-jdk-headless`)
- Docker + Docker Compose
- Gradle Wrapper inclus (`./gradlew`)

---

## Architecture microservices

```
                        ┌──────────────────┐
                        │   Eureka Server  │ :8761
                        │  (service registry)│
                        └────────┬─────────┘
                                 │  (tous s'enregistrent)
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
┌─────────▼────────┐   ┌─────────▼────────┐   ┌────────▼─────────┐
│  Config Server   │   │dataset-manager   │   │generator-service │
│   :8888          │   │   :8081          │   │   :8082          │
│  (configs YAML)  │   │  (JPA + H2)      │   │  (génération)    │
└──────────────────┘   └──────────────────┘   └──────────────────┘
                                 ▲                      │ Feign
                                 └──────────────────────┘
                        ┌──────────────────┐
                        │   API Gateway    │ :8080
                        │  (point d'entrée)│
                        └──────────────────┘
```

### Services

| Service | Port | Rôle |
|---|---|---|
| `eureka-server` | 8761 | Registre de services (discovery) |
| `config-server` | 8888 | Configuration centralisée (Spring Cloud Config) |
| `dataset-manager-service` | 8081 | Gestion des projets et entités (REST + JPA/H2) |
| `generator-service` | 8082 | Génération et export de données |
| `api-gateway` | 8080 | Point d'entrée unique (Spring Cloud Gateway) |

---

## Endpoints (via API Gateway :8080)

### Projets
```
POST   /api/projects              Créer un projet
GET    /api/projects              Lister tous les projets
GET    /api/projects/{id}         Récupérer un projet
DELETE /api/projects/{id}         Supprimer un projet
```

### Entités
```
POST   /api/entities/project/{id}  Ajouter une entité à un projet
GET    /api/entities/project/{id}  Lister les entités d'un projet
DELETE /api/entities/{id}          Supprimer une entité
```

### Génération
```
GET    /api/generator/{projectId}/preview             Prévisualiser (5 lignes)
GET    /api/generator/{projectId}/export?format=json  Exporter en JSON
GET    /api/generator/{projectId}/export?format=csv   Exporter en CSV
GET    /api/generator/{projectId}/export?format=json&count=50  Avec comptage
```

### Exemple de body pour créer une entité
```json
{
  "name": "User",
  "fields": [
    { "name": "firstName", "type": "FIRST_NAME" },
    { "name": "lastName",  "type": "LAST_NAME" },
    { "name": "email",     "type": "EMAIL" },
    { "name": "age",       "type": "INTEGER" },
    { "name": "city",      "type": "CITY" }
  ]
}
```

Types disponibles : `FIRST_NAME`, `LAST_NAME`, `EMAIL`, `INTEGER`, `FLOAT`, `BOOLEAN`, `DATE`, `CITY`, `COUNTRY`, `PHONE`, `UUID`

---

## Circuit Breaker (Resilience4J)

Le `generator-service` appelle le `dataset-manager-service` via **Feign** avec un **Circuit Breaker** :

- **Instance** : `dataset-manager`
- **Fenêtre** : 5 appels
- **Seuil d'ouverture** : 50% d'échecs
- **Durée ouverte** : 10 secondes
- **Half-open** : 2 appels de test

### Tester le circuit breaker

```bash
# Simuler une panne du dataset-manager-service
docker compose stop dataset-manager-service

# Appeler le generator → 503 avec message explicite
GET http://localhost:8080/api/generator/1/preview

# Rétablir le service
docker compose start dataset-manager-service
```

---

## Tableau de bord Eureka

Accessible à : http://localhost:8761

Vérifie que les 3 services sont enregistrés :
- `DATASET-MANAGER-SERVICE`
- `GENERATOR-SERVICE`
- `API-GATEWAY`

---

# Dataset Generator — Phases 1-3 : Monolithe



## Description du projet

Ce projet consiste à concevoir un système logiciel modulaire permettant de générer automatiquement des datasets à partir d’une définition structurée.

## Architecture du système

L’architecture est organisée en 4 parties principales :

---

###  1. Domaine

#### Project

Représente un projet de dataset.

```java
id: Long
name: String
size: int
entities: List<Entity>
```

* `size` représente le nombre d’enregistrements à générer par entité

---

#### Entity

Représente une entité métier (ex : User, Product).

```java
name: String
attributes: List<Attribute>
subEntities: List<Entity>
```

* supporte les relations via `subEntities`

---

#### Attribute

Représente un champ d’une entité.

```java
name: String
dataType: DataType
constraints: List<Constraint>
```

---

#### DataType (enum)

Types supportés :

* STRING
* INTEGER
* FLOAT
* BOOLEAN
* DATE
* ENUM

---

#### Constraint (interface)

```java
isValid(value: Object): boolean
```

Implémentations :

* `RangeConstraint` (min, max)
* `EnumConstraint` (liste de valeurs autorisées)

---

## 2. Génération des données

---

### DataGenerator (interface)

```java
generate(attribute: Attribute): Object
```

Implémentations :

* `RandomDataGenerator` → génération aléatoire
* `ApiDataGenerator` → génération via API externe

---

### GeneratorFactory

```java
getGenerator(attribute: Attribute): DataGenerator
```

Responsable de sélectionner le générateur approprié en fonction de l’attribut.

---

### DatasetService

```java
generate(project: Project)
```

Rôle :

* parcourir les entités
* générer les données
* utiliser `GeneratorFactory`
* produire la structure finale :

```java
Map<String, List<Map<String, Object>>>
```

---

## 3. Export des données

---

### Exporter (interface)

```java
export(entityName: String, data: List<Map<String,Object>>): String
```

Implémentations :

* `CSVExporter`
* `XMLExporter`

---

### ExportService

Responsable de l’export en utilisant un `Exporter`.

---

## Fonctionnement global

1. Création d’un `Project`
2. Définition des entités et attributs
3. Ajout des contraintes
4. Définition de la taille (`size`)
5. Génération des données via `DatasetService`
6. Export des données via `ExportService`

---

## Structure des données générées

```java
Map<String, List<Map<String, Object>>>
```

### Explication :

* `String` → nom de l’entité
* `List` → liste des enregistrements
* `Map` → un enregistrement (clé = attribut)

---

### Exemple :

```json
{
  "User": [
    { "name": "Jean", "age": 25 },
    { "name": "Marie", "age": 30 }
  ]
}
```

---

## Gestion de la taille

Le champ `size` dans `Project` détermine :

le nombre de lignes générées pour chaque entité

---

## Patterns utilisés

---

### Strategy Pattern

Utilisé pour :

* `DataGenerator`
* `Exporter`

Permet de changer dynamiquement le comportement.

---

### Factory Pattern

Utilisé avec `GeneratorFactory`.

Permet de :

* sélectionner dynamiquement le générateur
* éviter les structures conditionnelles complexes

---

## Respect des principes SOLID

---

### Single Responsibility Principle

Chaque classe a une responsabilité unique :

* `DatasetService` → génération
* `GeneratorFactory` → création
* `ExportService` → export

---

### Open/Closed Principle

Le système est extensible :

* ajout de nouveaux générateurs
* ajout de nouveaux formats d’export

sans modification du code existant.

---

### Liskov Substitution Principle

Les implémentations de :

* `DataGenerator`
* `Exporter`

peuvent être substituées sans modifier le comportement.

---

### Interface Segregation Principle

Interfaces simples :

* `DataGenerator`
* `Exporter`

---

### Dependency Inversion Principle

Les services dépendent d’abstractions :

* `DatasetService` → DataGenerator
* `ExportService` → Exporter

---
