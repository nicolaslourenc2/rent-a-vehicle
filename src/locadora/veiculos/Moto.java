package locadora.veiculos;

import java.math.BigDecimal;

public class Moto extends Veiculo {
    private static final long serialVersionUID = 1L;

    public Moto(String placa, String modelo, BigDecimal valorDiaria) {
        super(placa, modelo, valorDiaria);
    }

    @Override
    public BigDecimal calcularValor(int dias) {
        return calcularValorDiarias(dias);
    }
}
