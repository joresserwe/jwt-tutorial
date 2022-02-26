package bin.joresserwe.jwt.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.sql.Timestamp
import java.time.LocalDateTime

@Component
class TokenProvider(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.token-validity-in-seconds}")
    tokenValidityInSeconds: Long
) {

    private val tokenValidityInSeconds: Long
    private val key: Key
    private val log = KotlinLogging.logger {}

    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }

    init {
        log.debug { "initMethod!" }
        this.tokenValidityInSeconds = tokenValidityInSeconds
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
        log.debug { this.key.toString() }
    }

    fun createToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",", transform = GrantedAuthority::getAuthority)

        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusSeconds(tokenValidityInSeconds)))
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val authorities = claims[AUTHORITIES_KEY].toString().split(",").map(::SimpleGrantedAuthority).toList()

        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String): Boolean {
        return runCatching {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        }.onFailure {
            when (it) {
                is SecurityException, is MalformedJwtException ->
                    log.info { "잘못된 JWT 서명입니다." }
                is ExpiredJwtException ->
                    log.info { "만료된 JWT 토큰입니다." }
                is UnsupportedJwtException ->
                    log.info { "지원되지 않는 JWT 토큰입니다." }
                is IllegalArgumentException ->
                    log.info { "JWT 토큰이 잘못되었습니다." }
            }
        }.isSuccess
    }

}
