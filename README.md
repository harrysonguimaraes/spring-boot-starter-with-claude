# hello-world-spring-claude

Este projeto é uma API REST minimalista desenvolvida com **Spring Boot**, criada como projeto de referência para demonstrar uma estrutura bem organizada com boas práticas desde o primeiro commit. O endpoint principal, `GET /hello-world`, retorna um simples JSON `{"texto": "hello world!"}` e serve como ponto de partida para qualquer API Spring Boot construída sobre essa base.

## Stack

- **Java 17** com **Spring Boot 3.2.5** e build via **Gradle**
- **Spring Web** para exposição dos endpoints REST
- **Spock Framework 2.4-M4** (Groovy 4) como framework de testes — substituindo completamente o JUnit — com testes escritos em estilo BDD (`given / when / then`)
- Configuração via `application.yml`

## Arquitetura

O projeto segue uma separação de responsabilidades em camadas: o `HelloWorldController` (`@RestController`) recebe as requisições HTTP e delega a lógica de negócio ao `HelloWorldService` (`@Service`), que constrói e retorna o DTO de resposta (`HelloWorldResponse`). Essa separação, mesmo num projeto simples, garante que controller e serviço possam ser testados de forma independente.

## Testes

A cobertura é feita por três Spock Specifications: um **teste unitário** do `@Service` instanciado diretamente (sem contexto Spring), um **teste de slice** do `@RestController` usando `@WebMvcTest` com stub via `@SpringBean`, e um **teste de integração** com `@SpringBootTest + @AutoConfigureMockMvc` que sobe o contexto completo e valida o endpoint de ponta a ponta. Todos os testes passam com `./gradlew test`.

## Replicando o projeto

Este projeto foi inteiramente gerado com o auxílio do **Claude**. O prompt utilizado para reproduzi-lo do zero está disponível em [`prompts/prompts.md`](./prompts/prompts.md) — basta utilizá-lo em uma nova sessão para obter a mesma estrutura, stack e cobertura de testes.
