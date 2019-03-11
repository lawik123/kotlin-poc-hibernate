package nl.lawik.poc.hibernate.util

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * Wrapper for JPA [CriteriaBuilder], [CriteriaQuery] and [Root]
 */
data class QueryContext<T, X>(val builder: CriteriaBuilder, val criteriaQuery: CriteriaQuery<T>, val root: Root<X>)