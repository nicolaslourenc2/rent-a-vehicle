package locadora.locacao;

import java.io.Serializable;

/**
 * Representa um cliente cadastrado na locadora.
 */
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int codigo;
    private final String nome;
    private final String telefone;

    public Cliente(int codigo, String nome, String telefone) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código do cliente deve ser positivo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do cliente.");
        }
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o telefone do cliente.");
        }

        this.codigo = codigo;
        this.nome = nome.trim();
        this.telefone = telefone.trim();
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + " (" + telefone + ")";
    }
}
