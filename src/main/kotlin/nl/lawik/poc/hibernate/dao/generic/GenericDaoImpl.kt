package nl.lawik.poc.hibernate.dao.generic

import org.hibernate.ObjectNotFoundException
import org.hibernate.Session
import java.io.Serializable
import java.lang.Exception


open class GenericDaoImpl<T, ID : Serializable>(private val clazz: Class<T>, protected val session: Session) :
    GenericDao<T, ID> {

    override fun load(id: ID): T? {
        return try {
            session.load(clazz, id)
        } catch (e: Exception) {
            when (e) {
                is ObjectNotFoundException -> null
                else -> throw e
            }
        }
    }

    override fun multiLoad(vararg ids: ID, orderedReturn: Boolean): List<T> {
        val multi = session.byMultipleIds(clazz)
        multi.enableOrderedReturn(orderedReturn)
        return multi.multiLoad(*ids)
    }

    override fun multiLoad(ids: List<ID>, orderedReturn: Boolean): List<T> {
        val multi = session.byMultipleIds(clazz)
        multi.enableOrderedReturn(orderedReturn)
        return multi.multiLoad(ids)
    }

    override fun loadAll(firstResult: Int, maxResults: Int): List<T> {
        val criteriaBuilder = session.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(clazz)
        val root = criteriaQuery.from(clazz)
        criteriaQuery.select(root)

        val query = session.createQuery(criteriaQuery)
        query.firstResult = firstResult
        query.maxResults = maxResults
        return query.resultList
    }

    override fun count(): Long {
        val criteriaBuilder = session.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Long::class.java)
        val root = criteriaQuery.from(clazz)
        criteriaQuery.select(criteriaBuilder.countDistinct(root))

        val query = session.createQuery(criteriaQuery)
        return query.singleResult
    }

    @Suppress("UNCHECKED_CAST")
    override fun save(o: T): ID? {
        return try {
            val id = session.save(o) as ID?
            id
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveOrUpdate(o: T) {
        try {
            session.saveOrUpdate(o)
        } catch (e: Exception) {
            throw e
        }
    }


    override fun delete(o: T) {
        try {
            session.delete(o)
        } catch (e: Exception) {
            throw e
        }
    }
}