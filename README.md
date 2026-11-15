# Locadora de Veículos

Sistema de locadora de veículos desenvolvido como **projeto final da disciplina de Programação Orientada a Objetos**, aplicando os conceitos de POO a um problema real de gestão de frota, clientes e aluguéis (carros e motos, com cobrança por diária).

## Equipe

| Nome | GitHub | Parte |
|------|--------|-------|
| Isaque dos Reis Jess | [@usuario1](https://github.com/usuario1) | Parte 1 — Veículos e regras de cobrança |
| Laura Zoffoli Silveira | [@usuario2](https://github.com/usuario2) | _(definir)_ |
| Nicolas Lourenço | [@nicolaslourenc2](https://github.com/nicolaslourenc2) | Parte 2 — Clientes e controle das locações |
| Rauhan Kniess de Moura | [@usuario3](https://github.com/usuario3) | _(definir)_ |

**Disciplina:** Programação Orientada a Objetos  
**Instituição:** _Pontifícia Universidade Católica do Paraná_  
**Professora:** _Marina de Lara_

## Descrição do problema

Uma locadora precisa controlar sua frota de carros e motos, saber quais veículos estão disponíveis, cadastrar clientes, registrar locações, calcular o valor a cobrar conforme o tipo de veículo e tratar devoluções. Cada tipo de veículo possui uma regra própria de cobrança, e as operações devem manter o estado da frota e das locações consistente mesmo diante de entradas inválidas.

## Funcionalidades

- [x] Cadastro e listagem de veículos (`Carro` e `Moto`) — **Parte 1**
- [x] Controle de disponibilidade da frota — **Parte 1**
- [x] Cadastro e listagem de clientes com código gerado automaticamente — **Parte 2**
- [x] Registro de locação associando cliente e veículo disponível — **Parte 2**
- [x] Cálculo do valor do aluguel usando polimorfismo (`veiculo.calcularValor(dias)`) — **Parte 2**
- [x] Registro de devolução (por código da locação ou placa) e liberação do veículo — **Parte 2**
- [x] Listagem de locações ativas e encerradas — **Parte 2**
- [ ] Persistência de objetos em arquivo (`ArquivoLocadora`) — **Parte 3**
- [ ] Interface gráfica (Swing) integrada à `Locadora` — **Parte 4**

## Decisões de modelagem

Esta seção explica **por que** cada conceito de POO foi usado e qual problema ele resolve.

| Conceito | Onde está no código | Por que foi usado |
|----------|--------------------|-------------------|
| Classes e objetos | `Veiculo`, `Carro`, `Moto`, `CatalogoVeiculos`, `Cliente`, `Locacao`, `Locadora` | Representar cada entidade e coordenador do domínio com responsabilidades bem definidas |
| Encapsulamento | Atributos privados em `Veiculo`, `Cliente`, `Locacao` e `Locadora` | Proteger invariantes (placa normalizada, código único/automático, disponibilidade e situação da locação só mudam por métodos controlados) |
| Coleção de objetos | `ArrayList<Veiculo>` em `CatalogoVeiculos`; `ArrayList<Cliente>` e `ArrayList<Locacao>` em `Locadora` | Armazenar, buscar e retornar cópias defensivas das listas sem expor as coleções internas |
| Associação e interação | `Locacao` associa `Cliente` e `Veiculo`; `Locadora` coordena `CatalogoVeiculos`, `Cliente`, `Veiculo` e `Locacao` | Permitir que cada locação registre quem alugou, qual veículo, por quantos dias e o total calculado |
| Herança | `Carro` e `Moto` estendem `Veiculo` | Reaproveitar atributos e controles comuns (`placa`, `modelo`, `valorDiaria`, `disponivel`) |
| Polimorfismo | `veiculo.calcularValor(dias)` chamado na `Locacao` | Calcular o valor correto para `Carro` (com taxa de limpeza) e `Moto` (somente diárias) sem `if/instanceof` |
| Classe/método abstrato | `Veiculo` declara `calcularValor(int dias)` abstrato | Obrigar que todo tipo de veículo defina sua própria regra de cobrança |
| Interface | `Locavel` (`alugar()` e `devolver()`), implementada por `Veiculo` | Definir o contrato mínimo de locação/devolução desacoplado da implementação |

## Recursos extras

- [x] Tratamento de exceções (validações nas classes de domínio com `IllegalArgumentException` e `IllegalStateException`, a integrar com Parte 3 e Parte 4)
- [ ] Persistência em arquivo (`Serializable` já preparado nas classes das Partes 1 e 2; `ArquivoLocadora` na Parte 3)
- [ ] Interface gráfica (Swing na Parte 4)

## Estrutura do projeto

O projeto é unificado em um único módulo Java, dividido explicitamente por pacotes para facilitar o estudo e a apresentação de cada parte:

```text
rent-a-vehicle/
├── src/
│   └── locadora/
│       ├── veiculos/              # Parte 1 — Veículos e regras de cobrança
│       │   ├── Locavel.java
│       │   ├── Veiculo.java
│       │   ├── Carro.java
│       │   ├── Moto.java
│       │   ├── CatalogoVeiculos.java
│       │   └── DemoVeiculos.java
│       └── locacao/               # Parte 2 — Clientes e controle das locações
│           ├── Cliente.java
│           ├── SituacaoLocacao.java
│           ├── Locacao.java
│           ├── Locadora.java
│           └── DemoLocadora.java
├── .gitignore
├── LICENSE
├── rent-a-vehicle.iml
└── README.md
```

> Os pacotes `locadora.persistencia` (**Parte 3**) e `locadora.ui` (**Parte 4**) serão adicionados na mesma estrutura `src/locadora/`.

## Requisitos

- Java JDK instalado (Java 17 ou superior)
- Terminal (PowerShell, Bash etc.) ou IDE Java (ex.: IntelliJ IDEA)

## Como instalar e executar

```powershell
# 1. Clonar o repositório
git clone https://github.com/nicolaslourenc2/rent-a-vehicle.git
cd rent-a-vehicle

# 2. Compilar todo o projeto
javac -encoding UTF-8 -d out src/locadora/veiculos/*.java src/locadora/locacao/*.java

# 3. Executar a demonstração completa (Partes 1 e 2)
java -cp out locadora.locacao.DemoLocadora

# (Opcional) Executar apenas a demo isolada da Parte 1
java -cp out locadora.veiculos.DemoVeiculos
```

---

## Parte 1 — Veículos e regras de cobrança (`locadora.veiculos`)

Reúne o cadastro de carros e motos, o controle de disponibilidade e a cobrança por diária. A `DemoVeiculos` inicia com **4 carros e 4 motos**.

| Arquivo | Responsabilidade |
| --- | --- |
| `Locavel.java` | Define o contrato `alugar()` e `devolver()`. |
| `Veiculo.java` | Classe abstrata que implementa `Locavel`, encapsula os atributos e declara `calcularValor(int dias)`. |
| `Carro.java` | Herda de `Veiculo` e cobra as diárias mais uma taxa fixa de limpeza (`TAXA_LIMPEZA = R$ 30,00`). |
| `Moto.java` | Herda de `Veiculo` e cobra somente as diárias. |
| `CatalogoVeiculos.java` | Mantém um `ArrayList` de veículos e oferece cadastro, busca por placa normalizada e listagens. |
| `DemoVeiculos.java` | Executa o menu isolado da Parte 1 no terminal. |

---

## Parte 2 — Clientes e controle das locações (`locadora.locacao`)

Relaciona clientes aos veículos do catálogo, gera códigos sequenciais automaticamente, calcula totais via polimorfismo e controla o ciclo de vida das locações.

| Arquivo | Responsabilidade |
| --- | --- |
| `Cliente.java` | Encapsula `codigo`, `nome` e `telefone`, validando dados obrigatórios no construtor. |
| `SituacaoLocacao.java` | Enum com os estados `ATIVA` e `ENCERRADA`. |
| `Locacao.java` | Associa `Cliente` e `Veiculo`, calcula `valorTotal` com `veiculo.calcularValor(dias)` antes de chamar `veiculo.alugar()`, e libera o veículo em `encerrar()`. |
| `Locadora.java` | Coordena `CatalogoVeiculos`, `ArrayList<Cliente>` e `ArrayList<Locacao>`, gera códigos automáticos de clientes e locações, e oferece listagens filtradas. |
| `DemoLocadora.java` | Menu no terminal para testar o fluxo completo (clientes, veículos, locações ativas/encerradas e devoluções). |

### Menu da `DemoLocadora`

| Opção | Ação |
| --- | --- |
| `1` | Cadastra um cliente (código gerado automaticamente). |
| `2` | Lista os clientes cadastrados. |
| `3` | Lista todos os veículos com situação (`Disponível` / `Alugado`). |
| `4` | Lista apenas os veículos disponíveis. |
| `5` | Realiza uma locação (código do cliente, placa e dias) e exibe o total calculado. |
| `6` | Devolve uma locação informando o código da locação. |
| `7` | Devolve um veículo informando a placa. |
| `8` | Lista as locações ativas. |
| `9` | Lista as locações encerradas. |
| `0` | Encerra o programa. |

### Integração com as Partes 3 e 4

- **Parte 3 (Persistência e Exceções):** Todas as classes de domínio (`Locadora`, `CatalogoVeiculos`, `Veiculo`, `Carro`, `Moto`, `Cliente`, `Locacao` e `SituacaoLocacao`) implementam `Serializable`. Basta serializar a instância única de `Locadora` em `ArquivoLocadora`. As regras de negócio já lançam `IllegalArgumentException` (dados inválidos, placa duplicada, cliente/veículo/locação não encontrados) e `IllegalStateException` (veículo já alugado, devolução de locação já encerrada).
- **Parte 4 (Interface Gráfica Swing):** As telas devem manter uma instância de `Locadora` e chamar `cadastrarCliente`, `listarClientes`, `cadastrarVeiculo`, `listarVeiculos`, `listarVeiculosDisponiveis`, `realizarLocacao`, `listarLocacoesAtivas` e `devolverLocacao`, exibindo as mensagens de exceção sem duplicar regras de cálculo na interface.

## Licença

Projeto acadêmico, sem fins comerciais.
