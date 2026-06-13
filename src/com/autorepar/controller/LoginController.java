package com.autorepar.controller;

import com.autorepar.dao.UsuarioDAO;
import com.autorepar.model.Usuario;
import com.autorepar.view.DashboardForm;
import com.autorepar.view.LoginForm;
import com.google.common.base.Strings;

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
        
        //validacion con Guava
        if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
            JOptionPane.showMessageDialog(
                    view,
                    "Por favor complete todos los campos",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }
        
        //validacion basica de formato email
        if(!email.contains("@")){
            JOptionPane.showMessageDialog(view, "ingrese un email valido", "Email invalido", JOptionPane.ERROR_MESSAGE);
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
