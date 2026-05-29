package com.example.helloworld.service

import com.example.helloworld.dto.HelloWorldResponse
import spock.lang.Specification

class HelloWorldServiceSpec extends Specification {

    HelloWorldService service = new HelloWorldService()

    def "should return a HelloWorldResponse with 'hello world!' as text"() {
        when:
        HelloWorldResponse response = service.getHelloWorld()

        then:
        response != null
        response.text() == "hello world!"
    }
}
