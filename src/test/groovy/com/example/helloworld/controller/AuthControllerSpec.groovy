package com.example.helloworld.controller

import com.example.helloworld.security.SecurityConfig
import com.example.helloworld.security.JwtUtil
import com.example.helloworld.controller.AuthController
import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension)
@WebMvcTest(AuthController)
@Import(SecurityConfig)
@TestPropertySource(properties = [
        "app.security.username=user",
        "app.security.password=password"
])
class AuthControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    AuthenticationManager authenticationManager = Stub()

    @SpringBean
    JwtUtil jwtUtil = Stub()

    def "POST /auth/login with valid credentials returns 200 and a token"() {
        given:
        jwtUtil.generateToken("user") >> "mocked.jwt.token"

        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content('{"username":"user","password":"password123"}'))

        then:
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$.token').value("mocked.jwt.token"))
    }

    def "POST /auth/login with wrong credentials returns 401"() {
        given:
        authenticationManager.authenticate(_) >> { throw new BadCredentialsException("bad") }

        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content('{"username":"user","password":"wrong"}'))

        then:
        result.andExpect(status().isUnauthorized())
              .andExpect(jsonPath('$.error').value("Invalid username or password"))
    }

    def "POST /auth/login with blank username returns 400"() {
        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content('{"username":"","password":"password123"}'))

        then:
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath('$.error').value("Validation failed"))
              .andExpect(jsonPath('$.details[0]').value("username is required"))
    }

    def "POST /auth/login with blank password returns 400"() {
        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content('{"username":"user","password":""}'))

        then:
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath('$.error').value("Validation failed"))
              .andExpect(jsonPath('$.details[0]').value("password is required"))
    }
}
