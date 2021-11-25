/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.config;

import com.es.api.esdb.model.LoggedInUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 *
 * @author anwar
 */
@Configuration
@EnableWebSecurity
public class WebMvcConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Value("${elastic.hosts}")
    private String hosts;

    @Value("${elastic.port}")
    private int port;

    @Value("${elastic.protocol}")
    private String protocol;

    @Value("${elastic.username}")
    private String username;

    @Value("${elastic.password}")
    private String password;
    
    @Value("${aws.es.endpoint}")
    private String endpoint = null;

    @Value("${aws.es.region}")
    private String region = null;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/",
                "/csrf",
                "/error");
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/signin").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "REST Api",
                "This is custom API",
                "1.0",
                "Terms of service",
                new Contact("", "", ""),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        LoggedInUser user = new LoggedInUser();
        user.setUserName("admin");
        user.setPassword("$2a$10$418NSGGNeOGqO0A2xI6bUuyBTG12h/8yj9LB0FnIw1lkdHlDEJ7vq");
        user.setAuthorities(authorities);
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(user);

        return userDetailsManager;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    /*
    @Bean
    public RestHighLevelClient client() {
        String[] esHosts = hosts.split(",");
        HttpHost[] oHosts = new HttpHost[esHosts.length];
        for (int i = 0; i < esHosts.length; i++) {
            oHosts[i] = new HttpHost(esHosts[i], port, protocol);
        }
        RestClientBuilder builder = RestClient.builder(oHosts);

        final CredentialsProvider credentialsProvider
                = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient(builder);
    }
    */
    
    @Bean
    public RestHighLevelClient elasticsearchClient() throws Throwable {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials("anwar-es", "@n4H=Y=8jLrJvw8"));

        //File  file = ResourceUtils.getFile("classpath:ap-south-1-es-amazonaws-com.crt");
        //InputStream is = new FileInputStream(file);
        /*      
        RestClientBuilder builder = 
        RestClient.builder(new HttpHost("search-genie-uqpfr3g4hgzgkkwtvedard7bk4.ap-south-1.es.amazonaws.com", 443, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient(builder);
        */
        
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//        Certificate trustedCa = factory.generateCertificate(is);
//        KeyStore trustStore = KeyStore.getInstance("pkcs12");
//        trustStore.load(null, null);
//        trustStore.setCertificateEntry("ca", trustedCa);
//        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
//            .loadTrustMaterial(trustStore, null);
//        final SSLContext sslContext = sslContextBuilder.build();

        String esUser = username;//System.getenv("ES_USER");
        String esPass = password;//System.getenv("ES_PASSWORD");
        
        String finalStr = esUser + ":" + esPass;
        
        Base64 enc = new Base64();
        String encodedStr = enc.encodeToString(finalStr.getBytes());
        
        Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization","Basic " + encodedStr)};
        
        RestClientBuilder builder = RestClient.builder(
            new HttpHost("search-genie-uqpfr3g4hgzgkkwtvedard7bk4.ap-south-1.es.amazonaws.com", 443, "https"))
            .setHttpClientConfigCallback(new HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                    httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String string, SSLSession ssls) {
                            return true;
                        }
                    });
                    //return httpClientBuilder.setSSLContext(sslContext);
                    return httpClientBuilder;
                }
            })
            .setDefaultHeaders(defaultHeaders);
        
        return new RestHighLevelClient(builder);
    }
}
