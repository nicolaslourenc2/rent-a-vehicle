package locadora.locacao;

import locadora.veiculos.CatalogoVeiculos;
import locadora.veiculos.Veiculo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordena o catálogo de veículos, o cadastro de clientes e o controle de locações e devoluções.
 */
public class Locadora implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CatalogoVeiculos catalogo = new CatalogoVeiculos();
    private final ArrayList<Cliente> clientes = new ArrayList<>();
    private final ArrayList<Locacao> locacoes = new ArrayList<>();
    private int proximoCodigoCliente = 1;
    private int proximoCodigoLocacao = 1;

    public void cadastrarVeiculo(Veiculo veiculo) {
        catalogo.cadastrarVeiculo(veiculo);
    }

    public Veiculo buscarVeiculoPorPlaca(String placa) {
        return catalogo.buscarVeiculoPorPlaca(placa);
    }

    public List<Veiculo> listarVeiculos() {
        return catalogo.listarVeiculos();
    }

    public List<Veiculo> listarVeiculosDisponiveis() {
        return catalogo.listarVeiculosDisponiveis();
    }

    /**
     * Cadastra um novo cliente gerando seu código automaticamente em ordem sequencial.
     */
    public Cliente cadastrarCliente(String nome, String telefone) {
        Cliente cliente = new Cliente(proximoCodigoCliente, nome, telefone);
        clientes.add(cliente);
        proximoCodigoCliente++;
        return cliente;
    }

    public Cliente buscarClientePorCodigo(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Informe um código de cliente válido.");
        }
        for (Cliente cliente : clientes) {
            if (cliente.getCodigo() == codigo) {
                return cliente;
            }
        }
        throw new IllegalArgumentException("Cliente com código " + codigo + " não encontrado.");
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }

    /**
     * Registra uma nova locação associando cliente e veículo, calculando o valor de forma polimórfica.
     */
    public Locacao realizarLocacao(int codigoCliente, String placa, int dias) {
        Cliente cliente = buscarClientePorCodigo(codigoCliente);
        Veiculo veiculo = catalogo.buscarVeiculoPorPlaca(placa);
        Locacao locacao = new Locacao(proximoCodigoLocacao, cliente, veiculo, dias);
        locacoes.add(locacao);
        proximoCodigoLocacao++;
        return locacao;
    }

    public Locacao devolverLocacao(int codigoLocacao) {
        Locacao locacao = buscarLocacaoPorCodigo(codigoLocacao);
        locacao.encerrar();
        return locacao;
    }

    public Locacao devolverVeiculoPorPlaca(String placa) {
        Veiculo veiculo = catalogo.buscarVeiculoPorPlaca(placa);
        for (Locacao locacao : locacoes) {
            if (locacao.isAtiva() && locacao.getVeiculo().getPlaca().equals(veiculo.getPlaca())) {
                locacao.encerrar();
                return locacao;
            }
        }
        throw new IllegalStateException("Não há locação ativa para o veículo " + veiculo.getPlaca() + ".");
    }

    public Locacao buscarLocacaoPorCodigo(int codigoLocacao) {
        if (codigoLocacao <= 0) {
            throw new IllegalArgumentException("Informe um código de locação válido.");
        }
        for (Locacao locacao : locacoes) {
            if (locacao.getCodigo() == codigoLocacao) {
                return locacao;
            }
        }
        throw new IllegalArgumentException("Locação com código " + codigoLocacao + " não encontrada.");
    }

    public List<Locacao> listarLocacoes() {
        return new ArrayList<>(locacoes);
    }

    public List<Locacao> listarLocacoesAtivas() {
        return filtrarLocacoesPorSituacao(SituacaoLocacao.ATIVA);
    }

    public List<Locacao> listarLocacoesEncerradas() {
        return filtrarLocacoesPorSituacao(SituacaoLocacao.ENCERRADA);
    }

    private List<Locacao> filtrarLocacoesPorSituacao(SituacaoLocacao situacao) {
        List<Locacao> filtradas = new ArrayList<>();
        for (Locacao locacao : locacoes) {
            if (locacao.getSituacao() == situacao) {
                filtradas.add(locacao);
            }
        }
        return filtradas;
    }
}
