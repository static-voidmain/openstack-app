package org.example.openstack.demo.configures.handler;

import org.example.openstack.demo.data.UserInformation;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class OpenStackAuthenticationSuccess implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserInformation userInformation = (UserInformation) authentication.getDetails();

        request.getSession().setAttribute("unscopedTokenId", userInformation.getUnscopedToken().getId());
        request.getSession().setAttribute("unscopedToken", userInformation.getUnscopedToken());
        request.getSession().setAttribute("scopedTokenId", userInformation.getScopedToken().getId());
        request.getSession().setAttribute("scopedToken", userInformation.getScopedToken());
        request.getSession().setAttribute("tokenExpire", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(userInformation.getUnscopedToken().getExpires()));
        request.getSession().setAttribute("projectId", userInformation.getProjectId());
        request.getSession().setAttribute("projectName", userInformation.getProjectName());
        request.getSession().setAttribute("domain", userInformation.getDomain());

        response.sendRedirect("/server/list");
    }
}
