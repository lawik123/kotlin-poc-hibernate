package nl.lawik.poc.hibernate.util

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.query.Query
import java.lang.Exception
import javax.persistence.criteria.*
import kotlin.reflect.KProperty1


object HibernateUtil {
    val sessionFactory: SessionFactory by lazy {
        val standardRegistry = StandardServiceRegistryBuilder().configure().build()
        val metaData = MetadataSources(standardRegistry).metadataBuilder.build()
        metaData.sessionFactoryBuilder.build()
    }
}

/**
 * Helper for creating a session scope and provides it to the provided function literal.
 * The session is closed after the function literal is processed
 * the result from the function literal is returned.
 * Starts and commits a transaction for the session scope if the handler is processed successfully, rollsback
 * the transaction if an exception occurs.
 *
 * @param handler function literal that makes use of the session, you can access the session using the "it" keyword
 * in the function literal.
 *
 * @return the result of the function literal
 */
fun <T> openAndCloseSession(handler: (Session) -> T): T {
    val session = HibernateUtil.sessionFactory.openSession()
    session.beginTransaction()
    return try {
        val handlerResult = handler(session)
        session.transaction.commit()
        handlerResult
    } catch (e: Exception) {
        session.transaction.rollback()
        throw e
    } finally {
        if (session.isOpen) {
            session.close()
        } else {
            println("[WARNING]: session closed by handler function: ${handler::class.java.enclosingMethod}, please consider omitting manually closing the session in the handler as it will be closed automatically at the end of the call")
        }
    }
}

// extension functions

fun <T> CriteriaQuery<T>.where(predicates: List<Predicate>): CriteriaQuery<T> {
    return this.where(*predicates.toTypedArray())
}

fun <R> Path<*>.get(prop: KProperty1<*, R?>): Path<R> = this.get<R>(prop.name)

/**
 * Function for creating a [Query] based on the provided Type parameters.
 *
 * @param T [CriteriaQuery] type (result type)
 * @param X [Root] type
 */
inline fun <reified T, reified X> Session.createQuery(
    init: QueryContext<T, X>.() -> Unit
): Query<T> {
    val builder = this.criteriaBuilder
    val criteriaQuery = builder.createQuery(T::class.java)
    val root = criteriaQuery.from(X::class.java)
    val context = QueryContext<T, X>(builder, criteriaQuery, root)
    context.init()
    return createQuery(criteriaQuery)
}

/**
 * Helper function for [createQuery] which creates a [Query]
 * used when both your [CriteriaQuery] and [Root] are of the same type
 *
 *@param T [CriteriaQuery] and [Root] type
 */
@JvmName("createQueryFromSingleType")
inline fun <reified T> Session.createQuery(
    init: QueryContext<T, T>.() -> Unit
): Query<T> {
    return this.createQuery<T, T>(init)
}









