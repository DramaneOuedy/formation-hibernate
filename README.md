# Formation Hibernate & JPA avec Spring Boot 3.4.3

Projet fil rouge de la formation **Hibernate – Niveau intermédiaire** (16 h, 2 jours).
Formateur : OUEDRAOGO Dramane.

Domaine : gestion commerciale — `Client → Commande → LigneCommande → Produit` (+ `Categorie`, `Utilisateur`).

## Prérequis

- JDK 17 ou plus récent
- Maven 3.9+
- MySQL 8 installé en local (port 3306), avec la base `formation_hibernate` et l'utilisateur `formation` / `formation`
- VS Code avec les extensions recommandées (*Extension Pack for Java*, *Spring Boot Extension Pack*)

Création de la base et du compte, à exécuter une fois en tant que `root` :

```sql
CREATE DATABASE IF NOT EXISTS formation_hibernate;
CREATE USER IF NOT EXISTS 'formation'@'localhost' IDENTIFIED BY 'formation';
GRANT ALL PRIVILEGES ON formation_hibernate.* TO 'formation'@'localhost';
```

## Vérifier le projet (sans MySQL)

```bash
mvn test
```

Les tests tournent sur une base H2 en mémoire et vérifient chaque module :
cycle de vie, cascades, requêtes, rollback, nombre de requêtes du N+1, cache, Spring Data, mini-projet.

## Lancer un TP (avec MySQL)

Chaque TP est un `CommandLineRunner` activé par un profil. La base est recréée à chaque lancement.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=tp1
```

Dans VS Code : onglet **Run and Debug**, choisir la configuration (TP1 … TP8, Mini-projet).

| Profil | Module | Ce que montre le TP |
| --- | --- | --- |
| `tp1` | 1 – Architecture | persist, dirty checking, détaché / merge, cache de 1er niveau |
| `tp2` | 2 – Mapping | enum, `@Embedded`, index, contrainte d'unicité |
| `tp3` | 3 – Relations | cascade, orphanRemoval, `LazyInitializationException`, clé étrangère |
| `tp4` | 4 – Requêtes | JPQL, DTO, pagination, `JOIN FETCH`, Criteria, SQL natif, UPDATE en masse |
| `tp5` | 5 – Transactions | commit / rollback, piège des exceptions contrôlées, `rollbackFor` |
| `tp6` | 6 – Performance | N+1 mesuré (101 requêtes) puis corrigé (1 requête), import en masse |
| `tp7` | 7 – Cache | cache de 2e niveau et cache de requête |
| `tp8` | 8 – Architecture | Service / Repository, DTO, Spring Data JPA |
| `projet` | 9 – Mini-projet | les 11 fonctionnalités demandées |

## Un tag Git par module

```bash
git tag            # m0 ... m9
git checkout m3    # état du projet à la fin du module 3
git checkout main  # version complète (avec les tests)
```

Pratique le jour J : si le participant prend du retard, il repart du tag du module en cours.

## Choix techniques

- `spring.jpa.open-in-view=false` : sinon la `LazyInitializationException` n'apparaît jamais.
- Les modules 1 à 7 utilisent directement l'`EntityManager` ; Spring Data JPA n'arrive qu'au module 8.
- `ddl-auto=create` uniquement pour la formation ; en production : `validate` + Flyway ou Liquibase.
