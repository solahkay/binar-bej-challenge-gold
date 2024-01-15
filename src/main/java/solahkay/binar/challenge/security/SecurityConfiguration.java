package solahkay.binar.challenge.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import solahkay.binar.challenge.enums.UserRole;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final UserDetailsService userDetailsService;

    private static final String[] AUTH_WHITE_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/swagger-resources/**"
    };

    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(AUTH_WHITE_LIST).permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/merchants/online").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/products/{sku}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                .antMatchers("/api/v1/users/{username}").hasRole(UserRole.CUSTOMER.name())
                .antMatchers(HttpMethod.DELETE, "/api/v1/users/delete/{username}").hasRole(UserRole.CUSTOMER.name())
                .antMatchers(HttpMethod.POST, "/api/v1/merchants/register").hasRole(UserRole.USER.name())
                .antMatchers("/api/v1/merchants/{merchantName}").hasRole(UserRole.MERCHANT.name())
                .antMatchers(HttpMethod.POST, "/api/v1/products").hasRole(UserRole.MERCHANT.name())
                .antMatchers(HttpMethod.PATCH, "/api/v1/products/{sku}").hasRole(UserRole.MERCHANT.name())
                .antMatchers(HttpMethod.DELETE, "/api/v1/products/delete/{sku}").hasRole(UserRole.MERCHANT.name())
                .antMatchers("/api/v1/orders").hasRole(UserRole.CUSTOMER.name())
                .antMatchers(HttpMethod.GET, "/api/v1/orders/{orderCode}").hasRole(UserRole.CUSTOMER.name())
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
