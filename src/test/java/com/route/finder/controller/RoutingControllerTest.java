package com.route.finder.controller;

import com.route.finder.handling.exceptions.BadRequestException;
import com.route.finder.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoutingController.class)
class RoutingControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean
    RouteService routeService;

    @Test
    void shouldReturnRoute() throws Exception {
        when(routeService.findRoute("CZE", "ITA")).thenReturn(List.of("CZE", "AUT", "ITA"));

        mvc.perform(get("/routing/CZE/ITA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route[0]").value("CZE"))
                .andExpect(jsonPath("$.route[1]").value("AUT"))
                .andExpect(jsonPath("$.route[2]").value("ITA"));
    }

    @Test
    void shouldReturn400WhenNoRouteExists() throws Exception {
        when(routeService.findRoute("ISL", "ITA"))
                .thenThrow(new BadRequestException("No land route found"));

        mvc.perform(get("/routing/ISL/ITA"))
                .andExpect(status().isBadRequest());
    }
}
