AppProdutos API

Sistema de gerenciamento de produtos, categorias, estoque, pedidos e auditoria.

Autenticação: JWT Bearer Token
Todos os endpoints sensíveis exigem token no header:
Authorization: Bearer <token>


1. Autenticação e Usuários
Criar novo usuário

POST /auth/register

Descrição: Cria um usuário no sistema.

Body:

{
  "username": "admin",
  "password": "123456",
  "role": "ROLE_ADMIN"
}


Roles possíveis: ROLE_ADMIN, ROLE_SELLER, ROLE_CUSTOMER

Login do usuário

POST /auth/login

Descrição: Autentica usuário e retorna token JWT.

Body:

{
  "username": "nome_do_usuario",
  "password": "senha"
}


Resposta exemplo:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Listar todos usuários

GET /usuarios

Roles: ADMIN


2. Categorias
Adicionar nova categoria matriz

POST /categorias

Roles: ADMIN

Body:

{
  "nome": "Eletrônicos"
}

Adicionar nova categoria filho

POST /categorias

Roles: ADMIN

Body:

{
  "nome": "Celulares",
  "categoriaPai": { "id": 1 }
}

Listar categorias

GET /categorias

Roles: ADMIN, SELLER, CUSTOMER

3. Produtos
Adicionar novo produto

POST /v1/produtos/produto

Roles: ADMIN, SELLER

Body:

{
  "nome": "Mouse Gamer",
  "preco": 150.00,
  "quantidadeEstoque": 10,
  "categoria": { "id": 1 }
}

Listar todos os produtos

GET /v1/produtos/

Roles: ADMIN, SELLER, CUSTOMER

Atualizar produto / Estoque

PUT /v1/produtos/atualiza

Roles: ADMIN, SELLER

Body:

{
  "id": 1,
  "nome": "Mouse Gamer RGB",
  "preco": 200.00,
  "quantidadeEstoque": 20,
  "categoria": { "id": 1 }
}

Obter produto DTO por ID

GET /v1/produtos/dto/{id}

Roles: ADMIN, SELLER, CUSTOMER

4. Controle de Estoque
Adicionar estoque

PUT /estoque/adicionar/{idProduto}?quantidade=10

Roles: ADMIN

Remover estoque

PUT /estoque/remover/{idProduto}?quantidade=2

Roles: ADMIN

5. Pedidos
Criar pedido

POST /pedidos

Roles: CUSTOMER

Body exemplo 1:

{
  "itens": [
    { "produtoId": 1, "quantidade": 1 },
    { "produtoId": 2, "quantidade": 0 }
  ]
}


Body exemplo 2:

{
  "itens": [
    { "produtoId": 1, "quantidade": 3 }
  ]
}

Listar pedidos do usuário logado

GET /pedidos

Roles: CUSTOMER

Buscar pedido por ID

GET /pedidos/{id}

Roles: CUSTOMER, ADMIN

6. Auditoria
Consultar logs

GET /auditoria

Descrição: Retorna histórico de alterações (create/update/delete) de qualquer entidade.

Roles: ADMIN

Exemplo resposta:

[
  {
    "id": 1,
    "entityType": "Produto",
    "entityId": 1,
    "action": "CREATE",
    "beforeJson": null,
    "afterJson": "{\"id\":1,\"nome\":\"Mouse Gamer\",\"preco\":150.0}",
    "who": "admin",
    "when": "2025-11-14T15:20:00"
  }
]


Notas:

Todos os endpoints sensíveis exigem autenticação com token JWT.

Roles definem permissões:

ADMIN – total controle.

SELLER – gerencia seus produtos.

CUSTOMER – apenas visualiza e realiza pedidos.

Operações que quebrariam regras (ex.: estoque insuficiente) retornam mensagens de erro claras.

Todas as alterações de dados são registradas no log de auditoria.
