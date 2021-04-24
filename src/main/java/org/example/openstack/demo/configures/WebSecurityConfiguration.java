package org.example.openstack.demo.configures;

import org.example.openstack.demo.configures.filters.OpenStackFilter;
import org.example.openstack.demo.configures.handler.OpenStackAuthenticationFailure;
import org.example.openstack.demo.configures.handler.OpenStackAuthenticationSuccess;
import org.example.openstack.demo.configures.provider.OpenStackTokenAuthProvider;
import org.openstack4j.api.OSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final OSClient.OSClientV3 adminOsClient;

    @Autowired
    public WebSecurityConfiguration(OSClient.OSClientV3 adminOsClient) {
        this.adminOsClient = adminOsClient;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAt(getOpenStackFilter(), UsernamePasswordAuthenticationFilter.class);
        http
                .headers().frameOptions().sameOrigin()
                .and().formLogin().loginPage("/login")
                .and().logout().logoutUrl("/logout")
                .and().authorizeRequests().antMatchers("/login", "/").permitAll()
                .and().authorizeRequests().antMatchers("/*", "/**/*").authenticated();
        http.csrf().disable();
    }

    private OpenStackFilter getOpenStackFilter() throws Exception {
        OpenStackFilter openStackFilter = new OpenStackFilter(this.authenticationManager());
        openStackFilter.setAuthenticationSuccessHandler(new OpenStackAuthenticationSuccess());
        openStackFilter.setAuthenticationFailureHandler(new OpenStackAuthenticationFailure());
        return openStackFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/i18n/**")
                .antMatchers("/static/**")
                .antMatchers("/css/**")
                .antMatchers("/js/**")
                .antMatchers("/images/**");
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new OpenStackTokenAuthProvider(adminOsClient));
    }
}
