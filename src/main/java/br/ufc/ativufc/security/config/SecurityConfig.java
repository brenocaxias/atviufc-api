package br.ufc.ativufc.security.config;

import br.ufc.ativufc.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http,
            JwtAuthenticationFilter jwtAuthFilter
    ) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ====================================================
                        // LIBERAÇÃO DOS ARQUIVOS ESTÁTICOS (FRONTEND)
                        // ====================================================
                        .requestMatchers(
                            "/",                 // Raiz do site
                            "/index.html",       // Página inicial
                            "/login.html",       // Login
                            "/cadastro.html",    // Cadastro
                            "/recuperacao.html", // Recuperar senha
                            "/sobre.html",       // Páginas institucionais
                            "/recursos.html",
                            "/plataforma.html",  // O HTML carrega, mas os dados via API pedirão token
                            "/style.css",        // Seu CSS principal
                            "/css/**",           // Pasta de CSS (se houver)
                            "/js/**",            // Seus scripts (auth.js, config.js, etc)
                            "/imagens/**",       // Suas logos e ícones
                            "/favicon.ico"       // Ícone da aba
                        ).permitAll()

                        // ====================================================
                        // ENDPOINTS PÚBLICOS DA API (BACKEND)
                        // ====================================================
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password/request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password/confirm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/discentes").permitAll() // Cadastro de aluno
                        .requestMatchers(HttpMethod.POST, "/responsaveis").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cursos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/atividades").permitAll()
                        .requestMatchers(HttpMethod.GET, "/subtipos").permitAll()

                        // H2 Console (Apenas para dev local)
                        .requestMatchers("/h2-console/**").permitAll()

                        // ====================================================
                        // TODO O RESTO EXIGE LOGIN (TOKEN JWT)
                        // ====================================================
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite qualquer origem (Frontend e Backend juntos ou separados)
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}