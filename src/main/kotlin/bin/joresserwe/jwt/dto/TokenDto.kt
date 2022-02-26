package bin.joresserwe.jwt.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class TokenDto(
    var token: String
)
