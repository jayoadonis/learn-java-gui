package jayo.arb.learn_j.java_swing.simple_calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class NormalCalculator extends JFrame implements ActionListener {

    private final JTextField DISPLAY_FIELD_;
    private final StringBuilder CURRENT_EXPRESSION_;

    public NormalCalculator() {
        super.setTitle("Calculator");
        super.setSize(400, 500);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());

        this.CURRENT_EXPRESSION_ = new StringBuilder();

        this.DISPLAY_FIELD_ = new JTextField();
        this.DISPLAY_FIELD_.setFont(new Font("Arial", Font.BOLD, 25));
        this.DISPLAY_FIELD_.setMargin(new Insets(0, 15, 0, 15));
        this.DISPLAY_FIELD_.setEditable(true);
        this.DISPLAY_FIELD_.setHorizontalAlignment(SwingConstants.RIGHT);
        this.DISPLAY_FIELD_.setPreferredSize(new Dimension(this.DISPLAY_FIELD_.getPreferredSize().width, 50));
        this.DISPLAY_FIELD_.setBackground(Color.ORANGE);

        JPanel displayPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 55;
        displayPanel.add(this.DISPLAY_FIELD_, gbc);

        super.add(displayPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "AC", "DEL", "<", ">"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        this.DISPLAY_FIELD_.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent kEv) {
                kEv.consume();

                if (kEv.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    NormalCalculator.this.handleBackspace();
                }
                else if (kEv.getKeyCode() == KeyEvent.VK_DELETE) {
                    NormalCalculator.this.handleDelete();
                }
                else if (kEv.getKeyCode() == KeyEvent.VK_LEFT) {
                    NormalCalculator.this.navigateLeft();
                }
                else if (kEv.getKeyCode() == KeyEvent.VK_RIGHT) {
                    NormalCalculator.this.navigateRight();
                }
            }
            @Override
            public void keyReleased(KeyEvent kEv) {
//                kEv.consume();
            }

            @Override
            public void keyTyped( KeyEvent kEv ) {
//                kEv.consume();
            }
        });

        super.add(buttonPanel, BorderLayout.CENTER);
        super.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent aEv) {
        String command = aEv.getActionCommand();
        switch (command) {
            case "AC":
                this.clearDisplay();
                break;
            case "=":
                try {
                    double result = this.evaluateExpression(this.CURRENT_EXPRESSION_.toString());
                    if( result % 1 == 0 ) { //REM: Check if there's no remainder
                        this.DISPLAY_FIELD_.setText(String.valueOf((long)result));
                    } else
                        this.DISPLAY_FIELD_.setText(String.valueOf(result));
                } catch ( ArithmeticException aEx ) {
                    this.DISPLAY_FIELD_.setText( "Error: " + aEx.getLocalizedMessage() );
//                this.CURRENT_EXPRESSION_.setLength(0);
                } catch (Exception ex) {
                    this.DISPLAY_FIELD_.setText( "Error" );
//                    ex.printStackTrace();
//                this.CURRENT_EXPRESSION_.setLength(0);
                } finally {
                }
                break;
            case "<":
                this.navigateLeft();
                break;
            case ">":
                this.navigateRight();
                break;
            case "DEL":
                this.handleBackspace();
                break;
            default:
                this.CURRENT_EXPRESSION_.append(command);
                this.DISPLAY_FIELD_.setText(this.CURRENT_EXPRESSION_.toString());
                break;
        }

        this.DISPLAY_FIELD_.requestFocus();
    }

