package nl.lawik.poc.hibernate.entity

import javax.persistence.*

@Entity
@Table(name = "task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @ManyToOne
    @JoinColumn(name = "person_id")
    val person: Person? = null
)