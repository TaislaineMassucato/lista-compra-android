# 🛍️ Lista de Compras Android - Documentação do Sistema

Este documento detalha o funcionamento do aplicativo **Lista de Compras**, com foco na integração com a API para persistência de dados.

---

## ✨ Funcionalidades Principais

### 1. Autenticação
* **Login e Cadastro:** O sistema possui telas de login e cadastro. Ao se autenticar, um token JWT é gerado pela API e armazenado localmente via `SharedPreferences`.
* **Segurança:** Todas as requisições subsequentes para a API incluem o token no cabeçalho `Authorization`.

### 2. Gestão de Itens (Lista de Compras)
* **Sincronização em Tempo Real:** Ao abrir o app, os itens são carregados diretamente do banco de dados via API.
* **Adição de Itens:** Ao selecionar um produto e quantidade, o item é enviado para a API e salvo no banco.
* **Edição e Remoção:** Alterações no nome do item ou sua remoção são refletidas instantaneamente no banco de dados.

### 3. Personalização (Novidade 🚀)
O app agora permite que o usuário expanda a base de dados dinamicamente:
* **Novos Locais (Grupos):** Possibilidade de cadastrar novos locais de compra (ex: "Farmácia da Esquina", "Pet Shop").
* **Novas Categorias:** Dentro de cada local, o usuário pode criar categorias personalizadas (ex: "Suplementos", "Brinquedos").
* **Novos Produtos:** Cadastro de produtos específicos dentro de uma categoria.

---

## 🛠️ Arquitetura Técnica

### Fluxo de Dados
1. **App (UI):** O usuário interage com os diálogos de criação.
2. **Retrofit (Network):** O app faz uma chamada `POST` para os endpoints da API.
3. **API (Backend):** Recebe os dados, valida o token e executa o `INSERT` no banco de dados (MySQL/PostgreSQL/MongoDB).
4. **Resposta:** A API retorna sucesso, e o app atualiza a interface chamando um `GET` para listar os dados atualizados.

### Endpoints da API (`ApiService.kt`)

| Recurso | Método | Endpoint | Descrição |
| :--- | :---: | :--- | :--- |
| **Itens** | `GET` | `/shopping-list` | Lista todos os itens do usuário. |
| **Itens** | `POST` | `/shopping-list` | Cadastra um novo item na lista. |
| **Grupos** | `POST` | `/groups` | Cadastra um novo local de compra. |
| **Categorias**| `POST` | `/categories` | Cadastra uma nova categoria. |
| **Produtos** | `POST` | `/products` | Cadastra um novo produto para uma categoria. |

---

## 📂 Estrutura de Pastas Atualizada

```
com.example.listacompra
├── MainActivity.kt      # Lógica principal e integração com API
├── LoginActivity.kt     # Gerenciamento de sessão
├── CadastroActivity.kt  # Registro de novos usuários
├── ItemLista.kt         # Modelo de dados (Data Class)
├── ApiService.kt        # Definição das rotas da API (Retrofit)
├── RetrofitClient.kt    # Configuração do cliente HTTP
└── ListaAdapter.kt      # Gerenciamento da visualização da lista
```

---

## 🚀 Como Testar a Integração

1. Certifique-se de que a API (Backend) está rodando no endereço configurado em `RetrofitClient.kt` (padrão: `http://10.0.2.2:3000`).
2. Faça o cadastro de um novo usuário.
3. Ao clicar no botão **"+"**, experimente usar a opção **"Novo Local"** ou **"Nova Categoria"**.
4. Verifique se os dados persistem após reiniciar o aplicativo.

---

## 👨‍💻 Desenvolvedora
**Taislaine Massucato** - *Estudos de Android & API Integration*
**Gabriel Guerra**