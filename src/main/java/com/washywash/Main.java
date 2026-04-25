package com.washywash;

import com.washywash.view.MainMenu;
import com.washywash.view.FormLogin;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });   
    }
}