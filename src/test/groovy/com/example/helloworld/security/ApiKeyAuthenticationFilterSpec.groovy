package com.example.helloworld.security

import com.example.helloworld.controller.HelloWorldController
import com.example.helloworld.service.HelloWorldService
import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension)
@WebMvcTest(HelloWorldController)
@Import(SecurityConfig)
@TestPropertySource(properties = [
        "app.security.username=user",
        "app.security.password=password",
        "app.security.api-key=test-api-key"
])
class ApiKeyAuthenticationFilterSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    HelloWorldService helloWorldService = Stub()

    @SpringBean
    JwtUtil jwtUtil = Stub()

    def "GET /hello-world with valid API key returns 200"() {
        when:
        def result = mockMvc.perform(get("/hello-world")
                .header(ApiKeyAuthenticationFilter.API_KEY_HEADER, "test-api-key")
                .accept(APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
    }

    def "GET /hello-world with invalid API key returns 401"() {
        when:
        def result = mockMvc.perform(get("/hello-world")
                .header(ApiKeyAuthenticationFilter.API_KEY_HEADER, "wrong-key")
                .accept(APPLICATION_JSON))

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "GET /hello-world without any credential returns 401"() {
        when:
        def result = mockMvc.perform(get("/hello-world").accept(APPLICATION_JSON))

        then:
        result.andExpect(status().isUnauthorized())
    }
}
