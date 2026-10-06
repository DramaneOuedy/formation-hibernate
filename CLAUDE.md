# Projet : formation Hibernate / JPA (Spring Boot 3.4.3)

Projet pédagogique pour une formation de 16 h. Le code doit rester simple, lisible et commenté en français.

## Commandes
- Compiler et tester (H2, pas besoin de MySQL) : `mvn test`
- Lancer un TP (MySQL requis, voir docker-compose.yml) : `mvn spring-boot:run -Dspring-boot.run.profiles=tp1` (tp1..tp8, projet)

## Structure
- `entity/` entités JPA, `repository/` accès aux données (CommandeQueries en EntityManager, ClientRepository/CommandeRepository en Spring Data)
- `service/` règles métier + `@Transactional`, `dto/` records exposés, `tp/` un CommandLineRunner par TP (activé par `@Profile`)
- Les tags Git `m0`..`m9` marquent l'état du projet à la fin de chaque module : ne pas les déplacer.

## Règles
- Toutes les associations en LAZY ; pas de cascade vers Client ou Produit.
- Transactions uniquement dans les services, jamais dans les repositories ni les runners.
- Garder `spring.jpa.open-in-view=false`.
- Les assertions des tests (nombre de requêtes SQL notamment) sont des objectifs pédagogiques : corriger le code plutôt que les assertions, sauf erreur évidente dans le test.
