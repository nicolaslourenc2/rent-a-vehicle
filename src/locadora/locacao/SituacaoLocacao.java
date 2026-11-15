package locadora.locacao;

import java.io.Serializable;

/**
 * Representa os possíveis estados de uma locação.
 */
public enum SituacaoLocacao implements Serializable {
    ATIVA("Ativa"),
    ENCERRADA("Encerrada");

    private final String descricao;

    SituacaoLocacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
