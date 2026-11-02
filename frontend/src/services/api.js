/**
 * Centralized API service for Smart City Route Finder.
 * Dynamically resolves base URL from VITE_API_BASE_URL or falls back to relative /api.
 */

const RAW_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';
// Clean trailing slash if present
const API_BASE_URL = RAW_BASE_URL.replace(/\/+$/, '');

const getEndpointUrl = (endpoint) => {
  if (!API_BASE_URL) {
    return endpoint;
  }
  return `${API_BASE_URL}${endpoint}`;
};

async function handleResponse(response) {
  const contentType = response.headers.get('content-type');
  const isJson = contentType && contentType.includes('application/json');
  const data = isJson ? await response.json() : await response.text();

  if (!response.ok) {
    const errorMessage = (data && data.message) || (data && data.error) || response.statusText || 'An unexpected error occurred';
    const error = new Error(errorMessage);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

export const api = {
  /**
   * Health check endpoint
   */
  async checkHealth() {
    const url = getEndpointUrl('/api/health');
    const response = await fetch(url);
    return handleResponse(response);
  },

  /**
   * Retrieve all registered cities and connection counts
   */
  async getCities() {
    const url = getEndpointUrl('/api/cities');
    const response = await fetch(url);
    return handleResponse(response);
  },

  /**
   * Retrieve all unique bidirectional roads
   */
  async getRoads() {
    const url = getEndpointUrl('/api/roads');
    const response = await fetch(url);
    return handleResponse(response);
  },

  /**
   * Calculate shortest route using Dijkstra's Algorithm
   */
  async getShortestRoute(source, destination) {
    const params = new URLSearchParams({ source, destination });
    const url = getEndpointUrl(`/api/route?${params.toString()}`);
    const response = await fetch(url);
    return handleResponse(response);
  },

  /**
   * Explore network using Breadth-First Search (BFS)
   */
  async getBfsTraversal(startCity) {
    const params = new URLSearchParams({ start: startCity });
    const url = getEndpointUrl(`/api/bfs?${params.toString()}`);
    const response = await fetch(url);
    return handleResponse(response);
  },

  /**
   * Explore network using Depth-First Search (DFS)
   */
  async getDfsTraversal(startCity) {
    const params = new URLSearchParams({ start: startCity });
    const url = getEndpointUrl(`/api/dfs?${params.toString()}`);
    const response = await fetch(url);
    return handleResponse(response);
  },

  /**
   * Add a new bidirectional road
   */
  async addRoad(cityA, cityB, distance) {
    const url = getEndpointUrl('/api/roads');
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        cityA: cityA.trim(),
        cityB: cityB.trim(),
        distance: Number(distance),
      }),
    });
    return handleResponse(response);
  },

  /**
   * Update the distance of an existing road
   */
  async updateRoad(cityA, cityB, distance) {
    const url = getEndpointUrl('/api/roads');
    const response = await fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        cityA: cityA.trim(),
        cityB: cityB.trim(),
        distance: Number(distance),
      }),
    });
    return handleResponse(response);
  },
};

