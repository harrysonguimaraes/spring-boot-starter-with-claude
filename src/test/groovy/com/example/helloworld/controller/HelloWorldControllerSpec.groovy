package com.example.helloworld.controller

import com.example.helloworld.dto.HelloWorldResponse
import com.example.helloworld.security.JwtUtil
import com.example.helloworld.service.HelloWorldService
import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension)
@WebMvcTest(HelloWorldController)
class HelloWorldControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    HelloWorldService helloWorldService = Stub()

    @SpringBean
    JwtUtil jwtUtil = Stub()

    def "GET /hello-world with authenticated user returns 200"() {
        given:
        helloWorldService.getHelloWorld() >> new HelloWorldResponse("hello world!")

        when:
        def result = mockMvc.perform(
                get("/hello-world")
                        .with(user("user").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath('$.texto').value("hello world!"))
    }

    def "GET /hello-world without authentication returns 401"() {
        when:
        def result = mockMvc.perform(get("/hello-world").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isUnauthorized())
    }
}
