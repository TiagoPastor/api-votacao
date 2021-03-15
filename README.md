# API de votação

API REST para votação em sessão

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você deve criar uma solução backend para gerenciar essas sessões de votação.

Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST:


 Objetivos
  - Cadastrar uma nova pauta;
  - Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por um tempo
determinado na chamada de abertura ou 1 minuto por default);
  - Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado é
identificado por um id único e pode votar apenas uma vez por pauta);
  - Contabilizar os votos e dar o resultado da votação na pauta.
### Stack utilizada

* Java 11
* Spring Boot Web; JPA; Data;
* Maven
* JUnit
* Swagger 2
* MySql
* Lombok

### Instalação

```sh
$ git clone https://github.com/TiagoPastor/api-votacao.git
$ cd api-votacao
$ mvn package
$ cd target
$ java -jar api_votacao.jar
```

### Sprint Boot Version

```
Pode importar como projeto mavem existente no Spring Tool Suite 4 que foi a versão usada para desenvolver
```

#### Swagger
Desenvolvimento:
```
http://localhost:8080/swagger-ui.html
```
### Arquivo de Collection Json - Usar no Postman

```
  - O arquivo de Collection Json esta na pasta Docs. Pode copiar ele e importar no postman para testar os payloads da api
```

### Tarefa Bônus 1 - Integração com sistemas externos

```
Foi implementado um serviço 'ValidaCPF' no qual é realizado a integração com um serviço externo: https://user-info.herokuapp.com/users/{cpf}

```

### Tarefa Bônus 4 - Versionamento da API

```
O versionamento foi feito pela url, onde a api foi construida como api/v1.

```

