# 🍔 Delivery API

API REST criada em Java com Spring Boot para gerenciamento completo de um ecossistema de delivery de comida.

A aplicação suporta cadastro e autenticação de usuários, gestão de clientes, restaurantes, produtos e pedidos, além de oferecer relatórios financeiros, métricas e documentação OpenAPI.

---

## 🚀 Visão Geral

A `Delivery API` é uma plataforma backend projetada para:

- conectar clientes a restaurantes de forma segura;
- expor operações CRUD para clientes, restaurantes e produtos;
- processar pedidos com fluxos de aprovação, preparação e entrega;
- gerar relatórios de faturamento, estatísticas de status e ranking de produtos;
- fornecer observabilidade com Actuator, Prometheus e tracing.

---

## ⚙️ Funcionalidades principais

### Autenticação e autorização

- Registro de usuário com bloqueio de criação de `ADMIN` via API.
- Login com geração de token JWT.
- Proteção de endpoints por perfis `ADMIN`, `RESTAURANTE` e `CLIENTE`.
- Acesso público apenas para login, cadastro, swagger e métricas.

### Gestão de clientes

- Cadastro e atualização de perfil do cliente.
- Busca por ID e listagem paginada de clientes ativos.
- Soft delete via alteração de status ativo/inativo.
- Exclusão permanente apenas para administradores.

### Gestão de restaurantes

- Criação de restaurante por usuário autorizado.
- Listagem paginada de restaurantes ativos.
- Busca por nome, categoria e ranking de avaliação.
- Atualização de dados cadastrais.
- Ativação/desativação do restaurante.
- Cálculo de taxa de entrega por CEP.
- Pesquisa de restaurantes próximos com base no CEP.

### Gestão de produtos

- Cadastro de produtos vinculados a restaurantes.
- Listagem de produtos ativos globalmente.
- Consulta por ID, restaurante e categoria.
- Alternância de disponibilidade de itens.
- Atualização e exclusão de produtos.

### Processamento de pedidos

- Criação de pedidos com múltiplos itens.
- Cálculo automático de total e subtotais.
- Consulta de pedido por ID.
- Confirmação de pedido pelo restaurante.
- Avanço do status de entrega.
- Cancelamento controlado por regras de negócio.
- Histórico de pedidos do cliente autenticado.

### Relatórios e métricas

- Faturamento total em um intervalo de tempo.
- Estatísticas de pedidos por status.
- Ranking de produtos mais vendidos.
- Health check customizado.
- Métricas Prometheus e endpoints Actuator expostos.
- Documentação interativa via Swagger/OpenAPI.

---

## 🧩 Tecnologias utilizadas

- Java 21
- Spring Boot 3.5.13-SNAPSHOT
- Spring Web MVC
- Spring Security
- JWT (JSON Web Tokens)
- Spring Data JPA
- Hibernate
- H2 Database (in-memory)
- Spring Validation
- Spring Boot Actuator
- SpringDoc OpenAPI
- Micrometer + Prometheus
- Zipkin / Brave tracing
- ModelMapper
- Lombok
- Maven
- Mockito

---

## 📚 Arquitetura do projeto

O código é organizado em camadas:

- `controller` — Endpoints REST
- `service` — Regras de negócio e fluxo de pedidos
- `repository` — Acesso a dados com Spring Data JPA
- `model` — Entidades JPA e relacionamentos
- `dto` — Objetos de transferência de dados
- `config` — Configurações de segurança, OpenAPI e servidor
- `security` — Filtros JWT e utilitários de autenticação
- `validation` — Validação customizada de requisições

---

## 🧭 Endpoints principais

### Autenticação

- `POST /api/auth/register` — Registrar novo usuário
- `POST /api/auth/login` — Autenticar e obter token JWT

### Clientes

- `POST /api/clientes/cadastrar` — Cadastrar cliente (ADMIN)
- `GET /api/clientes` — Listar clientes ativos (ADMIN)
- `GET /api/clientes/{id}` — Buscar cliente por ID
- `PUT /api/clientes/{id}` — Atualizar cliente
- `PATCH /api/clientes/{id}/status` — Alternar status ativo/inativo (ADMIN)
- `DELETE /api/clientes/{id}` — Excluir cliente (ADMIN)

