package locadora;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class DemoVeiculos {
    public static void main(String[] args) {
        CatalogoVeiculos catalogo = new CatalogoVeiculos();
        cadastrarVeiculosDeExemplo(catalogo);
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

        try (Scanner entrada = new Scanner(System.in)) {
            executarMenu(catalogo, moeda, entrada);
        }
    }

    private static void cadastrarVeiculosDeExemplo(CatalogoVeiculos catalogo) {
        catalogo.cadastrarVeiculo(new Carro("LUL4O13", "GM Astra", new BigDecimal("300.00")));
        catalogo.cadastrarVeiculo(new Carro("BCD2B34", "GM Corsa", new BigDecimal("320.00")));
        catalogo.cadastrarVeiculo(new Carro("CDE3C45", "Hyundai HB20", new BigDecimal("340.00")));
        catalogo.cadastrarVeiculo(new Carro("DEF4D56", "Volkswagen Polo", new BigDecimal("370.00")));
        catalogo.cadastrarVeiculo(new Moto("XYZ1A23", "Honda CG", new BigDecimal("120.00")));
        catalogo.cadastrarVeiculo(new Moto("EFG5E67", "Yamaha Fazer 250", new BigDecimal("150.00")));
        catalogo.cadastrarVeiculo(new Moto("FGH6F78", "Honda Biz 125", new BigDecimal("100.00")));
        catalogo.cadastrarVeiculo(new Moto("GHI7G89", "Yamaha Factor 150", new BigDecimal("110.00")));
    }

    private static void executarMenu(CatalogoVeiculos catalogo, NumberFormat moeda, Scanner entrada) {
        while (true) {
            System.out.println("\nLocadora de veículos");
            System.out.println("1 - Listar todos os veículos");
            System.out.println("2 - Listar veículos disponíveis");
            System.out.println("3 - Alugar veículo");
            System.out.println("4 - Devolver veículo");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            if (!entrada.hasNextLine()) {
                return;
            }

            try {
                switch (entrada.nextLine().trim()) {
                    case "1":
                        exibirVeiculos(catalogo.listarVeiculos(), moeda);
                        break;
                    case "2":
                        exibirVeiculos(catalogo.listarVeiculosDisponiveis(), moeda);
                        break;
                    case "3":
                        alugarVeiculo(catalogo, moeda, entrada);
                        break;
                    case "4":
                        devolverVeiculo(catalogo, entrada);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException erro) {
                System.out.println("Digite um número inteiro para a quantidade de dias.");
            } catch (IllegalArgumentException | IllegalStateException erro) {
                System.out.println(erro.getMessage());
            }
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

    private static void alugarVeiculo(CatalogoVeiculos catalogo, NumberFormat moeda, Scanner entrada) {
        System.out.print("Placa do veículo: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        Veiculo veiculo = catalogo.buscarVeiculoPorPlaca(entrada.nextLine());

        System.out.print("Quantidade de dias: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        int dias = Integer.parseInt(entrada.nextLine().trim());
        BigDecimal total = veiculo.calcularValor(dias);
        veiculo.alugar();
        System.out.printf("Veículo %s alugado. Total: %s%n", veiculo.getPlaca(), moeda.format(total));
    }

    private static void devolverVeiculo(CatalogoVeiculos catalogo, Scanner entrada) {
        System.out.print("Placa do veículo a devolver: ");
        if (!entrada.hasNextLine()) {
            return;
        }
        Veiculo veiculo = catalogo.buscarVeiculoPorPlaca(entrada.nextLine());
        veiculo.devolver();
        System.out.printf("Veículo %s devolvido e disponível para aluguel.%n", veiculo.getPlaca());
    }
}
