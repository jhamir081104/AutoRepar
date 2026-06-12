package com.autorepar.controller;

import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import com.autorepar.view.DashboardForm;
import com.autorepar.view.LoginForm;

import javax.swing.*;

public class LoginController {

    private LoginForm view;
    private UsuarioDAO usuarioDAO;

    public LoginController(LoginForm view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
    }

    public void realizarLogin() {

        String email = view.getTxtEmail()
                .getText()
                .trim();

        String password = new String(
                view.getTxtPassword().getPassword()
        );

        if (email.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Por favor complete todos los campos",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Usuario usuario = usuarioDAO.login(email, password);

        if (usuario != null) {

            JOptionPane.showMessageDialog(
                    view,
                    "¡Bienvenido " + usuario.getNombre() + "!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

            new DashboardForm(usuario).setVisible(true);
            view.dispose();

        } else {

            JOptionPane.showMessageDialog(
                    view,
                    "Email o contraseña incorrectos",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE
            );

            view.getTxtPassword().setText("");
            view.getTxtPassword().requestFocus();
        }
    }
}
