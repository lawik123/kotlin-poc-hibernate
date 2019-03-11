 # Kotlin Hibernate PoC
 This project is a PoC of using Kotlin and Hibernate (JPA), it showcases the following features:
 * Defining entities
 * CRUD operations
 * Writing Queries using a DSL (which uses the JPA CriteriaQuery API under the hood)
 
 Note: It is assumed that you have basic knowledge of Kotlin, Hibernate and databases.
 
 ## Installation
 
 ### prerequisites
 * Installation of PostgreSQL 9.6 (and optionally a database tool like pgAdmin)
 
 ### Installation steps
 1. Clone this repository
 2. Restore the provided `database.backup` (located in the `resources` folder) on a clean database named `hibernate_poc`
 3. configure the following fields in `resources/hibernate.cfg.xml` to match against your database settings:
    * `hibernate.connection.username`
    * `hibernate.connection.password`
    * `hibernate.connection.url`

## Running the project
Run the following command in the root directory of the project: `gradlew run`. This will download gradle, download the required dependencies and run the project.

## About

### Defining entities
Entities are easily defined using Kotlin data classes (see `Person.kt` and `Task.kt`), you can use JPA annotations within the declaration of the data class.

#### caveats
* The gradle plugin `org.jetbrains.kotlin.plugin.jpa` is required when defining entities in data classes, this plugin provides a default no-arg constructor to Hibernate (you can't call this constructor from your code, it's made available to Hibernate using reflection).
* When using bidirectional mapping it's important to override the default toString function of (one of) the entities, as the generated toString functions will cause the toString of both entities to be called recursively which causes a StackOverflow error.

### CRUD Operations
CRUD operations are possible the same way as Java, this project makes use of a generic Dao which is capable of generic CRUD operations and is extendable (see `GenericDao.kt` and `PersonDao.kt`, and their implementations).

### Writing queries using a DSL
A DSL has been written for simplifying the writing of queries. 
This DSL allows you to go from this:
```kotlin
fun findAbove18AndNameContains(pattern: String): List<Person> {
    val builder = session.criteriaBuilder
    val criteriaQuery = builder.createQuery(Person::class.java)
    val root = criteriaQuery.from(Person::class.java)

    criteriaQuery.where(
        builder.like(root.get(Person::name.name), "%$pattern%"),
        builder.ge(root.get(Person::age.name), 18)
    )

    criteriaQuery.orderBy(
        builder.asc(root.get<String>(Person::age.name)),
        builder.asc(root.get<String>(Person::name.name))
    )

    return session.createQuery(criteriaQuery).resultList
}
```

to this:
```kotlin
fun findAbove18AndNameContains(pattern: String): List<Person> {
        return session.createQuery<Person> {
            where{
                Person::name.like("%$pattern%")
                Person::age.ge(18)
            }
            orderBy {
                asc(Person::age)
                asc(Person::name)
            }
        }.resultList
    }
```

This is made possible using a series of Kotlin features (extension functions, function literal with receiver, Kotlin reflection) see `HibernateUtil.kt`, `QueryContext.kt`, and `QueryDSL.kt`.
 

 
 
 
  
 
 