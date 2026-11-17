package locadora.ui;

import locadora.locacao.Cliente;
import locadora.locacao.Locacao;
import locadora.locacao.Locadora;
import locadora.persistencia.ArquivoLocadora;
import locadora.veiculos.Carro;
import locadora.veiculos.Moto;
import locadora.veiculos.Veiculo;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class LocadoraFrame extends JFrame {
    private static final Color FUNDO = new Color(0xF3F6FA);
    private static final Color SUPERFICIE = Color.WHITE;
    private static final Color TEXTO = new Color(0x172B4D);
    private static final Color TEXTO_SECUNDARIO = new Color(0x65758B);
    private static final Color DESTAQUE = new Color(0x176B67);
    private static final Color BORDA = new Color(0xDFE6EF);

    private final Locadora locadora;
    private final ArquivoLocadora arquivo;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private final DefaultTableModel clientesModelo = criarModelo("Código", "Nome", "Telefone");
    private final DefaultTableModel veiculosModelo =
            criarModelo("Placa", "Modelo", "Tipo", "Diária", "Disponibilidade");
    private final DefaultTableModel locacoesModelo =
            criarModelo("Código", "Cliente", "Veículo", "Modelo", "Dias", "Total");

    private final JTable clientesTabela = new JTable(clientesModelo);
    private final JTable veiculosTabela = new JTable(veiculosModelo);
    private final JTable locacoesTabela = new JTable(locacoesModelo);
    private final JComboBox<Cliente> clienteSelecao = new JComboBox<>();
    private final JComboBox<Veiculo> veiculoSelecao = new JComboBox<>();
    private final JLabel clientesContagem = new JLabel("0");
    private final JLabel veiculosContagem = new JLabel("0");
    private final JLabel disponiveisContagem = new JLabel("0");
    private final JLabel locacoesContagem = new JLabel("0");
    private List<Locacao> locacoesAtivas = new ArrayList<>();
    private boolean alteracoesPendentes;

    LocadoraFrame(Locadora locadora, ArquivoLocadora arquivo) {
        super("Locadora de Veículos");
        this.locadora = locadora;
        this.arquivo = arquivo;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(820, 580));
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setJMenuBar(criarMenu());

        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        conteudo.add(criarCabecalho(), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("SansSerif", Font.BOLD, 14));
        abas.setForeground(TEXTO);
        abas.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        abas.addTab("Clientes", criarAbaClientes());
        abas.addTab("Veículos", criarAbaVeiculos());
        abas.addTab("Locações", criarAbaLocacoes());
        abas.addTab("Devoluções", criarAbaDevolucoes());
        conteudo.add(abas, BorderLayout.CENTER);
        setContentPane(conteudo);

        setRenderer(clienteSelecao);
        setRenderer(veiculoSelecao);
        atualizarTelas();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                fecharJanela();
            }
        });
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(0, 18));
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Locadora de veículos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(TEXTO);

        JLabel descricao = new JLabel("Gerencie clientes, frota e locações em um só lugar.");
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descricao.setForeground(TEXTO_SECUNDARIO);

        JPanel textos = new JPanel(new GridLayout(0, 1, 0, 5));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(descricao);
        cabecalho.add(textos, BorderLayout.NORTH);

        JPanel indicadores = new JPanel(new GridLayout(1, 4, 12, 0));
        indicadores.setOpaque(false);
        indicadores.add(criarCartaoMetrica("Clientes", clientesContagem));
        indicadores.add(criarCartaoMetrica("Veículos na frota", veiculosContagem));
        indicadores.add(criarCartaoMetrica("Disponíveis", disponiveisContagem));
        indicadores.add(criarCartaoMetrica("Locações ativas", locacoesContagem));
        cabecalho.add(indicadores, BorderLayout.SOUTH);
        return cabecalho;
    }

    private JPanel criarCartaoMetrica(String titulo, JLabel valor) {
        JPanel cartao = new JPanel(new BorderLayout(0, 4));
        cartao.setBackground(SUPERFICIE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rotulo.setForeground(TEXTO_SECUNDARIO);
        valor.setFont(new Font("SansSerif", Font.BOLD, 20));
        valor.setForeground(DESTAQUE);
        cartao.add(rotulo, BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);
        return cartao;
    }

    private JMenuBar criarMenu() {
        JMenuBar barra = new JMenuBar();
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA));
        barra.setBackground(SUPERFICIE);
        JMenu menuArquivo = new JMenu("Arquivo");
        menuArquivo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JMenuItem salvar = new JMenuItem("Salvar agora");
        salvar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        salvar.addActionListener(evento -> salvarDados(true));
        menuArquivo.add(salvar);
        barra.add(menuArquivo);
        return barra;
    }

    private JPanel criarAbaClientes() {
        JPanel painel = criarPainelAba();
        JTextField nome = new JTextField();
        JTextField telefone = new JTextField();
        JButton cadastrar = new JButton("Cadastrar cliente");

        prepararCampo(nome);
        prepararCampo(telefone);
        JPanel campos = criarPainelCampos();
        adicionarCampo(campos, "Nome", nome);
        adicionarCampo(campos, "Telefone", telefone);
        prepararBotao(cadastrar);

        cadastrar.addActionListener(evento -> {
            try {
                Cliente cliente = locadora.cadastrarCliente(nome.getText(), telefone.getText());
                atualizarTelas();
                salvarAposOperacao("Cliente cadastrado: " + cliente.getNome()
                        + " (código " + cliente.getCodigo() + ").");
                nome.setText("");
                telefone.setText("");
            } catch (IllegalArgumentException erro) {
                exibirErro(erro);
            }
        });

        painel.add(criarCartaoFormulario("Novo cliente",
                "Informe os dados para cadastrar um cliente.", campos, cadastrar), BorderLayout.WEST);
        painel.add(criarCartaoTabela("Clientes cadastrados",
                "Lista de clientes registrados na locadora.", clientesTabela), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaVeiculos() {
        JPanel painel = criarPainelAba();
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Carro", "Moto"});
        JTextField placa = new JTextField();
        JTextField modelo = new JTextField();
        JTextField diaria = new JTextField();
        JButton cadastrar = new JButton("Cadastrar veículo");

        prepararCampo(placa);
        prepararCampo(modelo);
        prepararCampo(diaria);
        prepararCombo(tipo);
        JPanel campos = criarPainelCampos();
        adicionarCampo(campos, "Tipo de veículo", tipo);
        adicionarCampo(campos, "Placa", placa);
        adicionarCampo(campos, "Modelo", modelo);
        adicionarCampo(campos, "Diária (ex.: 120,00)", diaria);
        prepararBotao(cadastrar);

        cadastrar.addActionListener(evento -> {
            try {
                BigDecimal valorDiaria = new BigDecimal(diaria.getText().trim().replace(',', '.'));
                Veiculo veiculo = "Carro".equals(tipo.getSelectedItem())
                        ? new Carro(placa.getText(), modelo.getText(), valorDiaria)
                        : new Moto(placa.getText(), modelo.getText(), valorDiaria);
                locadora.cadastrarVeiculo(veiculo);
                atualizarTelas();
                salvarAposOperacao("Veículo cadastrado: " + veiculo.getPlaca() + " - "
                        + veiculo.getModelo() + ".");
                placa.setText("");
                modelo.setText("");
                diaria.setText("");
            } catch (IllegalArgumentException erro) {
                exibirErro(erro);
            }
        });

        painel.add(criarCartaoFormulario("Novo veículo",
                "Cadastre um carro ou uma moto na frota.", campos, cadastrar), BorderLayout.WEST);
        painel.add(criarCartaoTabela("Frota de veículos",
                "A disponibilidade é atualizada após cada locação ou devolução.", veiculosTabela),
                BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaLocacoes() {
        JPanel painel = criarPainelAba();
        JTextField dias = new JTextField();
        JButton alugar = new JButton("Alugar");

        prepararCampo(dias);
        prepararCombo(clienteSelecao);
        prepararCombo(veiculoSelecao);
        JPanel campos = criarPainelCampos();
        adicionarCampo(campos, "Cliente", clienteSelecao);
        adicionarCampo(campos, "Veículo disponível", veiculoSelecao);
        adicionarCampo(campos, "Quantidade de dias", dias);
        prepararBotao(alugar);

        alugar.addActionListener(evento -> {
            Cliente cliente = (Cliente) clienteSelecao.getSelectedItem();
            Veiculo veiculo = (Veiculo) veiculoSelecao.getSelectedItem();
            if (cliente == null || veiculo == null) {
                exibirErro("Cadastre ou selecione um cliente e um veículo disponível.");
                return;
            }

            try {
                int quantidadeDias = Integer.parseInt(dias.getText().trim());
                Locacao locacao = locadora.realizarLocacao(
                        cliente.getCodigo(), veiculo.getPlaca(), quantidadeDias);
                atualizarTelas();
                salvarAposOperacao("Locação #" + locacao.getCodigo() + " registrada. Total: "
                        + moeda.format(locacao.getValorTotal()) + ".");
                dias.setText("");
            } catch (IllegalArgumentException | IllegalStateException erro) {
                exibirErro(erro);
            }
        });

        painel.add(criarCartaoFormulario("Nova locação",
                "Selecione o cliente, o veículo e o período.", campos, alugar), BorderLayout.WEST);
        painel.add(criarCartaoInformativo("Antes de confirmar",
                "O valor total é calculado pelas regras do veículo selecionado. "
                        + "Carros incluem a taxa de limpeza; motos cobram apenas as diárias."),
                BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaDevolucoes() {
        JPanel painel = criarPainelAba();
        JButton devolver = new JButton("Devolver locação selecionada");
        prepararBotao(devolver);
        devolver.addActionListener(evento -> devolverLocacaoSelecionada());
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        rodape.add(devolver, BorderLayout.EAST);
        painel.add(criarCartaoTabela("Locações ativas",
                "Selecione uma locação para liberar o veículo.", locacoesTabela), BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelAba() {
        JPanel painel = new JPanel(new BorderLayout(18, 18));
        painel.setBackground(FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 8, 8, 8));
        return painel;
    }

    private JPanel criarPainelCampos() {
        JPanel campos = new JPanel();
        campos.setLayout(new javax.swing.BoxLayout(campos, javax.swing.BoxLayout.Y_AXIS));
        campos.setOpaque(false);
        return campos;
    }

    private void adicionarCampo(JPanel campos, String texto, JComponent campo) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(TEXTO);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linha = new JPanel(new BorderLayout(0, 7));
        linha.setOpaque(false);
        linha.add(rotulo, BorderLayout.NORTH);
        linha.add(campo, BorderLayout.CENTER);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        campos.add(linha);
        campos.add(javax.swing.Box.createVerticalStrut(16));
    }

    private JPanel criarCartaoFormulario(String titulo, String descricao, JPanel campos, JButton botao) {
        JPanel cartao = criarCartao();
        cartao.setPreferredSize(new Dimension(310, 100));

        JPanel conteudo = new JPanel(new BorderLayout(0, 22));
        conteudo.setOpaque(false);
        conteudo.add(criarTitulosCartao(titulo, descricao), BorderLayout.NORTH);
        conteudo.add(campos, BorderLayout.CENTER);
        conteudo.add(botao, BorderLayout.SOUTH);
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarCartaoTabela(String titulo, String descricao, JTable tabela) {
        JPanel cartao = criarCartao();
        prepararTabela(tabela);

        JPanel conteudo = new JPanel(new BorderLayout(0, 16));
        conteudo.setOpaque(false);
        conteudo.add(criarTitulosCartao(titulo, descricao), BorderLayout.NORTH);

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.getViewport().setBackground(SUPERFICIE);
        conteudo.add(rolagem, BorderLayout.CENTER);
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarCartaoInformativo(String titulo, String descricao) {
        JPanel cartao = criarCartao();
        cartao.setPreferredSize(new Dimension(330, 100));
        cartao.add(criarTitulosCartao(titulo, descricao), BorderLayout.NORTH);
        return cartao;
    }

    private JPanel criarCartao() {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(SUPERFICIE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)));
        return cartao;
    }

    private JPanel criarTitulosCartao(String titulo, String descricao) {
        JPanel textos = new JPanel(new GridLayout(0, 1, 0, 6));
        textos.setOpaque(false);
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        tituloLabel.setForeground(TEXTO);
        JLabel descricaoLabel = new JLabel("<html><div style='width: 235px'>"
                + descricao + "</div></html>");
        descricaoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descricaoLabel.setForeground(TEXTO_SECUNDARIO);
        textos.add(tituloLabel);
        textos.add(descricaoLabel);
        return textos;
    }

    private void prepararCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);
        campo.setCaretColor(DESTAQUE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        campo.setPreferredSize(new Dimension(220, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    private void prepararCombo(JComboBox<?> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setForeground(TEXTO);
        combo.setBackground(SUPERFICIE);
        combo.setPreferredSize(new Dimension(220, 38));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    private void prepararBotao(JButton botao) {
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setForeground(Color.WHITE);
        botao.setBackground(DESTAQUE);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void prepararTabela(JTable tabela) {
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setForeground(TEXTO);
        tabela.setBackground(SUPERFICIE);
        tabela.setSelectionBackground(new Color(0xDCEFEB));
        tabela.setSelectionForeground(TEXTO);
        tabela.setRowHeight(34);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer linhas = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                          boolean selecionado, boolean foco,
                                                          int linha, int coluna) {
                super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(TEXTO);
                setBackground(selecionado ? tabela.getSelectionBackground()
                        : linha % 2 == 0 ? SUPERFICIE : new Color(0xF8FAFC));
                return this;
            }
        };
        tabela.setDefaultRenderer(Object.class, linhas);

        if (tabela == veiculosTabela) {
            DefaultTableCellRenderer disponibilidade = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                              boolean selecionado, boolean foco,
                                                              int linha, int coluna) {
                    super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
                    boolean disponivel = "Disponível".equals(valor);
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(getFont().deriveFont(Font.BOLD));
                    setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                    setForeground(selecionado ? TEXTO
                            : disponivel ? new Color(0x176B4D) : new Color(0x98621B));
                    setBackground(selecionado ? tabela.getSelectionBackground()
                            : disponivel ? new Color(0xE8F5EE) : new Color(0xFFF4DF));
                    return this;
                }
            };
            tabela.getColumnModel().getColumn(4).setCellRenderer(disponibilidade);
        }

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(new Font("SansSerif", Font.BOLD, 12));
        cabecalho.setForeground(TEXTO_SECUNDARIO);
        cabecalho.setBackground(new Color(0xF6F8FB));
        cabecalho.setPreferredSize(new Dimension(0, 38));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA));
    }

    private void devolverLocacaoSelecionada() {
        int linha = locacoesTabela.getSelectedRow();
        if (linha < 0 || linha >= locacoesAtivas.size()) {
            exibirErro("Selecione uma locação ativa para devolver.");
            return;
        }

        try {
            Locacao locacao = locacoesAtivas.get(linha);
            locadora.devolverLocacao(locacao.getCodigo());
            atualizarTelas();
            salvarAposOperacao("Locação #" + locacao.getCodigo()
                    + " devolvida. Veículo disponível novamente.");
        } catch (IllegalArgumentException | IllegalStateException erro) {
            exibirErro(erro);
        }
    }

    private void atualizarTelas() {
        List<Cliente> clientesLista = locadora.listarClientes();
        List<Veiculo> veiculosLista = locadora.listarVeiculos();
        List<Veiculo> veiculosDisponiveis = locadora.listarVeiculosDisponiveis();
        locacoesAtivas = locadora.listarLocacoesAtivas();

        clientesModelo.setRowCount(0);
        for (Cliente cliente : clientesLista) {
            clientesModelo.addRow(new Object[]{
                    cliente.getCodigo(), cliente.getNome(), cliente.getTelefone()
            });
        }

        veiculosModelo.setRowCount(0);
        for (Veiculo veiculo : veiculosLista) {
            veiculosModelo.addRow(new Object[]{
                    veiculo.getPlaca(),
                    veiculo.getModelo(),
                    veiculo.getClass().getSimpleName(),
                    moeda.format(veiculo.getValorDiaria()),
                    veiculo.isDisponivel() ? "Disponível" : "Alugado"
            });
        }

        DefaultComboBoxModel<Cliente> clientes = new DefaultComboBoxModel<>();
        for (Cliente cliente : clientesLista) {
            clientes.addElement(cliente);
        }
        clienteSelecao.setModel(clientes);

        DefaultComboBoxModel<Veiculo> veiculos = new DefaultComboBoxModel<>();
        for (Veiculo veiculo : veiculosDisponiveis) {
            veiculos.addElement(veiculo);
        }
        veiculoSelecao.setModel(veiculos);

        locacoesModelo.setRowCount(0);
        for (Locacao locacao : locacoesAtivas) {
            locacoesModelo.addRow(new Object[]{
                    locacao.getCodigo(),
                    locacao.getCliente().getNome(),
                    locacao.getVeiculo().getPlaca(),
                    locacao.getVeiculo().getModelo(),
                    locacao.getDias(),
                    moeda.format(locacao.getValorTotal())
            });
        }

        clientesContagem.setText(String.valueOf(clientesLista.size()));
        veiculosContagem.setText(String.valueOf(veiculosLista.size()));
        disponiveisContagem.setText(String.valueOf(veiculosDisponiveis.size()));
        locacoesContagem.setText(String.valueOf(locacoesAtivas.size()));
    }

    private void salvarAposOperacao(String mensagemSucesso) {
        try {
            arquivo.salvar(locadora);
            alteracoesPendentes = false;
            JOptionPane.showMessageDialog(this, mensagemSucesso,
                    "Operação concluída", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | SecurityException erro) {
            alteracoesPendentes = true;
            JOptionPane.showMessageDialog(this,
                    mensagemSucesso + "\nNão foi possível salvar os dados: " + erro.getMessage()
                            + "\nA alteração permanece apenas na memória. Use Arquivo > Salvar agora para tentar novamente.",
                    "Falha ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean salvarDados(boolean exibirSucesso) {
        try {
            arquivo.salvar(locadora);
            alteracoesPendentes = false;
            if (exibirSucesso) {
                JOptionPane.showMessageDialog(this, "Dados salvos com sucesso.",
                        "Salvamento concluído", JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (IOException | SecurityException erro) {
            alteracoesPendentes = true;
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar os dados: " + erro.getMessage()
                            + "\nAs alterações permanecem apenas na memória.",
                    "Falha ao salvar", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void fecharJanela() {
        if (!alteracoesPendentes) {
            dispose();
            return;
        }

        int escolha = JOptionPane.showConfirmDialog(this,
                "Há alterações ainda não salvas. Deseja tentar salvar antes de sair?",
                "Alterações pendentes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (escolha == JOptionPane.YES_OPTION && salvarDados(false)) {
            dispose();
        } else if (escolha == JOptionPane.NO_OPTION) {
            dispose();
        }
    }

    private static DefaultTableModel criarModelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    private static void setRenderer(JComboBox<?> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                if (valor instanceof Cliente) {
                    Cliente cliente = (Cliente) valor;
                    setText(cliente.getCodigo() + " - " + cliente.getNome());
                } else if (valor instanceof Veiculo) {
                    Veiculo veiculo = (Veiculo) valor;
                    setText(veiculo.getPlaca() + " - " + veiculo.getModelo()
                            + " (" + veiculo.getClass().getSimpleName() + ")");
                }
                return this;
            }
        });
    }

    private void exibirErro(RuntimeException erro) {
        exibirErro(erro.getMessage());
    }

    private void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Não foi possível concluir",
                JOptionPane.ERROR_MESSAGE);
    }
}
