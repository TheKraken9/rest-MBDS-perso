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

## Tests & QA

Les tests d’API sont automatisés avec **Postman / Newman** et se trouvent dans le dossier `tests/`.

### Contenu du dossier `tests/`

| Fichier | Description |
|---|---|
| `collection.json` | Collection Postman v2.1 — 34 requêtes, 72 assertions |
| `environment.json` | Environnement Postman (base_url, variables de chaînage) |
| `report-sample.html` | Rapport Newman HTML pré-généré (exemple de résultat attendu) |

### Structure de la collection

| Dossier | Requêtes | Objectif |
|---|---|---|
| **A. Unit Tests** | 12 | Smoke tests de chaque endpoint individuellement |
| **B. Error Cases** | 7 | Validation des codes 400/404 sur les cas d’erreur |
| **C. Integration Scenario** | 15 | Scénario complet avec chaînage de variables (`project_id`, `entity_id`) |

### Prérequis

```bash
# Node.js >= 18 requis
npm install -g newman
npm install -g newman-reporter-htmlextra
```

### Lancer l’application

```bash
# Dans le répertoire du projet
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
│                requests │       34 │        0 │
│            test-scripts │       34 │        0 │
│      prerequest-scripts │        6 │        0 │
│              assertions │       72 │        0 │
├─────────────────────────┴──────────┴──────────┤
│ total run duration: ~4s                        │
│ total data received: ~18 KB                    │
└────────────────────────────────────────────────┘
```

> Le rapport HTML généré (`tests/report.html`) donne la vue détaillée par requête. Un exemple de rapport est disponible dans `tests/report-sample.html`.

---


## Linux (erreur de permission, besoin de sudo) : npm install -g newman newman-reporter-htmlextra
## Windows npm install -g newman newman-reporter-htmlextra
