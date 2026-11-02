# Smart City Route Finder — Full-Stack Web Application

A full-stack urban transit pathfinding and graph analysis web application. Built with **Spring Boot 3 (Java 17/21)**, **React (Vite)**, and core **Data Structures & Algorithms (DSA)** in pure Java SE — without third-party graph libraries.

The application models interconnected cities as an undirected weighted graph, executes **Dijkstra's Algorithm** via a min-heap `PriorityQueue` for turn-by-turn shortest driving routes, and provides level-order **Breadth-First Search (BFS)** and branch **Depth-First Search (DFS)** network traversals with dynamic road administration and an interactive SVG map visualizer.

---

## Table of Contents

1. [Architecture & Tech Stack](#architecture--tech-stack)
2. [Data Structures & Algorithms (DSA)](#data-structures--algorithms-dsa)
3. [Project Structure](#project-structure)
4. [Demonstration Data Disclaimer](#demonstration-data-disclaimer)
5. [REST API Documentation](#rest-api-documentation)
6. [Local Development Setup](#local-development-setup)
7. [Running Tests & Builds](#running-tests--builds)
8. [Production Deployment Guide](#production-deployment-guide)
   - [1. GitHub Repository Setup](#1-github-repository-setup)
   - [2. Deploying Backend to Render (Docker)](#2-deploying-backend-to-render-docker)
   - [3. Deploying Frontend to Netlify](#3-deploying-frontend-to-netlify)
9. [Edge Case Handling & Defensive Programming](#edge-case-handling--defensive-programming)

---

## Architecture & Tech Stack

```
┌──────────────────────────────────────────────┐
│            Frontend (React + Vite)           │
│  • Modern responsive UI & CSS (Dark Theme)   │
│  • Interactive SVG Transit Network Canvas    │
│  • Dijkstra Turn-by-Turn Leg Breakdown       │
│  • BFS/DFS Radial & Step-by-Step Explorer    │
│  • Road Administration & Distance Manager    │
└──────────────────────┬───────────────────────┘
                       │ REST JSON (HTTP/HTTPS)
                       ▼
┌──────────────────────────────────────────────┐
│       Backend (Spring Boot 3.2 / Java 17+)   │
│  • Graph Adjacency List (Map<Node, List>)    │
│  • PriorityQueue Min-Heap (Dijkstra)         │
│  • FIFO Queue & HashSet (BFS / DFS)          │
│  • Dynamic Bidirectional Road Management     │
│  • Configurable CORS & Health Monitoring     │
└──────────────────────────────────────────────┘
```

### Technology Highlights
- **Frontend**: React 18, Vite 5, JavaScript (ES2022), Lucide Icons, Modern responsive CSS.
- **Backend**: Java 17/21, Spring Boot 3.2+, Maven, Jakarta Validation.
- **Algorithms**: Dijkstra's Algorithm, Breadth-First Search (BFS), Depth-First Search (DFS).
- **Hosting**: Render (Docker container for backend), Netlify (Vite SPA frontend).

---

## Data Structures & Algorithms (DSA)

| DSA Component | Concrete Implementation | Purpose in Application | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **Graph** | `Map<Node, List<Edge>>` (Adjacency List) | Stores cities (vertices) and connected roads (weighted edges) compactly without $O(V^2)$ matrix overhead. | Edge query: $\mathcal{O}(\text{deg}(V))$ | $\mathcal{O}(V + E)$ |
| **Min-Heap** | `java.util.PriorityQueue` | Powers Dijkstra's algorithm to greedily extract the unvisited vertex with the minimum tentative distance. | Poll / Insert: $\mathcal{O}(\log V)$ | $\mathcal{O}(V)$ |
| **Hash Tables** | `java.util.HashMap` & `LinkedHashMap` | Preserves deterministic city ordering, tracks distances, and records parent pointers for $O(1)$ lookups. | Access / Update: $\mathcal{O}(1)$ | $\mathcal{O}(V)$ |
| **Dynamic Array** | `java.util.ArrayList` | Backs each node's adjacency list and collects reconstructed path coordinates. | Amortized append: $\mathcal{O}(1)$ | $\mathcal{O}(V)$ |
| **FIFO Queue** | `java.util.LinkedList` | Implements Breadth-First Search level-order radial exploration from an origin hub. | Enqueue / Dequeue: $\mathcal{O}(1)$ | $\mathcal{O}(V)$ |
| **Hash Set** | `java.util.HashSet` | Tracks visited cities during Dijkstra, BFS, and DFS to prevent infinite cycles in cyclic graphs. | Add / Contains: $\mathcal{O}(1)$ | $\mathcal{O}(V)$ |
| **Dijkstra's Algorithm** | `RouteAlgorithms.findShortestRoute` | Finds the optimal driving route and calculates turn-by-turn cumulative leg distances. | $\mathcal{O}((V + E) \log V)$ | $\mathcal{O}(V)$ |
| **Breadth-First Search** | `RouteAlgorithms.breadthFirstSearch` | Explores cities radially by edge hop count (Level 0, Level 1, Level 2, etc.). | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |
| **Depth-First Search** | `RouteAlgorithms.depthFirstSearch` | Deep recursive branch exploration for reachability and cycle structure analysis. | $\mathcal{O}(V + E)$ | $\mathcal{O}(V)$ |

---

## Project Structure

```
smart-city-route-finder/
├── backend/                                # Spring Boot 3 REST API
│   ├── src/
│   │   ├── main/java/com/smartcity/routefinder/
│   │   │   ├── SmartCityRouteFinderApplication.java # Spring Boot entrypoint
│   │   │   ├── config/
│   │   │   │   └── CorsConfig.java         # Safe CORS config for local & production
│   │   │   ├── controller/
│   │   │   │   ├── CityRouteController.java # Main routing and traversal endpoints
│   │   │   │   └── HealthController.java   # /api/health monitoring endpoint
│   │   │   ├── dto/                        # Request/Response data transfer objects
│   │   │   ├── model/                      # Pure DSA Graph model & algorithms
│   │   │   │   ├── CityGraph.java          # Adjacency list graph with road logic
│   │   │   │   ├── Edge.java               # Weighted edge
│   │   │   │   ├── Node.java               # City vertex
│   │   │   │   └── RouteAlgorithms.java    # Dijkstra, BFS, DFS implementations
│   │   │   └── service/
│   │   │       └── CityGraphService.java   # Spring Service managing graph state
│   │   └── test/java/com/smartcity/routefinder/
│   │       └── SmartCityRouteFinderApplicationTests.java # Full integration tests
│   ├── pom.xml                             # Maven build descriptor
│   ├── Dockerfile                          # Multi-stage container build for Render
│   └── mvnw                                # Portable Maven wrapper
├── frontend/                               # React + Vite Web Application
│   ├── src/
│   │   ├── components/
│   │   │   ├── Navbar.jsx                  # Header, navigation & live API status pill
│   │   │   ├── RouteFinderCard.jsx         # Origin/destination selectors & leg table
│   │   │   ├── GraphVisualizer.jsx         # Interactive SVG transit network canvas
│   │   │   ├── RoadNetworkView.jsx         # Searchable road directory with edit actions
│   │   │   ├── TraversalExplorer.jsx       # BFS / DFS radial & step-by-step inspector
│   │   │   ├── RoadAdminModal.jsx          # Add & update bidirectional road modal
│   │   │   └── AlertBanner.jsx             # Dismissible status notification banners
│   │   ├── services/
│   │   │   └── api.js                      # Configurable REST API client
│   │   ├── App.jsx                         # Main application dashboard
│   │   ├── App.css                         # Polished responsive styling
│   │   └── main.jsx                        # React root mount
│   ├── package.json
│   ├── vite.config.js                      # Vite config with /api proxy in development
│   ├── .env.example                        # Documentation of environment variables
│   └── netlify.toml                        # Netlify routing & build configuration
├── netlify.toml                            # Root Netlify redirect fallback
├── .gitignore                              # Prevents committing secrets & build outputs
└── README.md                               # Complete documentation & deployment guide
```

---

## Demonstration Data Disclaimer

> **Academic Notice**:
> All road distances and connectivity data included in the initial pre-configured network are illustrative simulation values for academic graph modeling and algorithm verification. They do not represent real-time live GPS distances.

### Pre-loaded Cities (8 Vertices)
`Lucknow`, `Kanpur`, `Bareilly`, `Delhi`, `Agra`, `Varanasi`, `Prayagraj`, `Gorakhpur`.

### Pre-loaded Intercity Road Distances (11 Bidirectional Edges)
- `Delhi <===> Agra` : 210 km
- `Delhi <===> Bareilly` : 255 km
- `Agra <===> Kanpur` : 280 km
- `Agra <===> Bareilly` : 220 km
- `Bareilly <===> Lucknow` : 250 km
- `Kanpur <===> Lucknow` : 90 km
- `Kanpur <===> Prayagraj` : 200 km
- `Lucknow <===> Prayagraj` : 200 km
- `Lucknow <===> Gorakhpur` : 270 km
- `Prayagraj <===> Varanasi` : 125 km
- `Varanasi <===> Gorakhpur` : 200 km

---

## REST API Documentation

### 1. Health Monitoring
- **`GET /api/health`**
  - **Description**: Verifies service uptime, status, and loaded algorithms.
  - **Response (200 OK)**:
    ```json
    {
      "status": "UP",
      "service": "Smart City Route Finder API",
      "timestamp": "2026-11-02T17:00:04Z",
      "algorithms": "Dijkstra, BFS, DFS (Adjacency List Graph)"
    }
    ```

### 2. Cities Directory
- **`GET /api/cities`**
  - **Description**: Returns all registered cities and their connected neighbors.
  - **Response (200 OK)**:
    ```json
    [
      {
        "name": "Delhi",
        "roadCount": 2,
        "connectedCities": ["Agra", "Bareilly"]
      }
    ]
    ```

### 3. Road Network
- **`GET /api/roads`**
  - **Description**: Returns all unique bidirectional roads and distances.
  - **Response (200 OK)**:
    ```json
    [
      { "cityA": "Delhi", "cityB": "Agra", "distance": 210 }
    ]
    ```

### 4. Shortest Route Calculation (Dijkstra)
- **`GET /api/route?source={origin}&destination={target}`**
  - **Example**: `GET /api/route?source=Delhi&destination=Varanasi`
  - **Response (200 OK)**:
    ```json
    {
      "source": "Delhi",
      "destination": "Varanasi",
      "totalDistance": 815,
      "numberOfRoads": 4,
      "numberOfStops": 3,
      "path": ["Delhi", "Agra", "Kanpur", "Prayagraj", "Varanasi"],
      "legs": [
        { "fromCity": "Delhi", "toCity": "Agra", "distance": 210, "cumulativeDistance": 210 },
        { "fromCity": "Agra", "toCity": "Kanpur", "distance": 280, "cumulativeDistance": 490 },
        { "fromCity": "Kanpur", "toCity": "Prayagraj", "distance": 200, "cumulativeDistance": 690 },
        { "fromCity": "Prayagraj", "toCity": "Varanasi", "distance": 125, "cumulativeDistance": 815 }
      ],
      "reachable": true,
      "message": "Optimal route calculated successfully using Dijkstra's Algorithm.",
      "disclaimer": "Distances are simulation/demonstration values for academic modeling and not verified live GPS distances."
    }
    ```

### 5. Breadth-First Search (BFS)
- **`GET /api/bfs?start={city}`**
  - **Example**: `GET /api/bfs?start=Delhi`
  - **Response (200 OK)**:
    ```json
    {
      "algorithm": "BFS",
      "startCity": "Delhi",
      "totalVisited": 8,
      "traversalOrder": ["Delhi", "Agra", "Bareilly", "Kanpur", "Lucknow", "Prayagraj", "Gorakhpur", "Varanasi"],
      "details": {
        "Delhi": 0,
        "Agra": 1,
        "Bareilly": 1,
        "Kanpur": 2,
        "Lucknow": 2,
        "Prayagraj": 3,
        "Gorakhpur": 3,
        "Varanasi": 4
      },
      "description": "Breadth-First Search: Explores the graph radially level-by-level using a FIFO Queue."
    }
    ```

### 6. Depth-First Search (DFS)
- **`GET /api/dfs?start={city}`**
  - **Example**: `GET /api/dfs?start=Delhi`
  - **Response (200 OK)**:
    ```json
    {
      "algorithm": "DFS",
      "startCity": "Delhi",
      "totalVisited": 8,
      "traversalOrder": ["Delhi", "Agra", "Kanpur", "Lucknow", "Bareilly", "Prayagraj", "Varanasi", "Gorakhpur"],
      "details": {
        "Delhi": 1,
        "Agra": 2,
        "Kanpur": 3,
        "Lucknow": 4,
        "Bareilly": 5,
        "Prayagraj": 6,
        "Varanasi": 7,
        "Gorakhpur": 8
      },
      "description": "Depth-First Search: Explores deeply along adjacent branches before backtracking using recursion."
    }
    ```

### 7. Add Bidirectional Road
- **`POST /api/roads`**
  - **Request Body**:
    ```json
    {
      "cityA": "Bareilly",
      "cityB": "Gorakhpur",
      "distance": 380
    }
    ```
  - **Response (201 Created)**: Returns created road object.
  - **Conflict (409)**: If the road already exists.

### 8. Update Road Distance
- **`PUT /api/roads`**
  - **Request Body**:
    ```json
    {
      "cityA": "Lucknow",
      "cityB": "Kanpur",
      "distance": 85
    }
    ```
  - **Response (200 OK)**: Returns updated road object.
  - **Not Found (404)**: If no road exists between the two cities.

---

## Local Development Setup

### Prerequisites
- **Java 17 or later** (`java -version` and `javac -version`)
- **Maven 3.8+** (or use `./mvnw` inside `backend/`)
- **Node.js 18+ and npm** (`node -v` and `npm -v`)

### Step 1: Start the Backend (Terminal 1)
```bash
cd backend
mvn clean package -DskipTests
java -jar target/smart-city-route-finder-backend-1.0.0.jar
```
*The Spring Boot server starts on `http://localhost:8080` (or the port defined by `$PORT`).*

### Step 2: Start the Frontend (Terminal 2)
```bash
cd frontend
npm install
npm run dev
```
*Vite launches the local development server at `http://localhost:5173`. Requests to `/api` are automatically proxied to the Spring Boot backend.*

---

## Running Tests & Builds

### Backend Maven Tests
```bash
cd backend
mvn test
```
*Executes all 11 integration and algorithmic tests (Dijkstra optimal path, identical node, invalid city handling, BFS, DFS, road addition, road update, and duplicate conflict verification).*

### Frontend Production Build
```bash
cd frontend
npm run build
```
*Produces optimized production assets in `frontend/dist/`.*

---

## Production Deployment Guide

### 1. GitHub Repository Setup

1. Initialize git at the root of the workspace:
   ```bash
   git init
   git add .
   git commit -m "feat: complete full-stack Smart City Route Finder with Spring Boot and React"
   ```
2. Create a new repository on GitHub (e.g., `smart-city-route-finder`).
3. Push your code:
   ```bash
   git remote add origin https://github.com/<your-username>/smart-city-route-finder.git
   git branch -M main
   git push -u origin main
   ```

---

### 2. Deploying Backend to Render (Docker)

Render supports containerized Spring Boot applications using our pre-configured `backend/Dockerfile`.

1. Go to the [Render Dashboard](https://dashboard.render.com/) and click **New +** > **Web Service**.
2. Connect your GitHub repository: `smart-city-route-finder`.
3. Configure the service settings:
   - **Name**: `smart-city-route-finder-api`
   - **Region**: Choose the closest region to your users (e.g., Singapore, Frankfurt, Oregon).
   - **Branch**: `main`
   - **Root Directory**: `backend`
   - **Runtime**: **Docker**
   - **Instance Type**: Free
4. Add Environment Variables in Render:
   - `PORT`: `8080` *(Render sets this automatically; our Dockerfile binds dynamically)*
   - `CORS_ALLOWED_ORIGINS`: `https://<your-netlify-app-name>.netlify.app,http://localhost:5173`
5. Click **Create Web Service**.
6. When deployment finishes, copy your Render service URL:
   `https://smart-city-route-finder-api.onrender.com`
7. Verify by opening `https://smart-city-route-finder-api.onrender.com/api/health` in your browser. It should return `{"status":"UP", ...}`.

---

### 3. Deploying Frontend to Netlify

1. Go to the [Netlify Dashboard](https://app.netlify.com/) and click **Add new site** > **Import an existing project**.
2. Connect to GitHub and select the `smart-city-route-finder` repository.
3. Configure Netlify Build Settings:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `dist`
4. Configure Netlify Environment Variables:
   - Go to **Site configuration** > **Environment variables** > **Add a variable**.
   - **Key**: `VITE_API_BASE_URL`
   - **Value**: `https://smart-city-route-finder-api.onrender.com` *(Replace with your actual Render backend URL, without trailing slash)*
5. Click **Deploy Site**.
6. When deployed, your frontend live on Netlify will communicate with your Render backend over HTTPS.

---

## Edge Case Handling & Defensive Programming

1. **Integer Overflow Guard**: Dijkstra relaxes edges using `currentDist != Integer.MAX_VALUE && currentDist + edgeWeight < distances.get(v)`, guaranteeing that arithmetic wrapping cannot produce false negative weights.
2. **Disconnected Network Components**: If a city has no path to another (e.g. isolated or island city), Dijkstra gracefully returns `reachable: false` and the frontend renders an explicit warning banner rather than throwing exceptions.
3. **Identical Origin & Destination**: Immediately returns `totalDistance = 0 km` and a single-node path.
4. **Duplicate Road Rejection**: Attempting to add an already existing road returns an HTTP `409 Conflict` error and directs the user to update the road instead.
5. **Self-Loop Prevention**: Adding a road from a city to itself is prohibited and returns an HTTP `400 Bad Request`.
6. **CORS Safety**: Origin domains are explicitly matched via `CorsConfig`, supporting local development and deployed Netlify domains.
