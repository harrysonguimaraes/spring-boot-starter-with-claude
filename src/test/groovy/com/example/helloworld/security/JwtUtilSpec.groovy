package com.example.helloworld.security

import spock.lang.Specification

class JwtUtilSpec extends Specification {

    JwtUtil jwtUtil = new JwtUtil()

    static final String TEST_SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLW11c3QtYmUtYXQtbGVhc3QtMzItYnl0ZXM="

    def setup() {
        jwtUtil.secret = TEST_SECRET
        jwtUtil.expirationMs = 86400000L
        jwtUtil.init()   // simulate @PostConstruct — builds the cached signingKey
    }

    def "generateToken should return a non-blank JWT"() {
        when:
        def token = jwtUtil.generateToken("user")

        then:
        token != null
        !token.isBlank()
        token.split("\\.").length == 3   // header.payload.signature
    }

    def "extractUsername should return the subject used to generate the token"() {
        given:
        def token = jwtUtil.generateToken("user")

        when:
        def username = jwtUtil.extractUsername(token)

        then:
        username == "user"
    }

    def "isValid should return true for a fresh token"() {
        given:
        def token = jwtUtil.generateToken("user")

        expect:
        jwtUtil.isValid(token)
    }

    def "isValid should return false for a tampered token"() {
        given:
        def token = jwtUtil.generateToken("user") + "tampered"

        expect:
        !jwtUtil.isValid(token)
    }
}
