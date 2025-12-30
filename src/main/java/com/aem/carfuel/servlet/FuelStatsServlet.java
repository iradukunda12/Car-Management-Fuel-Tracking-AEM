package com.aem.carfuel.servlet;

import com.aem.carfuel.model.FuelStatistics;
import com.aem.carfuel.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@WebServlet(name = "FuelStatsServlet", urlPatterns = "/servlet/fuel-stats")
public class FuelStatsServlet extends HttpServlet {

    private final CarService carService;
    private final ObjectMapper objectMapper;

    @Autowired
    public FuelStatsServlet(CarService carService) {
        this.carService = carService;
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String carId = request.getParameter("carId");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            if (carId == null || carId.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                Map<String, String> error = new HashMap<>();
                error.put("error", "carId parameter is required");

                String jsonError = objectMapper.writeValueAsString(error);
                out.print(jsonError);
                out.flush();
                return;
            }

            FuelStatistics stats = carService.getFuelStatistics(carId);

            response.setStatus(HttpServletResponse.SC_OK);

            String jsonResponse = objectMapper.writeValueAsString(stats);
            out.print(jsonResponse);
            out.flush();

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            String jsonError = objectMapper.writeValueAsString(error);
            out.print(jsonError);
            out.flush();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");

            String jsonError = objectMapper.writeValueAsString(error);
            out.print(jsonError);
            out.flush();
        }
    }
}