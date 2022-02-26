package bin.joresserwe.jwt.controller

import bin.joresserwe.jwt.dto.LoginDto
import bin.joresserwe.jwt.dto.TokenDto
import bin.joresserwe.jwt.jwt.JwtFilter
import bin.joresserwe.jwt.jwt.TokenProvider
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class AuthController(
    private val tokenProvider: TokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder
) {

    @PostMapping("/authenticate")
    fun authorize(@Valid @RequestBody loginDto: LoginDto): ResponseEntity<TokenDto> {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.username, loginDto.password)

        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication

        val jwt = tokenProvider.createToken(authentication)

        val httpHeaders = HttpHeaders().apply { this[JwtFilter.AUTHORIZATION_HEADER] = "Bearer $jwt" }

        return ResponseEntity(TokenDto(jwt), httpHeaders, HttpStatus.OK)
    }

}
