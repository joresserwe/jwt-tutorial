package bin.joresserwe.jwt.service

import bin.joresserwe.jwt.entity.User
import bin.joresserwe.jwt.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("userDetailService")
class CustomUserDetailService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findOneWithAuthoritiesByUsername(username)?.let { createUser(username, it) }
            ?: throw UsernameNotFoundException("$username -> 데이터베이스에서 찾을 수 없습니다.")
    }

    private fun createUser(username: String, user: User): org.springframework.security.core.userdetails.User {
        if (!user.activated) throw RuntimeException("$username -> 활성화 되어있지 않습니다.")

        val grantedAuthorities = user.authorities.map { SimpleGrantedAuthority(it.authorityName) }.toList()

        return org.springframework.security.core.userdetails.User(user.username, user.password, grantedAuthorities)

    }
}
