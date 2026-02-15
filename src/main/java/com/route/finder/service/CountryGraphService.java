package com.route.finder.service;

import com.route.finder.config.AppConfig;
import com.route.finder.model.Country;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class CountryGraphService {

    private final RestClient restClient;
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    // adjacency list: CCA3 -> neighbors
    private Map<String, Set<String>> graph = Map.of();

    public CountryGraphService(AppConfig appConfig, ObjectMapper objectMapper) {
        this.appConfig = appConfig;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().build();
    }

    @PostConstruct
    public void init() {
        try {
            String json = restClient.get()
                    .uri(appConfig.countriesUrl())
                    .retrieve()
                    .body(String.class);

            Country[] countries = objectMapper.readValue(json, Country[].class);
            buildGraph(countries);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load/parse countries.json", e);
        }
    }

    private void buildGraph(Country[] countries){
        if (countries == null) {
            this.graph = Map.of();
            return;
        }

        // Build graph: ensure undirected edges
        Map<String, Set<String>> g = new HashMap<>();
        Set<String> known = new HashSet<>();
        for (Country c : countries) {
            if (c.cca3() != null) known.add(c.cca3().toUpperCase(Locale.ROOT));
        }

        for (Country c : countries) {
            if (c.cca3() == null) continue;
            String from = c.cca3().toUpperCase(Locale.ROOT);
            g.computeIfAbsent(from, k -> new HashSet<>());

            List<String> borders = (c.borders() == null) ? List.of() : c.borders();
            for (String rawTo : borders) {
                if (rawTo == null) continue;
                String to = rawTo.toUpperCase(Locale.ROOT);

                // uneori pot exista coduri care nu sunt în set (rar), dar filtrăm safe
                if (!known.contains(to)) continue;

                g.computeIfAbsent(from, k -> new HashSet<>()).add(to);
                g.computeIfAbsent(to, k -> new HashSet<>()).add(from);
            }
        }

        // Make immutable snapshot
        Map<String, Set<String>> immutable = new HashMap<>();
        for (var e : g.entrySet()) {
            immutable.put(e.getKey(), Collections.unmodifiableSet(e.getValue()));
        }
        this.graph = Collections.unmodifiableMap(immutable);
    }


    public boolean containsCountry(String cca3) {
        return graph.containsKey(cca3);
    }

    public Set<String> neighbors(String cca3) {
        return graph.getOrDefault(cca3, Set.of());
    }
}

