package com.smartcity.routefinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcity.routefinder.dto.RoadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SmartCityRouteFinderApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Health endpoint should return UP status")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", containsString("Smart City Route Finder")));
    }

    @Test
    @DisplayName("Should return all 8 initial cities with connections")
    void testGetAllCities() throws Exception {
        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
                .andExpect(jsonPath("$[*].name", hasItems("Delhi", "Agra", "Lucknow", "Kanpur", "Varanasi")));
    }

    @Test
    @DisplayName("Should return initial roads with distances")
    void testGetAllRoads() throws Exception {
        mockMvc.perform(get("/api/roads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(11))));
    }

    @Test
    @DisplayName("Dijkstra: Delhi to Varanasi should find optimal 815 km route")
    void testShortestRouteDelhiToVaranasi() throws Exception {
        mockMvc.perform(get("/api/route")
                        .param("source", "Delhi")
                        .param("destination", "Varanasi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source", is("Delhi")))
                .andExpect(jsonPath("$.destination", is("Varanasi")))
                .andExpect(jsonPath("$.reachable", is(true)))
                .andExpect(jsonPath("$.totalDistance", is(815)))
                .andExpect(jsonPath("$.numberOfRoads", is(4)))
                .andExpect(jsonPath("$.path", contains("Delhi", "Agra", "Kanpur", "Prayagraj", "Varanasi")))
                .andExpect(jsonPath("$.legs", hasSize(4)));
    }

    @Test
    @DisplayName("Dijkstra: Identical source and destination should return 0 km")
    void testShortestRouteIdenticalCities() throws Exception {
        mockMvc.perform(get("/api/route")
                        .param("source", "Delhi")
                        .param("destination", "Delhi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reachable", is(true)))
                .andExpect(jsonPath("$.totalDistance", is(0)))
                .andExpect(jsonPath("$.path", contains("Delhi")));
    }

    @Test
    @DisplayName("Dijkstra: Invalid city name should return 404")
    void testShortestRouteInvalidCity() throws Exception {
        mockMvc.perform(get("/api/route")
                        .param("source", "Atlantis")
                        .param("destination", "Delhi"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Atlantis")));
    }

    @Test
    @DisplayName("BFS traversal from Delhi should discover cities level-by-level")
    void testBfsTraversal() throws Exception {
        mockMvc.perform(get("/api/bfs").param("start", "Delhi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("BFS")))
                .andExpect(jsonPath("$.startCity", is("Delhi")))
                .andExpect(jsonPath("$.totalVisited", greaterThanOrEqualTo(8)))
                .andExpect(jsonPath("$.traversalOrder[0]", is("Delhi")))
                .andExpect(jsonPath("$.details.Delhi", is(0)))
                .andExpect(jsonPath("$.details.Agra", is(1)))
                .andExpect(jsonPath("$.details.Bareilly", is(1)));
    }

    @Test
    @DisplayName("DFS traversal from Delhi should traverse deeply")
    void testDfsTraversal() throws Exception {
        mockMvc.perform(get("/api/dfs").param("start", "Delhi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("DFS")))
                .andExpect(jsonPath("$.startCity", is("Delhi")))
                .andExpect(jsonPath("$.totalVisited", greaterThanOrEqualTo(8)))
                .andExpect(jsonPath("$.traversalOrder[0]", is("Delhi")));
    }

    @Test
    @DisplayName("POST /api/roads: Add new bidirectional road")
    void testAddNewRoad() throws Exception {
        RoadRequest newRoad = new RoadRequest("Bareilly", "Gorakhpur", 380);

        mockMvc.perform(post("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRoad)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.distance", is(380)));

        // Attempt duplicate addition -> should return 409 Conflict
        mockMvc.perform(post("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRoad)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Conflict")));
    }

    @Test
    @DisplayName("PUT /api/roads: Update existing road distance")
    void testUpdateRoadDistance() throws Exception {
        RoadRequest updateReq = new RoadRequest("Lucknow", "Kanpur", 85);

        mockMvc.perform(put("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance", is(85)));

        // Verify updated distance via Dijkstra
        mockMvc.perform(get("/api/route")
                        .param("source", "Lucknow")
                        .param("destination", "Kanpur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDistance", is(85)));
    }

    @Test
    @DisplayName("POST /api/roads: Validation failure for non-positive distance")
    void testInvalidDistanceValidation() throws Exception {
        RoadRequest invalidReq = new RoadRequest("Delhi", "Agra", -50);

        mockMvc.perform(post("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")));
    }
}

