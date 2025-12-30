package com.aem.carfuel;

import com.aem.carfuel.servlet.FuelStatsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServletRegistration(FuelStatsServlet servlet) {
        ServletRegistrationBean<FuelStatsServlet> registration =
                new ServletRegistrationBean<>(servlet, "/servlet/fuel-stats");
        registration.setName("FuelStatsServlet");
        return registration;
    }
}