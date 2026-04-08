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
