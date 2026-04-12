[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/UR3vHHhL)
# Equipes
## -ANDRIANIAINA LIANTSOA FEHIZORO
## -ANDRIANAMBININA FENITRA TOKINIAINA

---

# Dataset Generator

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


---

## Modifications apportées à l'architecture initiale

> Cette section compare l'architecture initiale (UML de conception) avec l'architecture implémentée, et explique chaque changement.

---

### 1. `Constraint` : interface → classe abstraite

**Avant :** `<<Interface>> Constraint` avec une seule méthode `isValid(value): boolean`

**Après :** `(abstract) Constraint` avec `isValid()` abstraite + méthode concrète `describe()`

**Pourquoi ?** Une interface ne peut pas fournir de comportement partagé sans `default`, ce qui est moins propre. Dès qu'on a voulu ajouter une méthode commune à toutes les contraintes, la classe abstraite s'est imposée. De plus, une lambda Java ne peut pas étendre une classe abstraite, ce qui nous a obligés à créer `NoConstraint` comme vraie classe.

---

### 2. `Exporter` : interface → classe abstraite (Template Method Pattern)

**Avant :** `<<Interface>> Exporter` avec `export(entityName, data)` — chaque exporteur (CSVExporter, XMLExporter) réimplémentait tout de zéro.

**Après :** `(abstract) Exporter` dont la méthode `export()` définit le squelette de l'algorithme (début du document → bloc par entité → fin du document). Chaque sous-classe ne redéfinit que `appendEntityBlock()`. `JsonExporter` surcharge directement `export()` car sa structure est différente.

**Nouveauté :** `JsonExporter` a été ajouté. L'ancien diagramme n'avait que `CSVExporter` et `XMLExporter`.

**Pourquoi ?** Éviter de dupliquer la boucle principale dans chaque exporteur. C'est le **Template Method Pattern**.

---

### 3. `DataType` (enum) → `FieldType` (classe abstraite) + `FieldTypeRegistry`

C'est le changement le plus profond.

**Avant :** `<<Enum>> DataType` (STRING, INTEGER, FLOAT...) stocké dans `Attribute`. `GeneratorFactory` faisait le lien entre un `DataType` et un `DataGenerator`.

**Après :** `(abstract) FieldType` avec trois méthodes abstraites : `code()`, `getGenerator()`, `createConstraint()`. Chaque type (`IntegerFieldType`, `EmailFieldType`, `EnumFieldType`...) est une classe qui porte à la fois son code, son générateur et sa contrainte. Le `FieldTypeRegistry` (map `code → FieldType`) remplace la `GeneratorFactory`.

**Pourquoi ?** L'enum ne pouvait pas évoluer sans être modifié (violation Open/Closed). Avec des classes, on ajoute un nouveau type en créant une nouvelle classe sans toucher au reste. Le Registry évite les `if/else` dans les services.

---

### 4. `Attribute` : objets Java → codes persistés en base (JPA)

**Avant :**
```java
Attribute {
    DataType dataType;            // enum Java
    List constraints; // objets Java
}
```

**Après :**
```java
AttributeEntity {
    String fieldTypeCode;   // ex: "INTEGER", "EMAIL"
    String constraintJson;  // ex: {"min":18,"max":90}
}
```

**Pourquoi ?** JPA ne peut pas persister des objets polymorphiques comme `Constraint` ou `FieldType` sans configuration complexe. On stocke donc un code et un JSON. Au moment de la génération, le `FieldTypeRegistry` retrouve le bon `FieldType` depuis le code, et `AttributeMapper` désérialise le JSON manuellement (sans ObjectMapper).

---

### 5. `RandomDataGenerator` / `ApiDataGenerator` → générateurs spécialisés

**Avant :** Deux générateurs génériques : `RandomDataGenerator` (tout aléatoire) et `ApiDataGenerator` (via API externe).

**Après :** Chaque type a son propre générateur dédié : `RandomIntegerGenerator`, `RandomStringGenerator`, `EmailGenerator`, `EnumGenerator`, `DateGenerator`, `AutoIncrementGenerator`, `NameGenerator`.

`NameGenerator` concrétise l'idée de l'ancien `ApiDataGenerator` : il appelle l'API **randomuser.me** (`GET /api/?nat=fr`) pour obtenir de vrais noms, avec un fallback local en cas d'échec réseau.

