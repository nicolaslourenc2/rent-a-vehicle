package locadora;

import locadora.locacao.Locadora;
import locadora.locacao.Locacao;
import locadora.persistencia.ArquivoLocadora;
import locadora.veiculos.Carro;
import locadora.veiculos.Moto;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;

/** Testes executáveis sem dependências externas. */
public class PersistenciaTest {
    private interface Acao { void executar() throws Exception; }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static void esperar(Class<? extends Exception> tipo, Acao acao) throws Exception {
        try {
            acao.executar();
        } catch (Exception erro) {
            if (tipo.isInstance(erro)) return;
            throw erro;
        }
        throw new AssertionError("Era esperada a exceção " + tipo.getSimpleName());
    }

    public static void main(String[] args) throws Exception {
        Path pasta = Files.createTempDirectory("teste-locadora-");
        Path dados = pasta.resolve("locadora.dat");
        Path invalido = pasta.resolve("invalido.dat");
        Path bloqueio = pasta.resolve("bloqueio");
        try {
            ArquivoLocadora arquivo = new ArquivoLocadora(dados);
            esperar(NoSuchFileException.class, arquivo::carregar);
            Locadora original = new Locadora();
            original.cadastrarCliente("Ana", "123");
            original.cadastrarVeiculo(new Carro("ABC-1234", "Carro", new BigDecimal("100")));
            original.cadastrarVeiculo(new Moto("XYZ1234", "Moto", new BigDecimal("50")));
            esperar(IllegalArgumentException.class, () -> original.realizarLocacao(1, "ABC1234", 0));
            esperar(IllegalArgumentException.class, () -> original.realizarLocacao(1, "ABC1234", -1));
            esperar(IllegalArgumentException.class, () -> original.cadastrarVeiculo(
                    new Carro("abc-1234", "Outro", BigDecimal.ONE)));
            original.realizarLocacao(1, "ABC1234", 2);
            esperar(IllegalStateException.class, () -> original.realizarLocacao(1, "ABC1234", 1));
            original.realizarLocacao(1, "XYZ1234", 1);
            original.devolverLocacao(2);
            esperar(IllegalStateException.class, () -> original.devolverLocacao(2));
            arquivo.salvar(original);
            Locadora recuperada = arquivo.carregar();
            Locacao ativa = recuperada.buscarLocacaoPorCodigo(1);
            verificar(recuperada.listarClientes().size() == 1, "Clientes não recuperados");
            verificar(recuperada.listarVeiculos().size() == 2, "Veículos não recuperados");
            verificar(recuperada.listarLocacoesEncerradas().size() == 1, "Histórico perdido");
            verificar(ativa.getCliente() == recuperada.buscarClientePorCodigo(1), "Referência do cliente perdida");
            verificar(ativa.getVeiculo() == recuperada.buscarVeiculoPorPlaca("ABC1234"), "Referência do veículo perdida");
            verificar(!ativa.getVeiculo().isDisponivel(), "Disponibilidade perdida");
            verificar(ativa.getValorTotal().compareTo(new BigDecimal("230")) == 0, "Total alterado");
            recuperada.devolverLocacao(1);
            verificar(recuperada.buscarVeiculoPorPlaca("ABC1234").isDisponivel(), "Devolução não atualizou catálogo");
            verificar(recuperada.cadastrarCliente("Bruno", "456").getCodigo() == 2, "Contador de clientes perdido");
            verificar(recuperada.realizarLocacao(2, "ABC1234", 3).getCodigo() == 3, "Contador de locações perdido");
            arquivo.salvar(recuperada);
            verificar(arquivo.carregar().listarLocacoes().size() == 3, "Atualização não persistida");
            Files.write(invalido, new byte[] {1, 2, 3});
            byte[] antes = Files.readAllBytes(invalido);
            esperar(IOException.class, () -> new ArquivoLocadora(invalido).carregar());
            verificar(Arrays.equals(antes, Files.readAllBytes(invalido)), "Arquivo inválido alterado");
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(invalido))) {
                saida.writeObject("objeto incorreto");
            }
            esperar(IOException.class, () -> new ArquivoLocadora(invalido).carregar());
            Files.write(bloqueio, new byte[] {1});
            esperar(IOException.class, () -> new ArquivoLocadora(bloqueio.resolve("dados.dat")).salvar(original));
            verificar(arquivo.carregar().listarLocacoes().size() == 3, "Dados anteriores perdidos");
            System.out.println("Todos os testes de persistência e regras passaram.");
        } finally {
            Files.deleteIfExists(dados);
            Files.deleteIfExists(invalido);
            Files.deleteIfExists(bloqueio);
            Files.deleteIfExists(pasta);
        }
    }
}
