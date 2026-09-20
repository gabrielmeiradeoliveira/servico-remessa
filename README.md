# Serviço de Remessas

API REST para cadastro de usuários, carteiras e remessas em reais com conversão para dólar pela cotação PTAX do Banco Central do Brasil.

## Requisitos

- Java 21
- Maven 3.9+ (ou Maven Wrapper incluído no projeto)

## Executar

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação fica disponível em `http://localhost:8080`.

## Docker

Para compilar e executar a aplicação em segundo plano:

```bash
docker compose up -d --build
```

Para encerrar os containers:

```bash
docker compose down
```

Depois da inicialização, a API estará em `http://localhost:8080`; Swagger e H2 permanecem disponíveis nos mesmos endereços descritos abaixo.

## Kubernetes

Os manifestos mínimos estão em `k8s/`. Eles criam um `Deployment` com uma réplica, um `Service` interno e um `Secret` para as credenciais do H2.

```bash
docker build -t servico-remessa:latest .
kubectl apply -f k8s/
kubectl port-forward service/servico-remessa 8080:80
```

A imagem `servico-remessa:latest` deve estar disponível para os nós do cluster. Em um cluster remoto, publique a imagem em um registry e altere `image` em `k8s/deployment.yml`. Também substitua os valores de exemplo em `k8s/secret.yml` por um Secret gerenciado fora do Git.

O H2 é em memória; por isso, mantenha apenas uma réplica e não use essa configuração para dados persistentes.

## Compilar e testar

```bash
./mvnw verify
```

O comando executa os testes unitários e os testes de integração com H2. Para aplicar o formatador configurado no projeto:

```bash
./mvnw spotless:apply
```

## Ferramentas locais

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:remessa`
  - usuário e senha: valores configurados em `src/main/resources/application.properties`

## Endpoints

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/usuarios` | Cria um usuário e sua carteira com saldos zerados. |
| `GET` | `/usuarios` | Lista os usuários. |
| `GET` | `/usuarios/{id}` | Busca um usuário por ID. |
| `GET` | `/usuarios/email/{email}` | Busca um usuário por e-mail. |
| `GET` | `/usuarios/cpf/{cpf}` | Busca uma pessoa física por CPF. |
| `GET` | `/usuarios/cnpj/{cnpj}` | Busca uma pessoa jurídica por CNPJ. |
| `PUT` | `/usuarios/{id}` | Atualiza os dados de um usuário. |
| `DELETE` | `/usuarios/{id}` | Exclui um usuário sem remessas associadas. |
| `POST` | `/carteiras/usuario/{usuarioId}` | Cria uma carteira manualmente; para usuários novos, ela já é criada no cadastro. |
| `GET` | `/carteiras/usuario/{usuarioId}` | Consulta os saldos da carteira. |
| `POST` | `/carteiras/usuario/{usuarioId}/creditos` | Credita saldo em BRL. |
| `POST` | `/transferencias` | Converte BRL em USD e realiza a remessa. |

## Fluxo de uso

1. Crie os usuários em `POST /usuarios`. A carteira é criada automaticamente com saldo zero em BRL e USD.
2. Credite o saldo de quem enviará a remessa com `POST /carteiras/usuario/{usuarioId}/creditos`.
3. Crie a remessa em `POST /transferencias`.

O enunciado exige validação de saldo, mas não define como ele é disponibilizado. Por isso, o crédito é uma operação auxiliar explícita e separada da remessa; a aplicação não atribui saldo inicial arbitrário. Em um ambiente produtivo, esse endpoint exigiria autenticação e autorização.

Exemplo de crédito:

```http
POST /carteiras/usuario/1/creditos
Content-Type: application/json

{
  "valorReal": 1000.00
}
```

Exemplo de remessa:

```http
POST /transferencias
Content-Type: application/json

{
  "remetenteId": 1,
  "destinatarioId": 2,
  "valorReal": 250.00
}
```

A remessa debita o valor em BRL da carteira do remetente, credita o valor convertido em USD na carteira do destinatário e grava a operação de forma transacional.

## Regras de negócio

- O remetente e o destinatário devem ser diferentes.
- O remetente precisa ter saldo suficiente em BRL.
- O limite diário é de R$ 10.000,00 para pessoa física e R$ 50.000,00 para pessoa jurídica.
- A cotação de compra do dólar vem do serviço PTAX do [Banco Central do Brasil](https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/). Quando não há cotação no dia solicitado, a aplicação consulta até os sete dias anteriores.
- Usuários com remessas associadas não podem ser excluídos, preservando o histórico financeiro.