//    @Override
//    public void actionPerformed(ActionEvent event) {
//        String command = event.getActionCommand();
//
//        if (command.equals("AC")) {
//            this.this.clearDisplay();
//        } else if (command.equals("=")) {
//            try {
//                double result = this.evaluateExpression(this.CURRENT_EXPRESSION_.toString());
//                if( result % 1 == 0 ) { //REM: Check if there's no remainder
//                    this.DISPLAY_FIELD_.setText(String.valueOf((long)result));
//                } else
//                    this.DISPLAY_FIELD_.setText(String.valueOf(result));
//            } catch ( ArithmeticException aEx ) {
//                this.DISPLAY_FIELD_.setText("Error: " + aEx.getLocalizedMessage());
////                this.CURRENT_EXPRESSION_.setLength(0);
//            } catch (Exception ex) {
//                this.DISPLAY_FIELD_.setText("Error");
////                this.CURRENT_EXPRESSION_.setLength(0);
//            } finally {
//            }
//        }
//        else if( command.equals("<") ) {
//            this.navigateLeft();
//        }
//        else if( command.equals(">") ) {
//            this.navigateRight();
//        }
//        else if( command.equals("DEL") ) {
//            this.this.handleBackspace();
//        }
//        else {
//            this.CURRENT_EXPRESSION_.append(command);
//            this.DISPLAY_FIELD_.setText(this.CURRENT_EXPRESSION_.toString());
//        }
//        this.DISPLAY_FIELD_.requestFocus();
//    }

    private double evaluateExpression(final String expression) {
        double[] valueStack = new double[100]; //REM: Stack for numbers
        char[] operatorStack = new char[100]; //REM: Stack for operators
        int valueStackPointer = 0, operatorStackPointer = 0; //REM: Stack pointers

        int indexExpression = 0;
        int lengthExpression = expression.length();

        while (indexExpression < lengthExpression) {
            char currentChar = expression.charAt(indexExpression);

            if (Character.isDigit(currentChar) || currentChar == '.'
                    || ( indexExpression == 0 && ( currentChar == '-' || currentChar == '+' ) )
            ) {
                int numberStartIndex = indexExpression;
                while ( indexExpression < lengthExpression
                        &&
                        ( Character.isDigit(expression.charAt(indexExpression)) || expression.charAt(indexExpression) == '.')
                ) {
                    indexExpression++;
                }
                if( ( indexExpression == 0 && ( currentChar == '-' || currentChar == '+' ) ) )
                    valueStack[valueStackPointer++] = Double.parseDouble(expression.substring(numberStartIndex, (indexExpression += 2) ) );
                else
                    valueStack[valueStackPointer++] = Double.parseDouble(expression.substring(numberStartIndex, indexExpression));
                indexExpression--; //REM: Adjust indexExpression to the end of the number
            } else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
                while (operatorStackPointer > 0 && this.hasPrecedence(operatorStack[operatorStackPointer - 1], currentChar)) {
                    valueStack[valueStackPointer - 2]
                            = this.applyOperation(
                            operatorStack[--operatorStackPointer],
                            valueStack[valueStackPointer - 2],
                            valueStack[valueStackPointer - 1]
                    );
                    valueStackPointer--;
                }
                operatorStack[operatorStackPointer++] = currentChar;
            }
            indexExpression++;
        }

        while (operatorStackPointer > 0) {
            valueStack[valueStackPointer - 2] = this.applyOperation(
                    operatorStack[--operatorStackPointer],
                    valueStack[valueStackPointer - 2],
                    valueStack[valueStackPointer - 1]
            );
            valueStackPointer--;
        }

        return valueStack[0];
    }
    private boolean hasPrecedence(char topOperator, char currentOperator) {
        return !((topOperator == '+' || topOperator == '-') && (currentOperator == '*' || currentOperator == '/'));
    }

    private double applyOperation(char operator, double operand1, double operand2) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) throw new ArithmeticException("Cannot divide by zero");
                return operand1 / operand2;
//            default:
//                throw new ArithmeticException("Operator cannot identified. Should use either( *, /, +, - ).");
        }
        return 0;
    }

    private double evaluate(String expression) {
        // Implement your evaluation logic here
        return 0;
    }

    private void clearDisplay() {
        this.DISPLAY_FIELD_.setText("");
        this.CURRENT_EXPRESSION_.setLength(0);
    }

    private void handleBackspace() {
        String text = this.DISPLAY_FIELD_.getText();
        int caretPosition = this.DISPLAY_FIELD_.getCaretPosition();
        if (caretPosition > 0) {
            this.DISPLAY_FIELD_.setText(text.substring(0, caretPosition - 1) + text.substring(caretPosition));
            this.DISPLAY_FIELD_.setCaretPosition(caretPosition - 1);
            this.refreshExpression();
        }
    }

    private void refreshExpression() {
        this.CURRENT_EXPRESSION_.setLength(0);
        this.CURRENT_EXPRESSION_.append( this.DISPLAY_FIELD_.getText() );
    }

    private void handleDelete() {
        String text = this.DISPLAY_FIELD_.getText();
        int caretPosition = this.DISPLAY_FIELD_.getCaretPosition();
        if (caretPosition < text.length()) {
            this.DISPLAY_FIELD_.setText(text.substring(0, caretPosition) + text.substring(caretPosition + 1));
            this.DISPLAY_FIELD_.setCaretPosition(caretPosition);
        }
    }

    private void navigateLeft() {
        int caretPosition = this.DISPLAY_FIELD_.getCaretPosition();
        if (caretPosition > 0) {
            this.DISPLAY_FIELD_.setCaretPosition(caretPosition - 1);
        }
    }

    private void navigateRight() {
        int caretPosition = this.DISPLAY_FIELD_.getCaretPosition();
        if (caretPosition < this.DISPLAY_FIELD_.getText().length()) {
            this.DISPLAY_FIELD_.setCaretPosition(caretPosition + 1);
        }
    }

    public static void execute() {

        SwingUtilities.invokeLater(() -> {
            NormalCalculator calculator = new NormalCalculator();
            calculator.setVisible(true);
        });

        return;
    }
}
