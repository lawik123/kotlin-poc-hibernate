package nl.lawik.poc.hibernate.dao.person

import nl.lawik.poc.hibernate.criteria.PersonCriteria
import nl.lawik.poc.hibernate.dao.generic.GenericDao
import nl.lawik.poc.hibernate.entity.Person

interface PersonDao: GenericDao<Person, Long> {
    fun find(personCriteria: PersonCriteria): List<Person>
    fun find2 (personCriteria: PersonCriteria): List<Person>
    fun findAbove18AndNameContains(pattern: String): List<Person>
}