**Pourquoi ?** Un générateur générique ne peut pas gérer les spécificités de chaque type (ex : une date nécessite des bornes `after`/`before`, un entier des bornes `min`/`max`). Chaque générateur connaît exactement le format de sa config.

---

### 6. Support des sous-entités (auto-référence JPA)

**Avant :** `Entity` avait déjà `subEntities: List<Entity>` dans le diagramme, mais ce n'était pas implémenté.

**Après :** `EntityModelEntity` est une vraie entité JPA auto-référencée avec `parentEntity` (ManyToOne) et `subEntities` (OneToMany + `orphanRemoval = true`). La génération est récursive : `DatasetService` filtre les entités racines (`parentEntity == null`), génère leurs lignes, puis appelle `generateSubRows()` récursivement pour chaque sous-entité imbriquée.

---

### 7. Couche Mapper manuelle

**Avant :** Absente du diagramme initial.

**Après :** Trois mappers manuels (`ProjectMapper`, `EntityModelMapper`, `AttributeMapper`) assurent la conversion entre entités JPA et DTOs. `AttributeMapper` contient un sérialiseur JSON et un parseur récursif écrits à la main, sans bibliothèque externe.

---

### Tableau récapitulatif

| Concept | Ancien | Nouveau |
|---|---|---|
| `Constraint` | Interface | abstract class + `describe()` |
| `Exporter` | Interface | abstract class + Template Method |
| `DataType` | Enum dans Attribute | abstract class FieldType + Registry |
| `Attribute` | tient des objets Java | stocke `fieldTypeCode` + `constraintJson` |
| Générateurs | `RandomDataGenerator` / `ApiDataGenerator` | 7 générateurs spécialisés dont `NameGenerator` (API) |
| Export | CSV + XML | CSV + XML + JSON |
| Sous-entités | prévu, non implémenté | JPA auto-référencé + génération récursive |
| Mappers | absents | `ProjectMapper`, `EntityModelMapper`, `AttributeMapper` |


---

---

## Tests & QA

Les tests d’API sont automatisés avec **Postman / Newman** et se trouvent dans le dossier `tests/`.

### Contenu du dossier `tests/`

| Fichier | Description |
|---|---|
| `collection.json` | Collection Postman v2.1 — 34 requêtes, 72 assertions |
| `environment.json` | Environnement Postman (base_url, variables de chaînage) |
| `report.html` | Rapport Newman HTML généré |

### Structure de la collection

| Dossier | Requêtes | Objectif |
|---|----------|---|
| **A. Unit Tests** | 10       | Smoke tests de chaque endpoint individuellement |
| **B. Error Cases** | 7        | Validation des codes 400/404 sur les cas d’erreur |
| **C. Integration Scenario** | 13       | Scénario complet avec chaînage de variables (`project_id`, `entity_id`) |

### Prérequis

```bash
# Node.js >= 18 requis
npm install -g newman
npm install -g newman-reporter-htmlextra
```

### Lancer l’application

```bash
# Dans le répertoire du projet
sur le bouton run
  ou
./gradlew bootRun
# L’API est disponible sur http://localhost:8080
```

### Exécuter les tests

```bash
# Depuis la racine du projet
newman run tests/collection.json \
  --environment tests/environment.json \
  --reporters cli,htmlextra \
  --reporter-htmlextra-export tests/report.html
```

### Résultat attendu

```
┌─────────────────────────┬──────────┬──────────┐
│                         │ executed │   failed │
├─────────────────────────┼──────────┼──────────┤
│              iterations │        1 │        0 │
│                requests │       33 │        0 │
│            test-scripts │       30 │        0 │
│      prerequest-scripts │        0 │        0 │
│              assertions │       72 │        0 │
├─────────────────────────┴──────────┴──────────┤
│ total run duration: ~4s                        │
│ total data received: ~18 KB                    │
└────────────────────────────────────────────────┘
```

> Le rapport HTML généré (`tests/report.html`) donne la vue détaillée par requête.

---


### Linux (erreur de permission, besoin de sudo) : sudo npm install -g newman newman-reporter-htmlextra
### Windows : npm install -g newman newman-reporter-htmlextra
