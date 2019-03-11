package nl.lawik.poc.hibernate

import nl.lawik.poc.hibernate.criteria.PersonCriteria
import nl.lawik.poc.hibernate.dao.generic.GenericDaoImpl
import nl.lawik.poc.hibernate.dao.person.PersonDaoImpl
import nl.lawik.poc.hibernate.entity.Person
import nl.lawik.poc.hibernate.util.openAndCloseSession
import java.time.LocalDateTime

fun main(args: Array<String>) {

    // generic dao
    openAndCloseSession {
        val genericDaoImpl = GenericDaoImpl<Person, Long>(Person::class.java, it)
        println("Number of persons: ${genericDaoImpl.count()}\n")
        println("Person with ID 2: ${genericDaoImpl.load(2L)}\n")
        println("Person with ID 1: ${genericDaoImpl.load(1L) ?: "person not found"}\n")
        println("All persons: ${genericDaoImpl.loadAll()}\n")
        println("Multiload using vararg: ${genericDaoImpl.multiLoad(1L, 2L, 3L, 4L)}\n")
        println(
            "Multiload using array and no orderedReturn: ${genericDaoImpl.multiLoad(
                *arrayOf(1L, 2L, 3L, 4L),
                orderedReturn = false
            )}\n"
        )
        println("Multiload using list: ${genericDaoImpl.multiLoad(listOf(1L, 2L, 3L, 4L))}\n")

        val insertedPersonId = genericDaoImpl.save(Person(null, "new person ${LocalDateTime.now()}", 20))!!
        println("Inserted person ID: $insertedPersonId\n")
        val insertedPerson = genericDaoImpl.load(insertedPersonId)!!
        println("Inserted person($insertedPersonId): $insertedPerson\n")

        insertedPerson.name = "updated person ${LocalDateTime.now()}"
        genericDaoImpl.saveOrUpdate(insertedPerson)
        println("Updated person($insertedPersonId): ${genericDaoImpl.load(insertedPersonId)}\n")

        genericDaoImpl.delete(insertedPerson)
        println(
            "Deleted person ($insertedPersonId): ${genericDaoImpl.load(insertedPersonId)
                ?: "person with id $insertedPersonId not found"}\n"
        )
    }

    // person dao
    findPersonsByCriteria()

}

fun findPersonsByCriteria() {
    openAndCloseSession {
        val personDaoImpl = PersonDaoImpl(it)
        val personCriteria = PersonCriteria()

        personCriteria.apply {
            minAge = 18
            maxAge = 25
            name = "test"
            taskNames.add("test")
            taskNames.add("test2")
        }

        println("find:")
        val persons = personDaoImpl.find(personCriteria)
        println(persons)

        println("\nfind2:")
        val persons2 = personDaoImpl.find2(personCriteria)
        println(persons2)

        println("\nfindAbove18AndNameContains:")
        val persons3 = personDaoImpl.findAbove18AndNameContains("te")
        println(persons3)
    }
}







