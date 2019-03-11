package nl.lawik.poc.hibernate.util

import javax.persistence.criteria.*
import kotlin.reflect.KProperty1

fun <T, X> QueryContext<T, X>.where(setup: WhereBuilder<T, X>.() -> Unit) {
    val whereBuilder = WhereBuilder(this)
    whereBuilder.setup()
    this.criteriaQuery.where(whereBuilder.build())
}

fun <T, X> QueryContext<T, X>.orderBy(setup: OrderByBuilder<T, X>.() -> Unit) {
    val orderByBuilder = OrderByBuilder(this)
    orderByBuilder.setup()
    this.criteriaQuery.orderBy(orderByBuilder.build())
}

fun <T, X> QueryContext<T, X>.select(selection: Selection<T>) {
    criteriaQuery.select(selection)
}

var QueryContext<*, *>.distinct: Boolean
    get() = criteriaQuery.isDistinct
    set(value) {
        criteriaQuery.distinct(value)
    }

fun <T, R> QueryContext<*, *>.join(prop: KProperty1<T, R?>): Join<T, R> = root.join<T, R>(prop.name)

class WhereBuilder<T, X>(queryContext: QueryContext<T, X>) : BaseQueryBuilder<T, X>(queryContext) {
    private val predicates = mutableListOf<Predicate>()

    // equal
    @JvmName("equalByProp")
    fun <R> KProperty1<X, R?>.equal(value: R) = equal(this, value)

    fun <R> equal(property: KProperty1<X, R?>, value: R) = equal(queryContext.root.get(property), value)

    fun <T> equal(expression: Expression<T>, value: T) = predicates.add(queryContext.builder.equal(expression, value))

    // number comparisons

    // le
    @JvmName("leByProp")
    fun KProperty1<X, Number?>.le(value: Number) = le(this, value)

    fun <R : Number> le(property: KProperty1<X, R?>, value: Number) = le(queryContext.root.get(property), value)

    fun le(expression: Expression<out Number>, value: Number) =
        predicates.add(queryContext.builder.le(expression, value))

    // lt
    @JvmName("ltByProp")
    fun KProperty1<X, Number?>.lt(value: Number) = lt(this, value)

    fun <R : Number> lt(property: KProperty1<X, R?>, value: Number) = lt(queryContext.root.get(property), value)

    fun lt(expression: Expression<out Number>, value: Number) =
        predicates.add(queryContext.builder.lt(expression, value))

    // ge
    @JvmName("geByProp")
    fun KProperty1<X, Number?>.ge(value: Number) = ge(this, value)

    fun <R : Number> ge(property: KProperty1<X, R?>, value: Number) = ge(queryContext.root.get(property), value)
    fun ge(expression: Expression<out Number>, value: Number) =
        predicates.add(queryContext.builder.ge(expression, value))

    // gt
    @JvmName("gtByProp")
    fun KProperty1<X, Number?>.gt(value: Number) = gt(this, value)

    fun <R : Number> gt(property: KProperty1<X, R?>, value: Number) = gt(queryContext.root.get(property), value)
    fun gt(expression: Expression<out Number>, value: Number) =
        predicates.add(queryContext.builder.gt(expression, value))

    // string comparisons

    // like
    @JvmName("likeByProp")
    fun KProperty1<X, String?>.like(value: String) =
        like(this, value)

    fun like(property: KProperty1<X, String?>, value: String) =
        like(queryContext.root.get(property), value)

    fun like(expression: Expression<String>, value: String) =
        predicates.add(queryContext.builder.like(expression, value))

    // notLike
    @JvmName("notLikeByProp")
    fun KProperty1<X, String?>.notLike(value: String) =
        notLike(this, value)

    fun notLike(property: KProperty1<X, String?>, value: String) =
        notLike(queryContext.root.get(property), value)

    fun notLike(expression: Expression<String>, value: String) =
        predicates.add(queryContext.builder.like(expression, value))

    fun or(setup: WhereBuilder<T, X>.() -> Unit) {
        val whereBuilder = WhereBuilder(queryContext)
        whereBuilder.setup()
        predicates.add(queryContext.builder.or(*whereBuilder.build().toTypedArray()))
    }

    fun and(setup: WhereBuilder<T, X>.() -> Unit) {
        val whereBuilder = WhereBuilder(queryContext)
        whereBuilder.setup()
        predicates.add(queryContext.builder.and(*whereBuilder.build().toTypedArray()))
    }

    fun predicate(predicate: Predicate) {
        predicates.add(predicate)
    }

    fun build(): List<Predicate> {
        return predicates
    }

}

class OrderByBuilder<T, X>(queryContext: QueryContext<T, X>) : BaseQueryBuilder<T, X>(queryContext) {
    private val orderByList = mutableListOf<Order>()

    fun asc(property: KProperty1<X, *>) = asc(queryContext.root.get(property))

    fun asc(expression: Expression<*>) = orderByList.add(queryContext.builder.asc(expression))

    fun desc(property: KProperty1<X, *>) = desc(queryContext.root.get(property))

    fun desc(expression: Expression<*>) = orderByList.add(queryContext.builder.desc(expression))

    fun build(): List<Order> {
        return orderByList
    }
}

/**
 * BaseQueryBuilder class which is extended by the [WhereBuilder] and [OrderByBuilder].
 * Used for providing the [QueryContext] to the other builders and restricting nesting of the builders
 * (See the [Deprecated] functions below)
 */
abstract class BaseQueryBuilder<T, X>(protected val queryContext: QueryContext<T, X>) {
    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Query builders can't be nested."
    )
    fun select(param: Selection<*>) {
    }

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Query builders can't be nested."
    )
    fun where(param: () -> Unit = {}) {
    }

    @Suppress("UNUSED_PARAMETER")
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Query builders can't be nested."
    )
    fun orderBy(param: () -> Unit = {}) {
    }
}