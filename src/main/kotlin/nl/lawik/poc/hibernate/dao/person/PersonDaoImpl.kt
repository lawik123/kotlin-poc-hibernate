package nl.lawik.poc.hibernate.dao.person

import nl.lawik.poc.hibernate.criteria.PersonCriteria
import nl.lawik.poc.hibernate.dao.generic.GenericDaoImpl
import nl.lawik.poc.hibernate.entity.Person
import nl.lawik.poc.hibernate.entity.Task
import nl.lawik.poc.hibernate.util.*
import org.hibernate.Session
import javax.persistence.criteria.*


class PersonDaoImpl(session: Session) : GenericDaoImpl<Person, Long>(Person::class.java, session), PersonDao {

    /**
     * Find example using the default way of the JPA CriteriaQuery API
     */
    override fun find(personCriteria: PersonCriteria): List<Person> {
        val builder = session.criteriaBuilder
        val criteriaQuery = builder.createQuery(Person::class.java)
        val root = criteriaQuery.from(Person::class.java)

        val predicates = mutableListOf<Predicate>()

        personCriteria.run {
            name?.let { predicates.add(builder.equal(root.get<String>(Person::name.name), it)) }
            minAge?.let { predicates.add(builder.ge(root.get(Person::age.name), it)) }
            maxAge?.let { predicates.add(builder.le(root.get(Person::age.name), it)) }

            if (taskNames.isNotEmpty()) {
                val join = root.join<Person, Task>(Person::tasks.name)
                predicates.add(builder.or(*taskNames.map {
                    builder.equal(
                        join.get<String>(Task::name.name),
                        it
                    )
                }.toTypedArray()))
            }
        }

        criteriaQuery.where(*predicates.toTypedArray())

        criteriaQuery.orderBy(
            builder.asc(root.get<Int>(Person::age.name)),
            builder.asc(root.get<String>(Person::name.name))
        )

        return session.createQuery(criteriaQuery).resultList
    }

    /**
     * Find example using the DSL defined in QueryDSL.kt
     */
    override fun find2(personCriteria: PersonCriteria): List<Person> {
        return session.createQuery<Person> {
            where {
                personCriteria.run {
                    name?.let { Person::name.equal(it) }
                    minAge?.let { Person::age.ge(it) }
                    maxAge?.let { Person::age.le(it) }

                    if (taskNames.isNotEmpty()) {
                        val join = join(Person::tasks)
                        or { taskNames.forEach { equal(join.get(Task::name), it) } }
                    }
                }
            }
            orderBy {
                asc(Person::age)
                asc(Person::name)
            }
        }.resultList
    }

    /**
     * Example using the DSL defined in QueryDSL.kt.
     *
     * @param pattern pattern to match the [Person] name against
     */
    override fun findAbove18AndNameContains(pattern: String): List<Person> {
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

}