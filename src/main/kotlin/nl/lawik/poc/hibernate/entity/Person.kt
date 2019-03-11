package nl.lawik.poc.hibernate.entity

import javax.persistence.*

@Entity
@Table(name = "person")
data class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column
    val age: Int,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "person_id")
    @OrderBy
    val tasks: MutableList<Task> = mutableListOf()
) {

    /**
     * The toString method is overridden due to the bidirectional relationship between [Person] and [Task],
     * the default toString causes the toString methods of these classes to be called recursively, which results in a
     * StackOverflow error
     */
    override fun toString()= "Person(id=$id, name=$name)"

}