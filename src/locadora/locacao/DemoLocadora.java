package locadora.locacao;

import locadora.veiculos.Carro;
import locadora.veiculos.Moto;
import locadora.veiculos.Veiculo;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Demonstração via terminal das operações de clientes, veículos e locações (Partes 1 e 2).
 */
public class DemoLocadora {
    public static void main(String[] args) {
        Locadora locadora = new Locadora();
        carregarDadosDeExemplo(locadora);
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

        try (Scanner entrada = new Scanner(System.in)) {
            executarMenu(locadora, moeda, entrada);
        }
    }

    private static void carregarDadosDeExemplo(Locadora locadora) {
        locadora.cadastrarVeiculo(new Carro("LUL4O13", "GM Astra", new BigDecimal("300.00")));
        locadora.cadastrarVeiculo(new Carro("BCD2B34", "GM Corsa", new BigDecimal("320.00")));
        locadora.cadastrarVeiculo(new Carro("CDE3C45", "Hyundai HB20", new BigDecimal("340.00")));
        locadora.cadastrarVeiculo(new Carro("DEF4D56", "Volkswagen Polo", new BigDecimal("370.00")));
        locadora.cadastrarVeiculo(new Moto("XYZ1A23", "Honda CG", new BigDecimal("120.00")));
        locadora.cadastrarVeiculo(new Moto("EFG5E67", "Yamaha Fazer 250", new BigDecimal("150.00")));
        locadora.cadastrarVeiculo(new Moto("FGH6F78", "Honda Biz 125", new BigDecimal("100.00")));
        locadora.cadastrarVeiculo(new Moto("GHI7G89", "Yamaha Factor 150", new BigDecimal("110.00")));

        locadora.cadastrarCliente("Ana Silva", "(41) 99999-1111");
        locadora.cadastrarCliente("Bruno Costa", "(41) 98888-2222");
    }

    private static void executarMenu(Locadora locadora, NumberFormat moeda, Scanner entrada) {
        while (true) {
            System.out.println("\n=== Locadora de Veículos ===");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Listar clientes");
            System.out.println("3 - Listar todos os veículos");
            System.out.println("4 - Listar veículos disponíveis");
            System.out.println("5 - Realizar locação");
            System.out.println("6 - Devolver locação (por código)");
            System.out.println("7 - Devolver veículo (por placa)");
            System.out.println("8 - Listar locações ativas");
            System.out.println("9 - Listar locações encerradas");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            if (!entrada.hasNextLine()) {
                return;
            }

            try {
                switch (entrada.nextLine().trim()) {
                    case "1":
                        cadastrarCliente(locadora, entrada);
                        break;
                    case "2":
                        exibirClientes(locadora.listarClientes());
                        break;
                    case "3":
                        exibirVeiculos(locadora.listarVeiculos(), moeda);
                        break;
                    case "4":
                        exibirVeiculos(locadora.listarVeiculosDisponiveis(), moeda);
                        break;
                    case "5":
                        realizarLocacao(locadora, moeda, entrada);
                        break;
                    case "6":
                        devolverPorCodigo(locadora, entrada);
                        break;
                    case "7":
                        devolverPorPlaca(locadora, entrada);
                        break;
                    case "8":
                        exibirLocacoes(locadora.listarLocacoesAtivas(), moeda);
                        break;
                    case "9":
                        exibirLocacoes(locadora.listarLocacoesEncerradas(), moeda);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException erro) {
                System.out.println("Informe um número inteiro válido.");
            } catch (IllegalArgumentException | IllegalStateException erro) {
                System.out.println(erro.getMessage());
            }
        }
    }

    private static void cadastrarCliente(Locadora locadora, Scanner entrada) {
        System.out.print("Nome do cliente: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        String nome = entrada.nextLine();

        System.out.print("Telefone: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        String telefone = entrada.nextLine();

        Cliente cliente = locadora.cadastrarCliente(nome, telefone);
        System.out.printf("Cliente cadastrado com sucesso! Código gerado: %d%n", cliente.getCodigo());
    }

    private static void exibirClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.printf("%-6s | %-25s | %s%n", "Código", "Nome", "Telefone");
        for (Cliente cliente : clientes) {
            System.out.printf("%-6d | %-25s | %s%n",
                    cliente.getCodigo(), cliente.getNome(), cliente.getTelefone());
        }
    }

    private static void exibirVeiculos(List<Veiculo> veiculos, NumberFormat moeda) {
        if (veiculos.isEmpty()) {
            System.out.println("Nenhum veículo na lista.");
            return;
        }
        System.out.printf("%-8s | %-20s | %-5s | %12s | %s%n",
                "Placa", "Modelo", "Tipo", "Diária", "Situação");
        for (Veiculo veiculo : veiculos) {
            String situacao = veiculo.isDisponivel() ? "Disponível" : "Alugado";
            System.out.printf("%-8s | %-20s | %-5s | %12s | %s%n",
                    veiculo.getPlaca(), veiculo.getModelo(), veiculo.getClass().getSimpleName(),
                    moeda.format(veiculo.getValorDiaria()), situacao);
        }
    }

    private static void realizarLocacao(Locadora locadora, NumberFormat moeda, Scanner entrada) {
        System.out.print("Código do cliente: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        int codigoCliente = Integer.parseInt(entrada.nextLine().trim());

        System.out.print("Placa do veículo: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        String placa = entrada.nextLine();

        System.out.print("Quantidade de dias: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        int dias = Integer.parseInt(entrada.nextLine().trim());

        Locacao locacao = locadora.realizarLocacao(codigoCliente, placa, dias);
        System.out.printf("Locação #%d registrada! Cliente: %s | Veículo: %s | Total: %s%n",
                locacao.getCodigo(),
                locacao.getCliente().getNome(),
                locacao.getVeiculo().getPlaca(),
                moeda.format(locacao.getValorTotal()));
    }

    private static void devolverPorCodigo(Locadora locadora, Scanner entrada) {
        System.out.print("Código da locação a devolver: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        int codigoLocacao = Integer.parseInt(entrada.nextLine().trim());
        Locacao locacao = locadora.devolverLocacao(codigoLocacao);
        System.out.printf("Locação #%d encerrada. Veículo %s disponível novamente.%n",
                locacao.getCodigo(), locacao.getVeiculo().getPlaca());
    }

    private static void devolverPorPlaca(Locadora locadora, Scanner entrada) {
        System.out.print("Placa do veículo a devolver: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        Locacao locacao = locadora.devolverVeiculoPorPlaca(entrada.nextLine());
        System.out.printf("Locação #%d encerrada. Veículo %s disponível novamente.%n",
                locacao.getCodigo(), locacao.getVeiculo().getPlaca());
    }

    private static void exibirLocacoes(List<Locacao> locacoes, NumberFormat moeda) {
        if (locacoes.isEmpty()) {
            System.out.println("Nenhuma locação encontrada.");
            return;
        }
        System.out.printf("%-6s | %-20s | %-8s | %-18s | %-4s | %12s | %s%n",
                "Código", "Cliente", "Placa", "Modelo", "Dias", "Total", "Situação");
        for (Locacao locacao : locacoes) {
            System.out.printf("%-6d | %-20s | %-8s | %-18s | %-4d | %12s | %s%n",
                    locacao.getCodigo(),
                    locacao.getCliente().getNome(),
                    locacao.getVeiculo().getPlaca(),
                    locacao.getVeiculo().getModelo(),
                    locacao.getDias(),
                    moeda.format(locacao.getValorTotal()),
                    locacao.getSituacao());
        }
    }
}
