package com.example.helloworld.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension)
//"hey JUnit 5, use Spring to manage this test"
@SpringBootTest
//Full application context — every bean loaded
//Closest to production. Slow.
@AutoConfigureMockMvc
class HelloWorldIntegrationSpec extends Specification {

    @Autowired
    MockMvc mockMvc // ← Spring injects this because of SpringExtension

    @Autowired
    ObjectMapper objectMapper

    @Value('${app.security.username}')
    String username

    @Value('${app.security.password}')
    String password

    def "GET /hello-world with valid JWT returns 200"() {
        given: "a valid JWT obtained from login"
        def loginBody = """{"username":"$username","password":"$password"}"""
        def loginResult = mockMvc.perform(
                post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginBody))
                .andReturn()
        def token = objectMapper.readTree(loginResult.response.contentAsString).get("token").asText()

        when:
        def result = mockMvc.perform(
                get("/hello-world")
                        .header("Authorization", "Bearer $token")
                        .accept(APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
              .andExpect(content().contentType(APPLICATION_JSON))
              .andExpect(jsonPath('$.text').value("hello world!"))
    }

    def "GET /hello-world without JWT returns 401"() {
        when:
        def result = mockMvc.perform(get("/hello-world").accept(APPLICATION_JSON))

        then:
        result.andExpect(status().isUnauthorized())
    }
}
