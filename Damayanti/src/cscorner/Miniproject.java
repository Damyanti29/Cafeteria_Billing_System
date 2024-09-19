package cscorner;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.sql.*;


public class Miniproject extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea cartArea;
    private JButton loginButton;
    private double totalPrice = 0;
    private JPanel topPanel;
    private String userName = null;
    private int userId = -1;
    
    private final String DB_URL = "jdbc:mysql://localhost:3306/food_ordering_system";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "";
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Miniproject frame = new Miniproject();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Miniproject() {
        setTitle("Food Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        setupFoodOrderingUI();
    }

    private void setupFoodOrderingUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel categoryPanel = createCategoryPanel();
        mainPanel.add(categoryPanel, BorderLayout.WEST);

        JPanel foodPanel = createFoodPanel();
        mainPanel.add(foodPanel, BorderLayout.CENTER);

        JPanel cartPanel = createCartPanel();
        mainPanel.add(cartPanel, BorderLayout.EAST);

        contentPane.add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setForeground(Color.LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel deliveryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deliveryPanel.setBackground(Color.DARK_GRAY);

        JLabel greetingLabel = new JLabel("DamEat");
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JRadioButton deliveryButton = new JRadioButton("Delivery");
        JRadioButton pickupButton = new JRadioButton("Pick Up");
        pickupButton.setSelected(true);
        ButtonGroup deliveryGroup = new ButtonGroup();
        deliveryGroup.add(deliveryButton);
        deliveryGroup.add(pickupButton);
        deliveryButton.setForeground(Color.WHITE);
        pickupButton.setForeground(Color.WHITE);
        deliveryButton.setBackground(Color.DARK_GRAY);
        pickupButton.setBackground(Color.DARK_GRAY);

        deliveryPanel.add(greetingLabel);
        deliveryPanel.add(deliveryButton);
        deliveryPanel.add(pickupButton);

        panel.add(deliveryPanel, BorderLayout.WEST);

        loginButton = new JButton("Login/Sign Up");
        loginButton.setFont(new Font("Tw Cen MT", Font.BOLD, 12));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.LIGHT_GRAY);
        loginButton.addActionListener(e -> showLoginForm());
        panel.add(loginButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] categories = {"All", "Pizza", "Donut", "Burger", "Vegetables", "Sandwich", "Soup"};
        for (String category : categories) {
            JButton categoryButton = new JButton(category);
            categoryButton.setForeground(Color.WHITE);
            categoryButton.setBackground(Color.GRAY);
            categoryButton.setMaximumSize(new Dimension(150, 30));
            panel.add(categoryButton);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        ImageIcon bottomImageIcon = new ImageIcon("images/bottom_image.jpeg");
        Image img = bottomImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);
        JLabel bottomImageLabel = new JLabel(resizedIcon);
        bottomImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(bottomImageLabel);

        return panel;
    }

    private JPanel createFoodPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 15, 15));
        panel.setBackground(Color.DARK_GRAY);

        String[] foodItems = {"Idli", "Pav Bhaji", "Vada Pav", "Burger", "Fried Rice", "Dosa", "Samosa", "Pizza"};
        double[] prices = {40, 60, 50, 150, 130, 60, 320, 705};
        ImageIcon[] icons = new ImageIcon[foodItems.length];

        for (int i = 0; i < foodItems.length; i++) {
            String imagePath = "images/" + foodItems[i].toLowerCase().replace(" ", "") + ".png";
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            icons[i] = new ImageIcon(img);
        }

        for (int i = 0; i < foodItems.length; i++) {
            JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
            itemPanel.setBackground(Color.GRAY);
            itemPanel.setBorder(new LineBorder(Color.BLACK, 1));

            JLabel itemImageLabel = new JLabel(icons[i]);
            itemImageLabel.setHorizontalAlignment(SwingConstants.LEFT);

            JLabel itemLabel = new JLabel("<html>" + foodItems[i] + "<br>Rupees " + prices[i] + "</html>");
            itemLabel.setHorizontalAlignment(SwingConstants.LEFT);
            itemLabel.setForeground(Color.BLACK);
            itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            JButton addButton = new JButton("+ Add");
            addButton.setBackground(Color.GREEN);
            int index = i;
            addButton.addActionListener(e -> addItemToCart(foodItems[index], prices[index]));

            itemPanel.add(itemImageLabel, BorderLayout.WEST);
            itemPanel.add(itemLabel, BorderLayout.CENTER);
            itemPanel.add(addButton, BorderLayout.SOUTH);

            panel.add(itemPanel);
        }

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel cartTitle = new JLabel("Cart:");
        cartTitle.setForeground(Color.WHITE);
        cartTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(cartTitle);

        cartArea = new JTextArea(10, 20);
        cartArea.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        cartArea.setEditable(false);
        cartArea.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(cartArea);
        panel.add(scrollPane);

        JButton subtotalButton = new JButton("Subtotal");
        subtotalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtotalButton.addActionListener(e -> showSubtotal());
        panel.add(subtotalButton);

        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setForeground(Color.WHITE);
        paymentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(paymentLabel);

        JRadioButton cardButton = new JRadioButton("Card");
        cardButton.setBackground(Color.DARK_GRAY);
        cardButton.setForeground(Color.WHITE);
        JRadioButton googlePayButton = new JRadioButton("Google Pay");
        googlePayButton.setForeground(Color.WHITE);
        googlePayButton.setBackground(Color.DARK_GRAY);
        JRadioButton payButton = new JRadioButton("PayPal");
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(Color.DARK_GRAY);
        JRadioButton cashButton = new JRadioButton("Cash On Delivery");
        cashButton.setBackground(Color.DARK_GRAY);
        cashButton.setForeground(Color.WHITE);
        cashButton.setSelected(true);
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(cardButton);
        paymentGroup.add(googlePayButton);
        paymentGroup.add(payButton);
        paymentGroup.add(cashButton);

        panel.add(cardButton);
        panel.add(googlePayButton);
        panel.add(payButton);
        panel.add(cashButton);

        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeOrderButton.setBackground(Color.GREEN);
        placeOrderButton.addActionListener(e -> placeOrder());
        panel.add(placeOrderButton);

        return panel;
    }

    private void showLoginForm() {
        JDialog loginDialog = new JDialog(this, "Login", true);
        loginDialog.getContentPane().setLayout(new BorderLayout());
        loginDialog.setSize(300, 200);
        loginDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        formPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);
        formPanel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField();
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
            	  try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                      // Check if user exists
                      String checkUserQuery = "SELECT id FROM users WHERE phone = ?";
                      PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
                      checkStmt.setString(1, phone);
                      ResultSet rs = checkStmt.executeQuery();

                      if (rs.next()) {
                          // User exists, log them in
                          this.userId = rs.getInt("id");
                          this.userName = name;
                      } else {
                          // New user, insert into the database
                          String insertUserQuery = "INSERT INTO users (name, phone, email) VALUES (?, ?, ?)";
                          PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
                          insertStmt.setString(1, name);
                          insertStmt.setString(2, phone);
                          insertStmt.setString(3, email);
                          insertStmt.executeUpdate();

                          // Get the generated user ID
                          ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                          if (generatedKeys.next()) {
                              this.userId = generatedKeys.getInt(1);
                          }

                          this.userName = name;
                      }

                      loginDialog.dispose();
                      showProfilePanel(name); // Show profile info
                  } catch (SQLException ex) {
                      ex.printStackTrace();
                      JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                  }
              
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        loginDialog.getContentPane().add(formPanel, BorderLayout.CENTER);
        loginDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        loginDialog.setVisible(true);
    }

    private void showProfilePanel(String name) {
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setBackground(Color.DARK_GRAY);

        JLabel nameLabel = new JLabel("Hello, " + name);
        nameLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton .setBackground(Color.RED);
        logoutButton.addActionListener(e -> {
            topPanel.remove(profilePanel); // Remove profile panel on logout
            topPanel.add(loginButton, BorderLayout.EAST); // Re-add the login button
            topPanel.revalidate(); // Refresh the layout
            topPanel.repaint();
            this.userName = null; // Clear the user's name
        });

        profilePanel.add(nameLabel);
        profilePanel.add(logoutButton);

        // Remove the login button and add the profile panel
        topPanel.remove(loginButton);
        topPanel.add(profilePanel, BorderLayout.EAST);

        topPanel.revalidate(); // Refresh layout to show profile panel
        topPanel.repaint();
    }

    private void addItemToCart(String itemName, double price) {
        cartArea.append(itemName + " - Rupees " + price + "\n");
        totalPrice += price;
    }

    private void showSubtotal() {
        JOptionPane.showMessageDialog(this, "Your subtotal is: Rupees " + totalPrice);
    }

    private void placeOrder() {
        if (userName == null) {
            JOptionPane.showMessageDialog(this, "Please log in before placing an order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (totalPrice > 0) {
        	 String couponCode = generateCouponCode();
             String cartDetails = cartArea.getText();
             LocalDateTime now = LocalDateTime.now();

             try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                 // Insert the order into the database
                 String insertOrderQuery = "INSERT INTO orders (user_id, order_details, total_price, coupon_code, order_time) VALUES (?, ?, ?, ?, ?)";
                 PreparedStatement orderStmt = conn.prepareStatement(insertOrderQuery);
                 orderStmt.setInt(1, this.userId);
                 orderStmt.setString(2, cartDetails);
                 orderStmt.setDouble(3, totalPrice);
                 orderStmt.setString(4, couponCode);
                 orderStmt.setTimestamp(5, Timestamp.valueOf(now));
                 orderStmt.executeUpdate();

                 String message = "Order placed! Your coupon code is: " + couponCode + "\n\nCart Details:\n" + cartDetails;
                 JOptionPane.showMessageDialog(this, message);

                 cartArea.setText(""); // Clear the cart after placing the order
                 totalPrice = 0; // Reset total price
             } catch (SQLException ex) {
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
             }
        } else {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateCouponCode() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "FOOD" + now.format(formatter);
    }
}

