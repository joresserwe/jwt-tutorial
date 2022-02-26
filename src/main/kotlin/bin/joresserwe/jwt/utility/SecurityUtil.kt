package bin.joresserwe.jwt.utility

import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

class SecurityUtil {
    private val log = KotlinLogging.logger { }

    companion object {
        fun getCurrentUsername(): String? {
            val authentication = SecurityContextHolder.getContext().authentication

            var username: String? = null

            when (authentication.principal) {
                is UserDetails ->
                    username = (authentication.principal as UserDetails).username
                is String ->
                    username = authentication.principal as String
            }

            return username
        }
    }
}
