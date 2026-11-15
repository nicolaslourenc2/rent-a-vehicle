package locadora;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CatalogoVeiculos implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Veiculo> veiculos = new ArrayList<>();

    public void cadastrarVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("Informe o veículo.");
        }

        if (encontrarPorPlaca(veiculo.getPlaca()) != null) {
            throw new IllegalArgumentException("A placa " + veiculo.getPlaca() + " já está cadastrada.");
        }

        veiculos.add(veiculo);
    }

    public Veiculo buscarVeiculoPorPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a placa.");
        }
        String placaNormalizada = Veiculo.normalizarPlaca(placa);
        Veiculo veiculo = encontrarPorPlaca(placaNormalizada);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo com a placa " + placaNormalizada + " não encontrado.");
        }
        return veiculo;
    }

    private Veiculo encontrarPorPlaca(String placa) {
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getPlaca().equals(placa)) {
                return veiculo;
            }
        }
        return null;
    }

    public List<Veiculo> listarVeiculos() {
        return new ArrayList<>(veiculos);
    }

    public List<Veiculo> listarVeiculosDisponiveis() {
        List<Veiculo> disponiveis = new ArrayList<>();
        for (Veiculo veiculo : veiculos) {
            if (veiculo.isDisponivel()) {
                disponiveis.add(veiculo);
            }
        }
        return disponiveis;
    }
}
