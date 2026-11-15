# Locadora de veículos — Parte 1

Esta parte do projeto reúne o cadastro de carros e motos, o controle de disponibilidade e a cobrança por diária. O menu do terminal inicia com **4 carros e 4 motos**.

## Como executar

Com um JDK instalado, abra o PowerShell na pasta `locadora-parte1` e execute os dois comandos abaixo.

```powershell
javac -encoding UTF-8 -d out src/locadora/*.java
java -cp out locadora.DemoVeiculos
```

## Menu

| Opção | Ação |
| --- | --- |
| `1` | Lista os veículos com placa, modelo, tipo, diária e situação. |
| `2` | Lista os veículos disponíveis para aluguel. |
| `3` | Pede a placa e a quantidade de dias, registra o aluguel e mostra o total. |
| `4` | Pede a placa e devolve o veículo ao catálogo de disponíveis. |
| `0` | Encerra o programa. |

## Como testar o aluguel e a devolução

1. Digite `1` e confirme que o catálogo mostra oito veículos.
2. Digite `3`, informe a placa `XYZ1A23` e digite `3` dias. O total da Honda CG deve ser **R$ 360,00**.
3. Digite `2` e confirme que a Honda CG saiu da lista de disponíveis.
4. Digite `4` e informe a placa `XYZ1A23`. O programa deve confirmar a devolução.
5. Digite `2` novamente e confirme que a Honda CG voltou à lista.

Para conferir a taxa de limpeza, alugue o Fiat Uno (`ABC1234`) por três dias. O total deve ser **R$ 930,00**, incluindo **R$ 30,00** de limpeza. Para testar as validações, tente alugar com zero dias ou devolver duas vezes a mesma placa. O programa deve mostrar uma mensagem e voltar ao menu.

## Classes e conceitos

| Arquivo | Responsabilidade |
| --- | --- |
| `Locavel.java` | Define o contrato `alugar()` e `devolver()`. |
| `Veiculo.java` | Classe abstrata que implementa `Locavel`, encapsula os atributos e declara `calcularValor(int dias)`. |
| `Carro.java` | Herda de `Veiculo` e cobra as diárias mais uma taxa de limpeza por locação. O exemplo adota R$ 30,00 na constante `TAXA_LIMPEZA`. |
| `Moto.java` | Herda de `Veiculo` e cobra somente as diárias. |
| `CatalogoVeiculos.java` | Mantém um `ArrayList` de veículos e oferece cadastro, busca por placa e listagem. |
| `DemoVeiculos.java` | Carrega os veículos de exemplo e executa o menu no terminal. |

## Integração com as outras partes

- A futura classe `Locadora` pode usar `CatalogoVeiculos` para cadastrar, listar e localizar veículos com `buscarVeiculoPorPlaca(String placa)`. As listagens devolvem cópias das listas com referências aos veículos cadastrados.
- Os construtores de `Carro` e `Moto` recebem placa, modelo e valor da diária. Valores monetários usam `BigDecimal`, por exemplo `new BigDecimal("100.00")`. `calcularValor(int dias)` retorna um `BigDecimal`.
- Ao criar uma locação, calcule o total antes de chamar `veiculo.alugar()`. Na devolução, chame `veiculo.devolver()` e atualize a situação da locação.
- Dias inválidos e placa duplicada geram `IllegalArgumentException`. Alugar um veículo já alugado ou devolver um veículo disponível gera `IllegalStateException`. O catálogo considera, por exemplo, `abc-1234` e `ABC1234` a mesma placa.
- O catálogo e os veículos implementam `Serializable` para integração com o salvamento de objetos da parte 3.
