package org.example.Presentation.Controllers;

import org.example.dtos.auth.UserResponseDto;
import org.example.Presentation.Observable;
import org.example.Presentation.Views.CarsView;
import org.example.Presentation.Views.LoginView;
import org.example.Presentation.Views.MainView;
import org.example.Services.AuthService;
import org.example.Services.CarService;
import org.example.Utilities.EventType;

import javax.swing.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class LoginController extends Observable {

    private final LoginView loginView;
    private final AuthService authService;

    public LoginController(LoginView loginView, AuthService authService) {
        this.loginView = loginView;
        this.authService = authService;

        this.loginView.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Username or password cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginView.showLoading(true);

        SwingWorker<UserResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected UserResponseDto doInBackground() throws Exception {
                return authService.login(username, password).get();
            }

            @Override
            protected void done() {
                loginView.showLoading(false);
                try {
                    UserResponseDto user = get();
                    if (user != null) {
                        loginView.setVisible(false);
                        openMainView();
                        notifyObservers(EventType.UPDATED, user);
                    } else {
                        JOptionPane.showMessageDialog(loginView, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(loginView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void openMainView() {
        MainView mainView = new MainView();

        String host = "localhost";
        int serverPort = 7000;
        int messagesPort = 7001;


        // Inicializar las vistas que van dentro del main view.
        CarsView carsView = new CarsView(mainView);
        CarService carService = new CarService(host, serverPort);
        new CarsController(carsView, carService);

        Dictionary<String, JPanel> tabs = new Hashtable<>();
        tabs.put("Cars", carsView.getContentPanel());

        // Conectarse al puerto 7001 para escuchar transmisiones del servidor
        mainView.connectToMessages(host, messagesPort);
        mainView.AddTabs(tabs);
        mainView.setVisible(true);
    }
}
