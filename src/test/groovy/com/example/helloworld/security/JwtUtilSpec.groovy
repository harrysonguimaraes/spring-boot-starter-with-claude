package com.example.helloworld.security

import spock.lang.Specification

class JwtUtilSpec extends Specification {

    JwtUtil jwtUtil = new JwtUtil()

    def setup() {
        // Inject values that would normally come from application.yml
        jwtUtil.secret = "bXktc3VwZXItc2VjcmV0LWp3dC1zaWduaW5nLWtleS1mb3ItaHMyNTYtbXVzdC1iZS1hdC1sZWFzdC0zMi1ieXRlcw=="
        jwtUtil.expirationMs = 86400000L
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
