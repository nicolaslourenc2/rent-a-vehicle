# Locadora de Veículos

Sistema de locadora de veículos desenvolvido como **projeto final da disciplina de Programação Orientada a Objetos**, aplicando os conceitos de POO a um problema real de gestão de frota, clientes e aluguéis (carros e motos, com cobrança por diária).

## Equipe

| Nome | GitHub | Parte |
|------|--------|-------|
| Isaque dos Reis Jess | [@isaquereis3-ui](https://github.com/isaquereis3-ui) | Parte 1 — Veículos e regras de cobrança |
| Laura Zoffoli Silveira | [@Laura-zs](https://github.com/Laura-zs) | Parte 4 — Interface gráfica |
| Nicolas Lourenço | [@nicolaslourenc2](https://github.com/nicolaslourenc2) | Parte 2 — Clientes e controle das locações |
| Rauhan Kniess de Moura | [@RAUHANKM](https://github.com/RAUHANKM) | Parte 3 - Persistência e exceções |


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
- [x] Persistência de objetos em arquivo (`ArquivoLocadora`) — **Parte 3**
- [x] Interface gráfica (Swing) integrada à `Locadora` — **Parte 4**

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

- [x] Tratamento de exceções (validações nas classes de domínio com `IllegalArgumentException` e `IllegalStateException`, integradas ao menu; disponíveis para a Parte 4)
- [x] Persistência em arquivo (`ArquivoLocadora`, com serialização da instância de `Locadora`)
- [x] Interface gráfica (Swing na Parte 4)

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
│       ├── locacao/               # Parte 2 — Clientes e controle das locações
│       │   ├── Cliente.java
│       │   ├── SituacaoLocacao.java
│       │   ├── Locacao.java
│       │   ├── Locadora.java
│       │   └── DemoLocadora.java
│       ├── persistencia/          # Parte 3 — Persistência em arquivo
│       │   └── ArquivoLocadora.java
│       └── ui/                    # Parte 4 — Interface gráfica Swing
│           ├── InterfaceLocadora.java
│           └── LocadoraFrame.java
├── .gitignore
├── LICENSE
├── rent-a-vehicle.iml
└── README.md
```

> A interface gráfica e a demonstração de terminal compartilham as regras de negócio e o arquivo `dados/locadora.dat`.

## Requisitos

- Java JDK instalado (Java 17 ou superior)
- Terminal (PowerShell, Bash etc.) ou IDE Java (ex.: IntelliJ IDEA)

## Como instalar e executar

```powershell
# 1. Clonar o repositório
git clone https://github.com/nicolaslourenc2/rent-a-vehicle.git
cd rent-a-vehicle

# 2. Compilar todo o projeto
javac -encoding UTF-8 -d out src/locadora/veiculos/*.java src/locadora/locacao/*.java src/locadora/persistencia/*.java src/locadora/ui/*.java

# 3. Executar a demonstração completa (Partes 1, 2 e 3)
java -cp out locadora.locacao.DemoLocadora

# (Alternativa) Iniciar a interface gráfica Swing
java -cp out locadora.ui.InterfaceLocadora

# (Opcional) Executar apenas a demonstração isolada da Parte 1
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
| `S` | Salva os dados ou tenta novamente após uma falha de gravação. |
| `0` | Salva os dados e encerra; se a gravação falhar, mantém o menu aberto. |

### Integração com as Partes 3 e 4

- **Parte 3 (Persistência e Exceções):** Todas as classes de domínio (`Locadora`, `CatalogoVeiculos`, `Veiculo`, `Carro`, `Moto`, `Cliente`, `Locacao` e `SituacaoLocacao`) implementam `Serializable`. `ArquivoLocadora` serializa a instância única de `Locadora`, preservando as referências entre objetos e os contadores de códigos. As regras de negócio já lançam `IllegalArgumentException` (dados inválidos, placa duplicada, cliente/veículo/locação não encontrados) e `IllegalStateException` (veículo já alugado, devolução de locação já encerrada).
- **Parte 4 (Interface Gráfica Swing):** `InterfaceLocadora` e `LocadoraFrame`, em `locadora.ui`, mantêm uma instância de `Locadora` e chamam suas operações de cadastro, listagem, locação e devolução, exibindo mensagens de validação sem duplicar regras de cálculo.

## Parte 4 — Interface gráfica (`locadora.ui`)

A `InterfaceLocadora` inicia a aplicação Swing. A janela oferece abas para cadastro e listagem de clientes e veículos, registro de locações e devolução das locações ativas. O total da locação é obtido do objeto `Locacao` retornado pelas regras do domínio; a interface não repete o cálculo.

As operações bem-sucedidas atualizam as tabelas e seleções e salvam a instância compartilhada de `Locadora` em `dados/locadora.dat`. Se o arquivo ainda não existir, a interface começa com os cadastros vazios. Erros de validação são exibidos na tela; falhas de persistência também são informadas, e alterações ainda não salvas podem ser tentadas novamente pelo menu **Arquivo > Salvar agora**.

Execute a partir da pasta do projeto:

```powershell
java -cp out locadora.ui.InterfaceLocadora
```

## Parte 3 — Salvamento dos dados e tratamento de exceções

`ArquivoLocadora`, no pacote `locadora.persistencia`, oferece `salvar(Locadora)` e `carregar()`, recebendo um `Path` no construtor. A serialização inclui clientes, carros, motos, locações ativas e encerradas, disponibilidade e próximos códigos.

A `DemoLocadora` utiliza `dados/locadora.dat`, relativo ao diretório em que o programa é executado. Execute sempre a partir da pasta do projeto para reutilizar os mesmos dados. Os exemplos são criados somente quando o arquivo não existe. O menu salva após cadastros de clientes, locações e devoluções, na opção S e ao sair (inclusive no fim da entrada). A demonstração isolada `DemoVeiculos` continua independente, sem persistência.

A gravação usa um temporário no mesmo diretório e uma substituição atômica para evitar arquivos parcialmente gravados. Se a operação não for suportada ou ocorrer outra falha de gravação, o menu informa que as alterações permanecem apenas na memória e permite tentar novamente com S. Na opção 0, uma falha mantém o menu aberto; se a entrada já terminou, não é possível oferecer nova tentativa. Arquivos corrompidos, incompatíveis ou sem permissão de leitura geram uma mensagem e encerramento controlado, sem sobrescrever os dados existentes. Corrija o acesso ou restaure um backup antes de reiniciar. Use apenas arquivos de dados produzidos pela aplicação.

As regras continuam nas classes responsáveis: `Locacao` e `Veiculo` validam os dias; `Veiculo` impede aluguel duplicado; `CatalogoVeiculos` impede placa duplicada; `Locacao` impede devolução repetida. O menu captura `IllegalArgumentException`, `IllegalStateException` e entradas numéricas inválidas e continua funcionando. A persistência propaga `IOException`, tratada pela interface, sem concentrar regras de negócio.

Na interface gráfica, `NoSuchFileException` indica o primeiro uso e inicia uma locadora vazia; outras falhas de leitura são informadas sem substituir os dados existentes.

### Testes sem bibliotecas externas

```powershell
javac -encoding UTF-8 -d out src/locadora/veiculos/*.java src/locadora/locacao/*.java src/locadora/persistencia/*.java src/locadora/ui/*.java tests/locadora/*.java
java -cp out locadora.PersistenciaTest
```

Os testes verificam recuperação e atualização do arquivo, referências compartilhadas, histórico, disponibilidade, valores e códigos sequenciais; dias inválidos, aluguel duplicado, placa duplicada e devolução repetida; arquivo ausente, corrompido, objeto de tipo incorreto e falha de gravação. Os arquivos de teste são temporários e removidos ao final.

## Licença

Projeto acadêmico, sem fins comerciais.
