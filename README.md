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

> Cette section explique les changements que nous avons effectués par rapport à la conception initiale, et pourquoi nous les avons faits.

---

### 1. Interfaces → Classes abstraites

**Avant :**
```
interface Constraint   { isValid(value): boolean }
interface FieldType    { code(); getGenerator(); createConstraint() }
interface Exporter     { export(...): String }
```

**Après :**
```
abstract class Constraint  { abstract isValid(); describe() }
abstract class FieldType   { abstract code(); abstract getGenerator(); abstract createConstraint() }
abstract class Exporter    { abstract format(); export() (Template Method) }
```

**Pourquoi ?**

- `Constraint` : on a ajouté une méthode concrète `describe()` commune à toutes les implémentations. Une interface ne peut pas fournir de comportement partagé de cette façon (sans default, ce qui est moins propre). La classe abstraite est plus adaptée quand toutes les sous-classes partagent du code.
- `FieldType` : même raison — on voulait pouvoir ajouter de la logique commune aux types de champs sans dupliquer dans chaque implémentation.
- `Exporter` : on a appliqué le **Template Method Pattern**. La méthode `export()` définit le squelette de l'algorithme (début du document → blocs par entité → fin du document), et chaque sous-classe (`XmlExporter`) redéfinit uniquement `appendEntityBlock()`. `JsonExporter` surcharge directement `export()` car sa structure est différente. Ce pattern évite de répéter la boucle principale dans chaque exporteur.

---

### 2. `Attribute` → `AttributeEntity` : du domaine pur vers la persistance JPA

**Avant (domaine conceptuel) :**
```java
class Attribute {
    String name;
    FieldType fieldType;      // objet Java
    Constraint constraint;    // objet Java
}
```

**Après (entité JPA persistée en base H2) :**
```java
@Entity
class AttributeEntity {
    String name;
    String fieldTypeCode;     // ex: "INTEGER", "ENUM"
    String constraintJson;    // ex: {"min":18,"max":90}
}
```

**Pourquoi ?**

JPA ne peut pas persister des objets polymorphiques (`FieldType`, `Constraint`) sans configuration complexe. On stocke donc un code (chaîne de caractères) et une configuration JSON (sérialisée manuellement). Au moment de la génération, le `FieldTypeRegistry` retrouve le bon objet `FieldType` à partir du code, et `AttributeMapper` désérialise le JSON en `Map<String, Object>`.

---

### 3. Support des sous-entités

L'entité `EntityModelEntity` est **auto-référencée** : elle a un champ `parentEntity` et une liste `subEntities`. Cela permet de modéliser des structures imbriquées comme un `Product` contenant plusieurs `Review`.

La génération est **récursive** : `DatasetService` parcourt d'abord les entités racines (`parentEntity == null`), génère leurs lignes, puis pour chaque sous-entité appelle `generateSubRows()` qui s'appelle elle-même récursivement. Le résultat est un tableau imbriqué dans chaque ligne parente.

---

### 4. Registry Pattern

On utilise un `FieldTypeRegistry` et un `ExporterRegistry` : ce sont des maps (`code → objet`) qui permettent de résoudre dynamiquement le bon type ou exporteur à partir d'une chaîne de caractères. Cela évite les `if/else` ou `switch` et respecte le principe Open/Closed (on ajoute un nouveau type sans toucher au service).

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

> Le rapport HTML généré (`tests/report.html`) donne la vue détaillée par requête. Un exemple de rapport est disponible dans `tests/report-sample.html`.

---


### Linux (erreur de permission, besoin de sudo) : sudo npm install -g newman newman-reporter-htmlextra
### Windows npm install -g newman newman-reporter-htmlextra
