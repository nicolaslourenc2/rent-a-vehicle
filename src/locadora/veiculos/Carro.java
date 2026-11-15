package locadora.veiculos;

import java.math.BigDecimal;

public class Carro extends Veiculo {
    private static final long serialVersionUID = 1L;
    private static final BigDecimal TAXA_LIMPEZA = new BigDecimal("30.00");

    public Carro(String placa, String modelo, BigDecimal valorDiaria) {
        super(placa, modelo, valorDiaria);
    }

    @Override
    public BigDecimal calcularValor(int dias) {
        return calcularValorDiarias(dias).add(TAXA_LIMPEZA);
    }
}
