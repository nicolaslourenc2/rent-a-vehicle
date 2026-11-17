package locadora.persistencia;

import locadora.locacao.Locadora;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/** Persiste o conjunto de objetos da locadora, sem executar regras de negócio. */
public class ArquivoLocadora {
    private final Path caminho;

    public ArquivoLocadora(Path caminho) {
        this.caminho = Objects.requireNonNull(caminho, "Informe o caminho do arquivo.").toAbsolutePath();
    }

    /**
     * Recupera clientes, veículos, locações e contadores, preservando referências.
     * Arquivo ausente lança NoSuchFileException; arquivos inválidos não são substituídos.
     * Utilize somente arquivos locais produzidos por esta aplicação.
     */
    public Locadora carregar() throws IOException {
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(caminho))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof Locadora)) {
                throw new IOException("O arquivo não contém dados de uma locadora.");
            }
            return (Locadora) objeto;
        } catch (ClassNotFoundException erro) {
            throw new IOException("O arquivo contém uma classe incompatível com o sistema.", erro);
        }
    }

    /**
     * Escreve primeiro em um temporário no mesmo diretório e substitui atomicamente.
     * Se o sistema de arquivos não permitir a troca atômica, informa a falha e mantém
     * o arquivo anterior, em vez de arriscar uma gravação parcial.
     */
    public void salvar(Locadora locadora) throws IOException {
        Objects.requireNonNull(locadora, "Informe a locadora.");
        Files.createDirectories(caminho.getParent());
        Path temporario = Files.createTempFile(caminho.getParent(), "locadora-", ".tmp");
        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(locadora);
            }
            Files.move(temporario, caminho, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
