package org.example.Services;

import org.example.dtos.RequestDto;
import org.example.dtos.ResponseDto;
import org.example.dtos.auth.LoginRequestDto;
import org.example.dtos.auth.UserResponseDto;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class AuthService extends BaseService {

    private final ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    public AuthService(String host, int port) {
        super(host, port);
    }

    public Future<UserResponseDto> login(String usernameOrEmail, String password) {
        return executor.submit(() -> {
            LoginRequestDto loginDto = new LoginRequestDto(usernameOrEmail, password);
            RequestDto request = new RequestDto(
                    "Auth",
                    "login",
                    gson.toJson(loginDto),
                    null
            );

            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) {
                return null;
            }

            return gson.fromJson(response.getData(), UserResponseDto.class);
        });
    }

}
