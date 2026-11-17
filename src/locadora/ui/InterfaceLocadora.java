package locadora.ui;

import locadora.locacao.Cliente;
import locadora.locacao.Locacao;
import locadora.locacao.Locadora;
import locadora.persistencia.ArquivoLocadora;
import locadora.veiculos.Carro;
import locadora.veiculos.Moto;
import locadora.veiculos.Veiculo;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public final class InterfaceLocadora {
    private InterfaceLocadora() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceLocadora::iniciar);
    }

    private static void iniciar() {
        ArquivoLocadora arquivo = new ArquivoLocadora(Paths.get("dados", "locadora.dat"));
        Locadora locadora;
        try {
            locadora = arquivo.carregar();
        } catch (NoSuchFileException erro) {
            locadora = new Locadora();
        } catch (IOException | SecurityException erro) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível carregar os dados: " + erro.getMessage()
                            + "\nO arquivo existente não foi alterado.",
                    "Erro ao abrir a locadora", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new LocadoraFrame(locadora, arquivo).setVisible(true);
    }
}
