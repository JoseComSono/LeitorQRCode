import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;

public class QRCodeReaderGUI extends JFrame {
    private JTextArea dataTextArea;
    private JLabel qrCodeLabel;

    public QRCodeReaderGUI() {
        setTitle("Leitor de QR Code");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        dataTextArea = new JTextArea(10, 20);
        dataTextArea.setEditable(false);
        dataTextArea.setLineWrap(true);
        dataTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(dataTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for additional buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

        JButton selectQRButton = new JButton("Selecionar QR Code");
        selectQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarQRCode();
            }
        });
        buttonPanel.add(selectQRButton);

        // Add the button for selecting categories
        JButton selectCategoryButton = new JButton("Selecionar Categorias");
        selectCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarCategorias();
            }
        });
        buttonPanel.add(selectCategoryButton);

        JButton importDBButton = new JButton("Importar Base de Dados");
        importDBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importarBaseDeDados();
            }
        });
        buttonPanel.add(importDBButton);

        JButton saveCSVButton = new JButton("Salvar CSV");
        saveCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarCSV();
            }
        });
        buttonPanel.add(saveCSVButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        qrCodeLabel = new JLabel();
        mainPanel.add(qrCodeLabel, BorderLayout.NORTH);

        add(mainPanel);
        setVisible(true);
    }

    private void selecionarQRCode() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage image = ImageIO.read(selectedFile);
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result qrCodeResult = new MultiFormatReader().decode(bitmap);
                String qrCodeData = qrCodeResult.getText();

                // Filtrar dados do QR Code
                String dadosFiltrados = filtrarDadosQRCode(qrCodeData);

                qrCodeLabel.setIcon(new ImageIcon(image));
                dataTextArea.setText(dadosFiltrados);
            } catch (IOException | NotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao ler o QR Code.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método para filtrar os dados do QR Code
    private String filtrarDadosQRCode(String qrCodeData) {
        StringBuilder dadosFiltrados = new StringBuilder();
        String[] parametros = qrCodeData.split("\\*");
        for (String parametro : parametros) {
            char chave = parametro.charAt(0);
            String valor = parametro.substring(2);
            switch (chave) {
                case 'A':
                    dadosFiltrados.append("NIF do Emitente: ").append(valor).append("\n");
                    break;
                case 'B':
                    dadosFiltrados.append("NIF do Adquirente: ").append(valor).append("\n");
                    break;
                case 'C':
                    dadosFiltrados.append("País: ").append(valor).append("\n");
                    break;
                case 'F':
                    dadosFiltrados.append("Data: ").append(valor).append("\n");
                    break;
                case 'G':
                    dadosFiltrados.append("Identificação: ").append(valor).append("\n");
                    break;
                case 'H':
                    dadosFiltrados.append("ATCUD: ").append(valor).append("\n");
                    break;
                default:
                    // Chave inválida
                    break;
            }
        }
        return dadosFiltrados.toString();
    }

    private void selecionarCategorias() {
        JFrame frame = new JFrame("Selecionar Categorias");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] options = {"Supermercado", "Saúde", "Estudos"};
        JComboBox<String> categoryComboBox = new JComboBox<>(options);
        formPanel.add(new JLabel("Categoria:"));
        formPanel.add(categoryComboBox);

        JTextField valueField = new JTextField();
        formPanel.add(new JLabel("Valor:"));
        formPanel.add(valueField);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                String enteredValue = valueField.getText();
                double valorSemIVA = Double.parseDouble(enteredValue);
                double iva = obterTaxaIVA(selectedCategory);
                double valorComIVA = valorSemIVA * (1 + (iva / 100));
                JOptionPane.showMessageDialog(frame, "Categoria selecionada: " + selectedCategory + "\nValor inserido (sem IVA): " + enteredValue + "\nTaxa de IVA: " + iva + "%\nValor com IVA: " + String.format("%.2f", valorComIVA), "Seleção de Categorias", JOptionPane.INFORMATION_MESSAGE);
                dataTextArea.append("\nCategoria: " + selectedCategory + "\nValor (sem IVA): " + enteredValue + "\nTaxa de IVA: " + iva + "%\nValor com IVA: " + String.format("%.2f", valorComIVA));
                frame.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private double obterTaxaIVA(String categoria) {
        switch (categoria) {
            case "Supermercado":
                return 13.0;
            case "Saúde":
                return 5.0;
            case "Estudos":
                return 12.0;
            default:
                return 0.0; // Categoria não encontrada, retorna 0%
        }
    }

    private void salvarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar CSV");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave + ".csv")) {
                writer.println("nifemitente;nifadquirente;pais;data;identificacao;atcud;categoria;valorsemiva;taxa;valorcomiva");
                String[] dados = dataTextArea.getText().split("\n");
                StringBuilder linha = new StringBuilder();
                for (String dado : dados) {
                    if (dado.contains(":")) {
                        linha.append(dado.substring(dado.indexOf(":") + 2)).append(";");
                    }
                }
                writer.println(linha.toString());
                writer.close();
                JOptionPane.showMessageDialog(this, "CSV salvo com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao salvar o CSV.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarBaseDeDados() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String url = "jdbc:postgresql://localhost:5432/QRCode";
            String user = "postgres";
            String password = "admin";

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                br.readLine(); // Ignora o cabeçalho

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");

                    String categoria = data[6];
                    double valorSemIVA = Double.parseDouble(data[7]);
                    double taxaIVA = Double.parseDouble(data[8].replace("%", ""));
                    double valorComIVA = Double.parseDouble(data[9].replace(",", "."));

                    // Verifica se a categoria já existe
                    String selectCategorySQL = "SELECT id FROM categorias WHERE nome = ?";
                    PreparedStatement selectCategoryStmt = conn.prepareStatement(selectCategorySQL);
                    selectCategoryStmt.setString(1, categoria);
                    ResultSet rs = selectCategoryStmt.executeQuery();

                    int categoriaId;
                    if (rs.next()) {
                        categoriaId = rs.getInt("id");
                    } else {
                        // Insere a nova categoria
                        String insertCategorySQL = "INSERT INTO categorias (nome, total_despesas, teto_despesa) VALUES (?, 0, 500) RETURNING id";
                        PreparedStatement insertCategoryStmt = conn.prepareStatement(insertCategorySQL);
                        insertCategoryStmt.setString(1, categoria);
                        ResultSet categoryRs = insertCategoryStmt.executeQuery();
                        categoryRs.next();
                        categoriaId = categoryRs.getInt("id");
                    }

                    // Insere a fatura
                    String insertInvoiceSQL = "INSERT INTO fatura (id_categoria, valor, nif_emitente, nif_adquirente, pais, data, identificacao, atcud) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertInvoiceStmt = conn.prepareStatement(insertInvoiceSQL);
                    insertInvoiceStmt.setInt(1, categoriaId);
                    insertInvoiceStmt.setDouble(2, valorComIVA);
                    insertInvoiceStmt.setString(3, data[0]); // NIF Emitente
                    insertInvoiceStmt.setString(4, data[1]); // NIF Adquirente
                    insertInvoiceStmt.setString(5, data[2]); // País
                    insertInvoiceStmt.setString(6, data[3]); // Data
                    insertInvoiceStmt.setString(7, data[4]); // Identificação
                    insertInvoiceStmt.setString(8, data[5]); // ATCUD
                    insertInvoiceStmt.executeUpdate();                    
                }

                br.close();
                JOptionPane.showMessageDialog(this, "Dados importados com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao importar dados para o banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QRCodeReaderGUI();
            }
        });
    }
}
