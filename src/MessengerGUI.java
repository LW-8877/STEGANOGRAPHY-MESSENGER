import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Swing desktop app for the Encrypted Steganography Messenger.
 */
public class MessengerGUI extends JFrame {

    private JTextArea messageArea;
    private JPasswordField passwordField;
    private JLabel carrierLabel;
    private JLabel statusLabel;
    private File carrierFile;   // selected image for encoding

    public MessengerGUI() {
        super("Encrypted Steganography Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top controls ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton chooseCarrierBtn = new JButton("Choose Carrier Image...");
        chooseCarrierBtn.addActionListener(e -> chooseCarrier());
        carrierLabel = new JLabel("No carrier selected");

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0;
        topPanel.add(chooseCarrierBtn, gbc);
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1;
        topPanel.add(carrierLabel, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0;
        topPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx=1; gbc.gridy=1; gbc.weightx=1;
        topPanel.add(passwordField, gbc);

        add(topPanel, BorderLayout.NORTH);

        // --- Center message box ---
        messageArea = new JTextArea(5, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(messageArea);
        add(scroll, BorderLayout.CENTER);

        // --- Bottom buttons + status ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel();
        JButton sendBtn = new JButton("Send (Encode)");
        JButton readBtn = new JButton("Read (Decode)");
        btnPanel.add(sendBtn);
        btnPanel.add(readBtn);
        bottomPanel.add(btnPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status: Ready");
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> doEncode());
        readBtn.addActionListener(e -> doDecode());

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void chooseCarrier() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select Carrier Image (PNG)");
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            carrierFile = fc.getSelectedFile();
            carrierLabel.setText(carrierFile.getName());
        }
    }

    private void doEncode() {
        if (carrierFile == null || !carrierFile.exists()) {
            statusLabel.setText("Status: Choose a carrier image first.");
            return;
        }
        String pwd = new String(passwordField.getPassword());
        String msg = messageArea.getText();
        if (msg == null || msg.isEmpty()) {
            statusLabel.setText("Status: Message empty.");
            return;
        }
        if (pwd == null || pwd.isEmpty()) {
            statusLabel.setText("Status: Password empty.");
            return;
        }
        JFileChooser save = new JFileChooser();
        save.setDialogTitle("Save Encoded Image");
        int res = save.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File outFile = save.getSelectedFile();
        String name = outFile.getName().toLowerCase().endsWith(".png") ? outFile.getAbsolutePath() : outFile.getAbsolutePath()+".png";
        try {
            String encrypted = AESUtil.encrypt(msg, pwd);
            int cap = SteganographyUtil.getCapacity(carrierFile.getAbsolutePath());
            if (encrypted.getBytes().length + 1 > cap) {
                statusLabel.setText("Status: Message too long for carrier (" + cap + " chars max).");
                return;
            }
            SteganographyUtil.encodeMessage(carrierFile.getAbsolutePath(), name, encrypted);
            statusLabel.setText("Status: Encoded -> " + name);
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void doDecode() {
        JFileChooser open = new JFileChooser();
        open.setDialogTitle("Select Encoded Image");
        int res = open.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File enc = open.getSelectedFile();
        String pwd = new String(passwordField.getPassword());
        if (pwd == null || pwd.isEmpty()) {
            statusLabel.setText("Status: Password empty.");
            return;
        }
        try {
            String hidden = SteganographyUtil.decodeMessage(enc.getAbsolutePath());
            String decrypted = AESUtil.decrypt(hidden, pwd);
            JOptionPane.showMessageDialog(this, decrypted, "Decoded Message", JOptionPane.INFORMATION_MESSAGE);
            statusLabel.setText("Status: Decoded OK.");
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MessengerGUI gui = new MessengerGUI();
            gui.setVisible(true);
        });
    }
}
