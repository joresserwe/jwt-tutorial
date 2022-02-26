package bin.joresserwe.jwt.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
class Authority(
    @Id
    @Column(length = 50)
    val authorityName: String
)
