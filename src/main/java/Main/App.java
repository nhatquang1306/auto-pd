package Main;

import TextReaders.CoordinatesReader;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class App {
    public static double scale = -1;
    public static final GridBagConstraints gbc = new GridBagConstraints();
    public static Map<String, Integer> keyMap;
    public static Set<Integer> functionKeys;
    public static final Dimension skillDimensions = new Dimension(75, 28);
    public static final Font skillFont = new Font("Verdana", Font.BOLD, 14);
    public static final Color skillColor = new Color(0, 120, 0);
    public static final Color runningColor = new Color(144, 238, 144);
    public static final Dimension uidDimensions = new Dimension(60, 28);
    public static final Insets buttonPadding = new Insets(2, 2, 2, 2);
    public static JTextField[] uidFields;
    public static JButton[] skillButtons, petButtons;
    public static Map<Integer, HWND> handleMap;


    public static void main(String[] args) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration gc = device.getDefaultConfiguration();
        scale = gc.getDefaultTransform().getScaleX();

        JFrame frame = new JFrame("Auto Phản Đồ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(0, 0, 3, 3));
        frame.add(panel);

        initialize();

        String[] titles = new String[] {"UID", "Kỹ năng", "Trợ thủ", "UID", "Kỹ năng", "Trợ thủ"};

        gbc.gridy = 0;
        Dimension labelDimensions = new Dimension(60, 26);
        for (int i = 0; i < 6; i++) {
            JLabel label = new JLabel(titles[i]);
            label.setPreferredSize(labelDimensions);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridx = i;
            panel.add(label, gbc);
        }

        for (int i = 0; i < 5; i++) {
            gbc.gridy = i + 1;
            addAccount(panel, i, 0);
        }

        for (int i = 5; i < 10; i++) {
            gbc.gridy = i - 4;
            addAccount(panel, i, 3);
        }

        gbc.gridy = 6;
        gbc.gridx = 1;
        JButton startButton1 = new JButton("Tỉnh Sư");
        startButton1.setPreferredSize(skillDimensions);
        startButton1.setMargin(buttonPadding);
        startButton1.addActionListener(e -> {
            startTS(startButton1, 0);
        });
        panel.add(startButton1, gbc);

        gbc.gridx = 2;
        JButton startButton2 = new JButton("Phản Đồ");
        startButton2.setPreferredSize(skillDimensions);
        startButton2.setMargin(buttonPadding);
        startButton2.addActionListener(e -> {
            startPD(startButton2, 0);
        });
        panel.add(startButton2, gbc);

        gbc.gridx = 4;
        JButton startButton3 = new JButton("Tỉnh Sư");
        startButton3.setPreferredSize(skillDimensions);
        startButton3.setMargin(buttonPadding);
        startButton3.addActionListener(e -> {
            startTS(startButton3, 5);
        });
        panel.add(startButton3, gbc);

        gbc.gridx = 5;
        JButton startButton4 = new JButton("Phản Đồ");
        startButton4.setPreferredSize(skillDimensions);
        startButton4.setMargin(buttonPadding);
        startButton4.addActionListener(e -> {
            startPD(startButton4, 5);
        });
        panel.add(startButton4, gbc);

        frame.pack();
        frame.setVisible(true);
    }

    private static void addAccount(JPanel panel, int i, int offset) {
        uidFields[i] = new JTextField();
        uidFields[i].setPreferredSize(uidDimensions);

        gbc.gridx = offset;
        panel.add(uidFields[i], gbc);

        JButton[] buttons = new JButton[] {new JButton("F1"), new JButton("F1")};
        for (int j = 0; j < 2; j++) {
            JButton button = buttons[j];
            button.addActionListener(e -> {
                KeyAdapter keyAdapter = new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (functionKeys.contains(e.getKeyCode())) {
                            button.setText(KeyEvent.getKeyText(e.getKeyCode()));
                        } else if (e.getKeyCode() == KeyEvent.VK_C) {
                            button.setText("Chay");
                        } else if (e.getKeyCode() == KeyEvent.VK_X) {
                            button.setText("X");
                        } else if (e.getKeyCode() == KeyEvent.VK_T) {
                            button.setText("Thủ");
                        }
                        button.removeKeyListener(this);
                    }
                };
                button.addKeyListener(keyAdapter);
            });
            button.setFont(skillFont);
            button.setForeground(skillColor);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setPreferredSize(skillDimensions);
            button.setMargin(buttonPadding);
        }

        skillButtons[i] = buttons[0];
        petButtons[i] = buttons[1];

        gbc.gridx = 1 + offset;
        panel.add(skillButtons[i], gbc);
        gbc.gridx = 2 + offset;
        panel.add(petButtons[i], gbc);
    }

    private static void startPD(JButton startButton, int offset) {
        if (startButton.getText().equals("Stop")) {
            return;
        }

        int[] skills = new int[5];
        int[] pets = new int[5];
        HWND[] handles = new HWND[5];
        int[] UIDs = new int[5];

        for (int i = 0; i < 5; i++) {
            int j = i + offset;
            String a = uidFields[j].getText();
            if (a.isBlank()) continue;
            try {
                UIDs[i] = Integer.parseInt(a);
            } catch (NumberFormatException _) {
                uidFields[j].setText("");
                return;
            }

            if (!handleMap.containsKey(UIDs[i])) {
                handleMap = getAllWindows();
                if (!handleMap.containsKey(UIDs[i])) return;
            }
            skills[i] = keyMap.get(skillButtons[j].getText());
            pets[i] = keyMap.get(petButtons[j].getText());
            handles[i] = handleMap.get(UIDs[i]);
        }

        PhanDo phanDo = new PhanDo(skills, pets, handles, scale, startButton);
        startButton.setBackground(runningColor);
        startButton.setText("Stop");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                phanDo.setTerminateFlag();
                startButton.removeActionListener(this);
            }
        };
        startButton.addActionListener(actionListener);

        new Thread(phanDo::run).start();
    }

    private static void startTS(JButton startButton, int offset) {
        if (startButton.getText().equals("Stop")) {
            return;
        }
        int UID;
        String a = uidFields[offset].getText();
        if (a.isBlank()) return;
        try {
            UID = Integer.parseInt(a);
        } catch (NumberFormatException _) {
            uidFields[offset].setText("");
            return;
        }

        if (!handleMap.containsKey(UID)) {
            handleMap = getAllWindows();
            if (!handleMap.containsKey(UID)) return;
        }
        TinhSu tinhSu = new TinhSu(handleMap.get(UID), scale, startButton);
        startButton.setBackground(runningColor);
        startButton.setText("Stop");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tinhSu.setTerminateFlag();
                startButton.removeActionListener(this);
            }
        };
        startButton.addActionListener(actionListener);

        new Thread(tinhSu::run).start();
    }

    public static Map<Integer, HWND> getAllWindows() {
        User32 user32 = User32.INSTANCE;
        Map<Integer, HWND> res = new HashMap<>();
        user32.EnumWindows((hwnd, arg) -> {
            char[] text = new char[100];
            user32.GetWindowText(hwnd, text, 100);
            String title = new String(text).trim();
            if (title.startsWith("http://colongonline.com") && title.endsWith("Kênh 1)")) {
                int UID = 0;
                int index = title.indexOf("[UID: ") + 6;
                while (index < title.length() && Character.isDigit(title.charAt(index))) {
                    UID = UID * 10 + Character.getNumericValue(title.charAt(index));
                    index++;
                }
                res.put(UID, hwnd);
            }
            return true;
        }, null);
        return res;
    }

    public static Map<String, Integer> getKeyMap() {
        Map<String, Integer> keyMap = new HashMap<>();
        keyMap.put("F1", 1);
        keyMap.put("F2", 2);
        keyMap.put("F3", 3);
        keyMap.put("F4", 4);
        keyMap.put("F5", 5);
        keyMap.put("F6", 6);
        keyMap.put("F7", 7);
        keyMap.put("F8", 8);
        keyMap.put("F9", 9);
        keyMap.put("F10", 10);
        keyMap.put("Chay", 0);
        keyMap.put("Thủ", 11);
        keyMap.put("X", 12);
        return keyMap;
    }

    public static void initialize() {
        keyMap = getKeyMap();
        functionKeys = Set.of(KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4,
                KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8, KeyEvent.VK_F9, KeyEvent.VK_F10);
        uidFields = new JTextField[10];
        skillButtons = new JButton[10];
        petButtons = new JButton[10];
        handleMap = getAllWindows();
        gbc.insets = new Insets(3, 3, 0, 0);
    }
}