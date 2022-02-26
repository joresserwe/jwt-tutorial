package bin.joresserwe.jwt.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class LoginDto(
    @NotNull
    @Size(min = 3, max = 10)
    var username: String,

    @NotNull
    @Size(min = 3, max = 50)
    var password: String
)
