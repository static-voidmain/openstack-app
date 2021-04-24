package org.example.openstack.demo.configures.filters;

import org.example.openstack.demo.data.OpenStackAuth;
import org.openstack4j.api.exceptions.ClientResponseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenStackFilter extends UsernamePasswordAuthenticationFilter {

    private boolean postOnly = true;
    private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
    private boolean continueChainBeforeSuccessfulAuthentication = false;
    public String obtainDomain(HttpServletRequest request){
        return (String)request.getParameter("domain");
    }

    public OpenStackFilter() {
    }

    public OpenStackFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContinueChainBeforeSuccessfulAuthentication(boolean continueChainBeforeSuccessfulAuthentication) {
        this.continueChainBeforeSuccessfulAuthentication = continueChainBeforeSuccessfulAuthentication;
    }

    /**
     * {@inheritDoc}
     */
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        username = (username != null) ? username : "";
        username = username.trim();
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        String domain = obtainDomain(request);
        domain = (domain != null) ? domain : "";
        domain = domain.trim();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        usernamePasswordAuthenticationToken.setDetails(domain);

        return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    private boolean checkExpire(String tokenExpire) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date tokenExpireDateTime = dateFormat.parse(tokenExpire);
        return tokenExpireDateTime.after(new Date());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if(!requiresAuthentication(httpServletRequest, httpServletResponse)){
            chain.doFilter(request, response);
            return;
        }
        HttpSession httpSession = httpServletRequest.getSession();
        String tokenId = (String) httpSession.getAttribute("unscopedTokenId");
        String tokenExpire = (String) httpSession.getAttribute("tokenExpire");

        if(tokenId != null && tokenExpire != null){
            try{
                if(checkExpire(tokenExpire)){
                    new OpenStackAuth(tokenId); //openstack4j/openstack4j
                    chain.doFilter(httpServletRequest, httpServletResponse);
                }else{
                    unsuccessfulAuthentication(httpServletRequest, httpServletResponse, new AuthenticationServiceException("Token has been expired."));
                }
            } catch (ClientResponseException clientResponseException) {
                unsuccessfulAuthentication(httpServletRequest, httpServletResponse, new AuthenticationServiceException("Token id is invalidated."));
            } catch (ParseException parseException){
                unsuccessfulAuthentication(httpServletRequest, httpServletResponse, new AuthenticationServiceException("Token expire date time format invalidated."));
            }
        }else{
            if(this.postOnly && httpServletRequest.getMethod().equals("POST")){
                String username = this.obtainUsername(httpServletRequest);
                String password = this.obtainPassword(httpServletRequest);
                String domain = this.obtainDomain(httpServletRequest);

                if(username != null && password != null && domain != null){
                    try{
                        Authentication authenticationResult = attemptAuthentication(httpServletRequest, httpServletResponse);
                        if(authenticationResult == null){
                            return;
                        }

                        this.sessionStrategy.onAuthentication(authenticationResult, httpServletRequest, httpServletResponse);

                        if(this.continueChainBeforeSuccessfulAuthentication){
                            chain.doFilter(httpServletRequest, httpServletResponse);
                        }
                        successfulAuthentication(httpServletRequest, httpServletResponse, chain, authenticationResult);
                    } catch( InternalAuthenticationServiceException internalAuthenticationServiceException){
                        unsuccessfulAuthentication(httpServletRequest, httpServletResponse, internalAuthenticationServiceException);
                    } catch ( AuthenticationException authenticationException){
                        unsuccessfulAuthentication(httpServletRequest, httpServletResponse, authenticationException);
                    }
                }else{
                    unsuccessfulAuthentication(httpServletRequest, httpServletResponse, new AuthenticationServiceException("Bye Bye."));
                }
            }else{
                unsuccessfulAuthentication(httpServletRequest, httpServletResponse, new AuthenticationServiceException("Bye Bye."));
            }
        }

        //--------------
    }
}
