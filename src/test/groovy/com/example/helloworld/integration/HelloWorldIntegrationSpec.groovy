package com.example.helloworld.integration

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension)
@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldIntegrationSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    def "GET /hello-world should return {'texto': 'hello world!'} end-to-end"() {
        when:
        def result = mockMvc.perform(get("/hello-world").accept(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath('$.texto').value("hello world!"))
    }
}