### Restaurantes

- `POST /api/restaurantes` — Cadastrar restaurante (RESTAURANTE, ADMIN)
- `GET /api/restaurantes/listar` — Listar restaurantes ativos
- `GET /api/restaurantes/{id}/buscar-restaurante-por-id` — Buscar restaurante por ID
- `GET /api/restaurantes/pesquisar/{nome}` — Buscar por nome
- `GET /api/restaurantes/categoria?categoria=nome` — Filtrar por categoria
- `GET /api/restaurantes/ranking` — Ranking de restaurantes
- `PATCH /api/restaurantes/{id}/toggle` — Alternar operação do restaurante
- `PUT /api/restaurantes/{id}` — Atualizar restaurante
- `GET /api/restaurantes/{id}/taxa-entrega/{cep}` — Calcular taxa de entrega
- `GET /api/restaurantes/proximos/{cep}` — Buscar restaurantes próximos por CEP
- `DELETE /api/restaurantes/{id}` — Excluir restaurante (ADMIN)

### Produtos

- `POST /api/produtos/restaurante/{restauranteId}` — Cadastrar produto (RESTAURANTE)
- `GET /api/produtos` — Listar produtos ativos (RESTAURANTE)
- `GET /api/produtos/{id}` — Buscar produto por ID
- `GET /api/produtos/restaurante/{restauranteId}` — Listar produtos por restaurante
- `GET /api/produtos/categoria/{categoria}` — Filtrar por categoria
- `PATCH /api/produtos/{id}/disponibilidade` — Alternar disponibilidade
- `PUT /api/produtos/{id}` — Atualizar produto
- `DELETE /api/produtos/{id}` — Excluir produto

### Pedidos

- `POST /api/pedidos` — Criar pedido (CLIENTE)
- `GET /api/pedidos/{id}` — Buscar pedido por ID
- `PUT /api/pedidos/{id}/confirmar` — Confirmar pedido (RESTAURANTE)
- `PATCH /api/pedidos/{id}/status/avancar` — Avançar status do pedido (RESTAURANTE)
- `PATCH /api/pedidos/{id}/cancelar` — Cancelar pedido
- `GET /api/pedidos/meus` — Listar pedidos do usuário autenticado

### Relatórios e observabilidade

- `GET /api/relatorios/faturamento` — Faturamento por período
- `GET /api/relatorios/estatisticas` — Contagem por status
- `GET /api/relatorios/ranking-produtos` — Ranking de produtos mais vendidos
- `GET /custom-health` — Health check customizado
- `GET /custom-info` — Informações da aplicação
- `GET /actuator/health` — Actuator health
- `GET /actuator/prometheus` — Métricas Prometheus
- `GET /swagger-ui.html` — Documentação OpenAPI

---

## 🛠️ Executar localmente

### Requisitos

- Java 21+
- Maven 3.6+

### Passo a passo

```bash
git clone https://github.com/Richard15151/delivery-api-Richard.git
cd delivery-api
./mvnw spring-boot:run
```

### Acessos

- API: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Console H2: `http://localhost:8081/h2-console`
  - JDBC URL: `jdbc:h2:mem:deliverydb`
  - Username: `sa`
  - Password: vazio

---

## ✅ Testes

Executar suíte de testes:

```bash
./mvnw test
```

---

## 💡 Observações importantes

- O banco de dados H2 é configurado como `create-drop` para desenvolvimento.
- A aplicação roda na porta `8081`.
- O cache de listagens está habilitado em alguns endpoints para maior performance.
- A documentação OpenAPI suporta testes dos endpoints diretamente no navegador.

---

## 🤝 Contribuindo

1. Faça um fork do repositório.
2. Crie uma branch para sua feature.
3. Adicione testes e documentação.
4. Abra um Pull Request.

---

## 📄 Licença

Projeto distribuído sob a licença MIT.
