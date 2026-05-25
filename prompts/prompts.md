
Crie um novo projeto Spring Boot com as seguintes especificações:

Estrutura e build

Gradle + Java 17, usando as versões estáveis mais recentes de todas as dependências (Spring Boot, Spock, Groovy, plugins)
Arquivo de configuração: application.yml (nunca .properties)
Classe principal: Main.java (nunca Application.java)
Componentes obrigatórios

Um @RestController com endpoint GET /hello-world retornando {"texto": "hello world!"} em JSON
Um @Service injetado no controller, responsável pela lógica de negócio
Um DTO/record para a resposta
Testes — usar Spock (nunca JUnit)

src/test/groovy/, classes estendem Specification, blocos given/when/then
Teste unitário para o @Service (sem Spring, instância direta)
Teste de slice para o @RestController com @WebMvcTest + @SpringBean stub
Teste de integração com @SpringBootTest + @AutoConfigureMockMvc
Obs: usar spock-spring 2.4-M4+ e anotar specs Spring com @ExtendWith(SpringExtension)
Arquivos raiz

.gitignore ignorando: .claude/, .gradle/, .idea/, build/, .env
Arquivo .env vazio (git-ignorado)
Critério de conclusão: todos os testes devem passar com ./gradlew test antes de encerrar.
