package nl.lawik.poc.hibernate.criteria

data class PersonCriteria(
    var name: String? = null,
    var minAge: Int? = null,
    var maxAge: Int? = null,
    var taskNames: MutableList<String> = mutableListOf()
)