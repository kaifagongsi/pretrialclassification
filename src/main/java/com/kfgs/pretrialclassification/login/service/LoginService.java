package com.kfgs.pretrialclassification.login.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginService {
    String login(String username, String password);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
