package locadora.locacao;

import locadora.veiculos.Veiculo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Associa um cliente a um veículo alugado por uma quantidade de dias,
 * armazenando o valor calculado e a situação da locação.
 */
public class Locacao implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int codigo;
    private final Cliente cliente;
    private final Veiculo veiculo;
    private final int dias;
    private final BigDecimal valorTotal;
    private SituacaoLocacao situacao;

    public Locacao(int codigo, Cliente cliente, Veiculo veiculo, int dias) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código da locação deve ser positivo.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Informe o cliente.");
        }
        if (veiculo == null) {
            throw new IllegalArgumentException("Informe o veículo.");
        }
        if (dias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero.");
        }

        // Calcula o valor antes de alterar o estado do veículo para manter consistência em caso de erro.
        BigDecimal totalCalculado = veiculo.calcularValor(dias);
        veiculo.alugar();

        this.codigo = codigo;
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dias = dias;
        this.valorTotal = totalCalculado;
        this.situacao = SituacaoLocacao.ATIVA;
    }

    /**
     * Encerra a locação e devolve o veículo ao catálogo de disponíveis.
     */
    public void encerrar() {
        if (situacao == SituacaoLocacao.ENCERRADA) {
            throw new IllegalStateException("A locação " + codigo + " já está encerrada.");
        }
        veiculo.devolver();
        situacao = SituacaoLocacao.ENCERRADA;
    }

    public int getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public int getDias() {
        return dias;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public SituacaoLocacao getSituacao() {
        return situacao;
    }

    public boolean isAtiva() {
        return situacao == SituacaoLocacao.ATIVA;
    }

    @Override
    public String toString() {
        return String.format("Locação #%d | %s | %s (%s) | %d dia(s) | %s",
                codigo, cliente.getNome(), veiculo.getModelo(), veiculo.getPlaca(), dias, situacao);
    }
}
