package bin.joresserwe.jwt.jwt

import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(private val tokenProvider: TokenProvider) : Filter {

    private val log = KotlinLogging.logger { }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }


    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        val jwt = resolveToken(request as HttpServletRequest)
        val requestURI = request.requestURI

        if (!jwt.isNullOrEmpty() && tokenProvider.validateToken(jwt)) {
            val authentication = tokenProvider.getAuthentication(jwt)
            SecurityContextHolder.getContext().authentication = authentication
            log.debug { "Security Context에 '${authentication.name}' 인증 정보를 저장했습니다. uri : $requestURI" }
        } else {
            log.debug { "유효한 JWT 토큰이 없습니다. uri : $requestURI" }
        }

        chain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken: String? = request.getHeader(AUTHORIZATION_HEADER)
        log.debug { bearerToken }
        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

}
