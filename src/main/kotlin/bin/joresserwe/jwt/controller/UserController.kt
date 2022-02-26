package bin.joresserwe.jwt.controller

import bin.joresserwe.jwt.dto.UserDto
import bin.joresserwe.jwt.entity.User
import bin.joresserwe.jwt.service.UserService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api")
class UserController(
    private val userService: UserService
) {

    private val log = KotlinLogging.logger { }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody userDto: UserDto) = userService.signup(userDto)

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    fun getMyUserInfo() = userService.getMyUserWithAuthorities()

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun getUserInfo(@PathVariable username: String): User? {
        return userService.getUserWithAuthorities(username)
    }
}
