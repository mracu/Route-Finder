package com.route.finder.service;

import com.route.finder.handling.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {

    private final CountryGraphService graphService;

    public RouteService(CountryGraphService graphService) {
        this.graphService = graphService;
    }

    public List<String> findRoute(String originRaw, String destinationRaw) {
        String origin = normalize(originRaw);
        String destination = normalize(destinationRaw);

        if (origin.equals(destination)) {
            if (!graphService.containsCountry(origin)) {
                throw new BadRequestException("Unknown country code: " + origin);
            }
            return List.of(origin);
        }

        if (!graphService.containsCountry(origin)) {
            throw new BadRequestException("Unknown origin country code: " + origin);
        }
        if (!graphService.containsCountry(destination)) {
            throw new BadRequestException("Unknown destination country code: " + destination);
        }

        // BFS
        ArrayDeque<String> q = new ArrayDeque<>();
        Map<String, String> prev = new HashMap<>(); // child -> parent
        Set<String> visited = new HashSet<>();

        visited.add(origin);
        q.add(origin);

        while (!q.isEmpty()) {
            String cur = q.removeFirst();
            for (String nxt : graphService.neighbors(cur)) {
                if (visited.contains(nxt)) continue;
                visited.add(nxt);
                prev.put(nxt, cur);

                if (nxt.equals(destination)) {
                    return reconstructPath(origin, destination, prev);
                }
                q.addLast(nxt);
            }
        }

        throw new BadRequestException("No land route found from " + origin + " to " + destination);
    }

    private static List<String> reconstructPath(String origin, String destination, Map<String, String> prev) {
        List<String> path = new ArrayList<>();
        String cur = destination;
        path.add(cur);

        while (!cur.equals(origin)) {
            cur = prev.get(cur);
            if (cur == null) break; // safety
            path.add(cur);
        }

        Collections.reverse(path);
        return path;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim().toUpperCase(Locale.ROOT);
    }
}

