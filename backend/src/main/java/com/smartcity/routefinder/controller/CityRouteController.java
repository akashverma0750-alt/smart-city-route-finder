package com.smartcity.routefinder.controller;

import com.smartcity.routefinder.dto.*;
import com.smartcity.routefinder.service.CityGraphService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class CityRouteController {

    private final CityGraphService cityGraphService;

    public CityRouteController(CityGraphService cityGraphService) {
        this.cityGraphService = cityGraphService;
    }

    @GetMapping("/cities")
    public ResponseEntity<List<CityDto>> getAllCities() {
        return ResponseEntity.ok(cityGraphService.getAllCities());
    }

    @GetMapping("/roads")
    public ResponseEntity<List<RoadResponse>> getAllRoads() {
        return ResponseEntity.ok(cityGraphService.getAllRoads());
    }

    @GetMapping("/route")
    public ResponseEntity<RouteResponse> findShortestRoute(
            @RequestParam(name = "source") String source,
            @RequestParam(name = "destination") String destination) {
        RouteResponse response = cityGraphService.findShortestRoute(source, destination);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bfs")
    public ResponseEntity<BfsDfsResponse> getBfsTraversal(
            @RequestParam(name = "start") String start) {
        BfsDfsResponse response = cityGraphService.getBfsTraversal(start);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dfs")
    public ResponseEntity<BfsDfsResponse> getDfsTraversal(
            @RequestParam(name = "start") String start) {
        BfsDfsResponse response = cityGraphService.getDfsTraversal(start);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/roads")
    public ResponseEntity<RoadResponse> addRoad(@Valid @RequestBody RoadRequest request) {
        RoadResponse response = cityGraphService.addRoad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/roads")
    public ResponseEntity<RoadResponse> updateRoad(@Valid @RequestBody RoadRequest request) {
        RoadResponse response = cityGraphService.updateRoad(request);
        return ResponseEntity.ok(response);
    }

    // ================= Exception Handlers =================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation failed.");
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

