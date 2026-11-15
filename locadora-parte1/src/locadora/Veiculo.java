package locadora;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

public abstract class Veiculo implements Locavel, Serializable {
    private static final long serialVersionUID = 1L;

    private final String placa;
    private final String modelo;
    private final BigDecimal valorDiaria;
    private boolean disponivel = true;

    protected Veiculo(String placa, String modelo, BigDecimal valorDiaria) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a placa.");
        }
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o modelo.");
        }
        if (valorDiaria == null || valorDiaria.signum() <= 0) {
            throw new IllegalArgumentException("O valor da diária deve ser positivo.");
        }

        String placaNormalizada = normalizarPlaca(placa);
        if (placaNormalizada.isEmpty()) {
            throw new IllegalArgumentException("Informe a placa.");
        }
        this.placa = placaNormalizada;
        this.modelo = modelo.trim();
        this.valorDiaria = valorDiaria;
    }

    static String normalizarPlaca(String placa) {
        return placa.trim().replace("-", "").toUpperCase(Locale.ROOT);
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public final boolean isDisponivel() {
        return disponivel;
    }

    @Override
    public final void alugar() {
        if (!disponivel) {
            throw new IllegalStateException("O veículo " + placa + " já está alugado.");
        }
        disponivel = false;
    }

    @Override
    public final void devolver() {
        if (disponivel) {
            throw new IllegalStateException("O veículo " + placa + " já está disponível.");
        }
        disponivel = true;
    }

    public abstract BigDecimal calcularValor(int dias);

    protected final BigDecimal calcularValorDiarias(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero.");
        }
        return valorDiaria.multiply(BigDecimal.valueOf(dias));
    }
}
