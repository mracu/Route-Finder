package com.route.finder.controller;

import com.route.finder.model.RouteResponse;
import com.route.finder.service.RouteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RouteService routeService;

    public RoutingController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/{origin}/{destination}")
    public RouteResponse routing(@PathVariable String origin, @PathVariable String destination) {
        return new RouteResponse(routeService.findRoute(origin, destination));
    }
}
