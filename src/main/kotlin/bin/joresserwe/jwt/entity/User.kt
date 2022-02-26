package bin.joresserwe.jwt.entity

import javax.persistence.*

@Entity
class User(
    @Column(length = 50, unique = true)
    val username: String,

    @Column(length = 100)
    val password: String,

    @Column(length = 50)
    val nickname: String,

    @ManyToMany
    @JoinTable(
        name = "user_authority_r",
        joinColumns = [JoinColumn(name = "userId")],
        inverseJoinColumns = [JoinColumn(name = "authorityName")]
    )
    val authorities: MutableSet<Authority> = HashSet(),

    val activated: Boolean,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0


}
