package me.isaiah.shell;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Calc extends JInternalFrame implements ActionListener{
    private JTextField display = new JTextField("0");
    private double result = 0;
    private String operator = "=";
    private boolean calculating = true;
   
    public Calc() {
      setLayout(new BorderLayout());
   
      display.setEditable(false);
      add(display, "North");
   
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(4, 4));
   
      String buttonLabels = "789/456*123-0.=+";
      for (int i = 0; i < buttonLabels.length(); i++) {
        JButton b = new JButton(buttonLabels.substring(i, i + 1));
        panel.add(b);
        b.addActionListener(this);
      }
      add(panel, "Center");
    }
   
    public void actionPerformed(ActionEvent evt) {
      String cmd = evt.getActionCommand();
      if ('0' <= cmd.charAt(0) && cmd.charAt(0) <= '9' || cmd.equals(".")) {
          if (calculating) display.setText(cmd);
          else display.setText(display.getText() + cmd);

          calculating = false;
      } else {
          if (calculating) {
              if (cmd.equals("-")) {
                  display.setText(cmd);
                  calculating = false;
              } else operator = cmd;
          } else {
              double x = Double.parseDouble(display.getText());
              calculate(x);
              operator = cmd;
              calculating = true;
          }
      }
    }
   
    private void calculate(double n) {
      switch (operator) {
          case "+":
              result += n;
              break;
          case "-":
              result -= n;
              break;
          case "*":
              result *= n;
              break;
          case "/":
              result /= n;
              break;
          case "=":
              result = n;
              break;
      }
      display.setText("" + result);
    }
   
  }