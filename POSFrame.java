import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class POSFrame extends JFrame {

    private static final String STORE_NAME = "POIN OFF-SALES";
    private static final NumberFormat RUPIAH =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    private JTable productTable;
    private JTable cartTable;
    private JTextField qtyField;
    private JLabel selectedLabel;
    private JLabel totalLabel;
    private JLabel pointsLabel;
    private JTextArea receiptArea;
    private JButton addButton;
    private JButton checkoutButton;
    private JButton printButton;

    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    public POSFrame() {
        setTitle("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initProductData();
        initComponents();
        bindEvents();

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initProductData() {
        products.add(new Product("P001", "Air Mineral 600ml", 3000));
        products.add(new Product("P002", "Kopi Sachet", 5000));
        products.add(new Product("P003", "Roti isi", 8000));
        products.add(new Product("P004", "Snack Keripik", 6000));
        products.add(new Product("P005", "Minuman Botol", 12000));
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());

        productModel = new DefaultTableModel(
                new Object[] {"ID", "Nama Produk", "Harga (Rp)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Product p : products) {
            productModel.addRow(new Object[] {p.getId(), p.getName(), p.getPrice()});
        }
        productTable = new JTable(productModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        selectedLabel = new JLabel("Dipilih: -");
        selectedLabel.setFont(selectedLabel.getFont().deriveFont(Font.BOLD));
        qtyField = new JTextField("1", 4);
        addButton = new JButton("Add to Cart");

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        qtyPanel.add(new JLabel("Qty:"));
        qtyPanel.add(qtyField);
        qtyPanel.add(addButton);

        JPanel productBottom = new JPanel(new BorderLayout());
        productBottom.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));
        productBottom.add(selectedLabel, BorderLayout.NORTH);
        productBottom.add(qtyPanel, BorderLayout.CENTER);

        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Produk"));
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        productPanel.add(productBottom, BorderLayout.SOUTH);

        cartModel = new DefaultTableModel(
                new Object[] {"ID", "Nama Produk", "Qty", "Harga", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);

        totalLabel = new JLabel();
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 18f));
        pointsLabel = new JLabel();
        pointsLabel.setFont(pointsLabel.getFont().deriveFont(14f));
        checkoutButton = new JButton("Checkout");
        printButton = new JButton("Cetak");

        receiptArea = new JTextArea(10, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel summaryPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        summaryPanel.add(totalLabel);
        summaryPanel.add(pointsLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(checkoutButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(printButton);

        JPanel summaryAndButtons = new JPanel(new BorderLayout(0, 6));
        summaryAndButtons.add(summaryPanel, BorderLayout.NORTH);
        summaryAndButtons.add(buttonPanel, BorderLayout.CENTER);

        JPanel receiptPanel = new JPanel(new BorderLayout());
        JLabel receiptTitle = new JLabel("Struk:");
        receiptTitle.setFont(receiptTitle.getFont().deriveFont(Font.BOLD));
        receiptPanel.add(receiptTitle, BorderLayout.NORTH);
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        JPanel cartBottom = new JPanel(new BorderLayout(0, 8));
        cartBottom.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 12));
        cartBottom.add(summaryAndButtons, BorderLayout.NORTH);
        cartBottom.add(receiptPanel, BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        mainPanel.add(productPanel);
        mainPanel.add(cartPanel);
        add(mainPanel, BorderLayout.CENTER);

        updateSummary();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        exitItem.addActionListener(e -> dispose());
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "POIN Off-Sales - Praktikum Java Swing"
                )
        );

        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void bindEvents() {
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                Product selected = products.get(row);
                selectedLabel.setText(
                        "Dipilih: " + selected.getId() + " - " + selected.getName()
                                + " (" + RUPIAH.format(selected.getPrice()) + ")"
                );
            }
        });

        addButton.addActionListener(e -> addSelectedProductToCart());
        checkoutButton.addActionListener(e -> checkout());
        printButton.addActionListener(e -> printReceipt());
    }

    private void addSelectedProductToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Pilih produk terlebih dahulu.",
                    "Produk belum dipilih",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Qty harus berupa bilangan bulat.",
                    "Qty tidak valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            cart.add(new CartItem(products.get(row), qty));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Qty tidak valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        refreshCartTable();
        updateSummary();
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (CartItem item : cart) {
            Product p = item.getProduct();
            cartModel.addRow(new Object[] {
                    p.getId(), p.getName(), item.getQty(), p.getPrice(), item.getSubtotal()
            });
        }
    }

    private int calculateTotal() {
        int total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void updateSummary() {
        int total = calculateTotal();
        int points = total / 1000;     
        totalLabel.setText("Total: " + RUPIAH.format(total));
        pointsLabel.setText("Points: " + points);
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keranjang masih kosong.",
                    "Checkout gagal",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int total = calculateTotal();
        int points = total / 1000;

        StringBuilder receipt = new StringBuilder();
        receipt.append("===== ").append(STORE_NAME).append(" =====\n");
        receipt.append("Toko: Demo Toko\n");
        receipt.append("==========================\n");
        for (CartItem item : cart) {
            Product p = item.getProduct();
            receipt.append(p.getName()).append('\n');
            receipt.append(String.format("  %d x %s = %s%n",
                    item.getQty(),
                    RUPIAH.format(p.getPrice()),
                    RUPIAH.format(item.getSubtotal())));
        }
        receipt.append("--------------------------\n");
        receipt.append("Total  : ").append(RUPIAH.format(total)).append('\n');
        receipt.append("Points : ").append(points).append('\n');
        receipt.append("==========================\n");
        receipt.append("Terima kasih atas kunjungan Anda!\n");
        receiptArea.setText(receipt.toString());
        receiptArea.setCaretPosition(0);

        cart.clear();
        refreshCartTable();
        updateSummary();
    }

    private void printReceipt() {
        if (receiptArea.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Belum ada struk yang dapat dicetak."
            );
            return;
        }
        try {
            receiptArea.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mencetak: " + ex.getMessage()
            );
        }
    }
}
