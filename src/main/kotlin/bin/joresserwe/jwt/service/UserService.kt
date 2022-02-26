package bin.joresserwe.jwt.service

import bin.joresserwe.jwt.dto.UserDto
import bin.joresserwe.jwt.entity.Authority
import bin.joresserwe.jwt.entity.User
import bin.joresserwe.jwt.repository.UserRepository
import bin.joresserwe.jwt.utility.SecurityUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*

@Service
class UserService
    (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signup(userDto: UserDto): User {
        getUserWithAuthorities(userDto.username)
            ?.let { throw RuntimeException("이미 가입되어있는 유저입니다.") }

        val authority = Authority("ROLE_USER")

        val user = User(
            userDto.username,
            passwordEncoder.encode(userDto.password),
            userDto.nickname,
            Collections.singleton(authority),
            true
        )

        return userRepository.save(user)
    }

    fun getUserWithAuthorities(username: String): User? {
        return userRepository.findOneWithAuthoritiesByUsername(username)
    }

    fun getMyUserWithAuthorities(): User? {
        return SecurityUtil.getCurrentUsername()?.let(::getUserWithAuthorities)
    }

}
