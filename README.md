# 🍔 Delivery API

API REST desenvolvida em Java com Spring Boot para gerenciamento de um sistema de delivery de comida. Permite o cadastro e gerenciamento de clientes, restaurantes, produtos e pedidos, além de fornecer relatórios e estatísticas para análise de vendas.

## 📋 Descrição do Projeto

Esta API foi criada para facilitar o gerenciamento completo de um serviço de delivery, conectando clientes a restaurantes através de pedidos online. O sistema suporta operações CRUD para todas as entidades principais e inclui funcionalidades avançadas como relatórios de faturamento, ranking de produtos mais vendidos e controle de status de pedidos.

## 🚀 Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.13-SNAPSHOT** - Framework para desenvolvimento de aplicações Java
- **Spring Web** - Para criação de APIs REST
- **Spring Data JPA** - Para persistência de dados
- **Hibernate** - ORM para mapeamento objeto-relacional
- **H2 Database** - Banco de dados em memória para desenvolvimento
- **Lombok** - Para redução de código boilerplate
- **Maven** - Gerenciamento de dependências e build
- **Spring Boot DevTools** - Para desenvolvimento

## 📦 Funcionalidades Principais

### 👥 Gerenciamento de Clientes
- Cadastro de novos clientes
- Listagem de clientes ativos
- Busca por ID
- Atualização de dados
- Ativação/desativação de clientes
- Exclusão de clientes

### 🏪 Gerenciamento de Restaurantes
- Cadastro de restaurantes
- Listagem com ranking por avaliação
- Busca por nome e categoria
- Atualização de dados
- Ativação/desativação
- Exclusão

### 🍕 Gerenciamento de Produtos
- Cadastro de produtos por restaurante
- Listagem de produtos disponíveis
- Busca por restaurante e categoria
- Atualização de dados
- Controle de disponibilidade
- Exclusão

### 📋 Gerenciamento de Pedidos
- Criação de pedidos com múltiplos itens
- Cálculo automático de valores e subtotais
- Geração automática de número do pedido
- Atualização de status (Pendente → Preparando → Saiu para Entrega → Entregue/Cancelado)
- Listagem por cliente, status e período
- Relatórios de faturamento
- Estatísticas por status
- Ranking de produtos mais vendidos

### 🏥 Health Check
- Endpoint para verificação de saúde da aplicação
- Informações sobre status, timestamp e versão Java

## 🏗️ Arquitetura e Estrutura

O projeto segue a arquitetura em camadas típica de aplicações Spring Boot:

```
src/main/java/com/deliverytech/delivery_api/
├── controller/     # Controladores REST
├── model/         # Entidades JPA
├── repository/    # Interfaces de repositório
├── service/       # Lógica de negócio
└── DeliveryApiApplication.java
```

### Entidades Principais

- **Cliente**: Dados pessoais e status
- **Restaurante**: Informações do estabelecimento e avaliação
- **Produto**: Itens do menu com preço e categoria
- **Pedido**: Ordem de compra com itens e status
- **ItemPedido**: Itens individuais do pedido com quantidade e subtotal

## 🔧 Configuração e Execução

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6+

### Como executar

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/Richard15151/delivery-api-Richard.git
   cd delivery-api
   ```

2. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

   Ou através da IDE:
   - Execute a classe `DeliveryApiApplication.java`

3. **Acesse a aplicação:**
   - API: http://localhost:8081
   - Console H2: http://localhost:8081/h2-console
     - JDBC URL: `jdbc:h2:mem:deliverydb`
     - Username: `sa`
     - Password: (vazio)

## 📚 Endpoints da API

### Clientes
- `POST /clientes/cadastrar` - Cadastrar cliente
- `GET /clientes` - Listar clientes ativos
- `GET /clientes/{id}` - Buscar cliente por ID
- `PUT /clientes/{id}/atualizar-dados-clientes` - Atualizar cliente
- `PATCH /clientes/{id}/status` - Alternar status ativo/inativo
- `DELETE /clientes/{id}/deletar-cliente` - Deletar cliente

### Restaurantes
- `POST /restaurantes/cadastrar` - Cadastrar restaurante
- `GET /restaurantes` - Listar restaurantes (ranking)
- `GET /restaurantes/{id}` - Buscar restaurante por ID
- `GET /restaurantes/pesquisar/{nome}` - Buscar por nome
- `GET /restaurantes/categoria/{categoria}` - Buscar por categoria
- `GET /restaurantes/ranking` - Ver ranking por avaliação
- `PUT /restaurantes/{id}/atualizar-dados-restaurante` - Atualizar restaurante
- `PATCH /restaurantes/{id}/status` - Alternar status
- `DELETE /restaurantes/{id}/deletar-restaurante` - Deletar restaurante

### Produtos
- `POST /produtos/cadastrar` - Cadastrar produto
- `GET /produtos` - Listar produtos disponíveis
- `GET /produtos/{id}` - Buscar produto por ID
- `GET /produtos/restaurante/{id}` - Listar produtos por restaurante
- `GET /produtos/categoria/{nomeCategoria}` - Listar por categoria
- `PUT /produtos/{id}/atualizar-dados-produto` - Atualizar produto
- `PATCH /produtos/{id}/status` - Alternar disponibilidade
- `DELETE /produtos/{id}/deletar-produto` - Deletar produto

### Pedidos
- `POST /pedidos` - Criar pedido
- `GET /pedidos/cliente/{clienteId}` - Listar pedidos por cliente
- `GET /pedidos/status/{status}` - Listar por status
- `GET /pedidos/periodo?inicio=...&fim=...` - Listar por período
- `GET /pedidos/data?inicio=...&fim=...` - Listar por data
- `PATCH /pedidos/{id}/status` - Atualizar status do pedido

### Relatórios
- `GET /pedidos/relatorios/faturamento?inicio=...&fim=...` - Faturamento total
- `GET /pedidos/relatorios/estatisticas?status=...` - Contagem por status
- `GET /pedidos/relatorios/ranking-produtos` - Ranking de produtos

### Health
- `GET /health` - Status da aplicação

## 📊 Banco de Dados

O projeto utiliza H2 Database em memória para desenvolvimento. As tabelas são criadas automaticamente via Hibernate com `ddl-auto=create-drop`.

### Principais Tabelas
- `cliente` - Dados dos clientes
- `restaurante` - Informações dos restaurantes
- `produto` - Catálogo de produtos
- `pedido` - Pedidos realizados
- `itemPedido` - Itens de cada pedido

## 🧪 Testes

Execute os testes com:
```bash
./mvnw test
```

## 📝 Considerações Técnicas

- **Validação**: Implementada validação básica nos serviços
- **Transações**: Uso de `@Transactional` para operações complexas
- **Enums**: StatusPedido para controle de estados
- **Relacionamentos**: JPA com fetch EAGER para itens do pedido
- **JSON**: Jackson para serialização com referências gerenciadas

## 🤝 Contribuição

Para contribuir com o projeto:
1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
  POST     /pedidos    Cria pedido

------------------------------------------------------------------------

🧪 Testes

Você pode testar os endpoints usando: - Postman - Insomnia

------------------------------------------------------------------------

📌 Observações

-   Projeto em desenvolvimento
-   Pode ser expandido com autenticação e pagamento online

------------------------------------------------------------------------

👨‍💻 Autor

Desenvolvido por Richard de Oliveira Ribeiro
