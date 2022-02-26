package bin.joresserwe.jwt.config

import bin.joresserwe.jwt.jwt.TokenProvider
import bin.joresserwe.jwt.jwt.JwtAccessDeniedHandler
import bin.joresserwe.jwt.jwt.JwtAuthenticationEntryPoint
import bin.joresserwe.jwt.jwt.JwtSecurityConfig
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * EnableWebSecurity :: 기본적인 Web 보안을 활성화 하겠다.
 * EnableGlobalMethodSecurity :: @PreAuthorize를 Method 단위로 사용하기 위한 Annotation
 *
 * 추가적인 설정을 위해 WebSecurityConfigurer를 Implements 하거나
 * WebSecurityConfigurerAdapter를 Extends 하는 방법이 있다.
 * 지금은 후자를 이용
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()


    override fun configure(web: WebSecurity) {
        web.ignoring()
            .antMatchers(
                "/h2-console/**",
                "/favicon.ico"
            )
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable() // token 방식을 이용해서 csrf를 disable

            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)

            .and()
            .headers()
            .frameOptions()
            .sameOrigin()

            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근 제한
            .antMatchers("/api/hello").permitAll()   // 해당 주소의 접근을 허용하고
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/signup").permitAll()
            .anyRequest().authenticated()  // 나머지는 인증을 받겠다.

            .and()
            .apply(JwtSecurityConfig(tokenProvider))

    }
}
