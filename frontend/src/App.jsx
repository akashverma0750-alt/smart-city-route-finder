import React, { useState, useEffect, useCallback } from 'react';
import Navbar from './components/Navbar';
import AlertBanner from './components/AlertBanner';
import RouteFinderCard from './components/RouteFinderCard';
import GraphVisualizer from './components/GraphVisualizer';
import RoadNetworkView from './components/RoadNetworkView';
import TraversalExplorer from './components/TraversalExplorer';
import RoadAdminModal from './components/RoadAdminModal';
import { api } from './services/api';
import { AlertCircle, RefreshCw, Cpu, Database, Globe } from 'lucide-react';
import './App.css';

export default function App() {
  const [cities, setCities] = useState([]);
  const [roads, setRoads] = useState([]);
  const [sourceCity, setSourceCity] = useState('');
  const [destinationCity, setDestinationCity] = useState('');
  const [routeResult, setRouteResult] = useState(null);
  const [traversalResult, setTraversalResult] = useState(null);

  const [activeTab, setActiveTab] = useState('route'); // 'route', 'roads', 'traversal'
  const [apiStatus, setApiStatus] = useState('checking'); // 'online', 'offline', 'checking'
  const [alert, setAlert] = useState(null);

  const [loadingRoute, setLoadingRoute] = useState(false);
  const [loadingTraversal, setLoadingTraversal] = useState(false);

  // Road Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('add');
  const [preselectedRoad, setPreselectedRoad] = useState(null);

  // Helper to show self-dismissing alerts
  const showAlert = (type, message, duration = 6000) => {
    setAlert({ type, message });
    if (duration > 0) {
      setTimeout(() => {
        setAlert((curr) => (curr && curr.message === message ? null : curr));
      }, duration);
    }
  };

  // Fetch initial graph data
  const fetchData = useCallback(async () => {
    try {
      setApiStatus('checking');
      await api.checkHealth();
      setApiStatus('online');

      const [citiesData, roadsData] = await Promise.all([
        api.getCities(),
        api.getRoads(),
      ]);

      setCities(citiesData);
      setRoads(roadsData);

      // Default sample selections for instant demo
      if (citiesData.length >= 2) {
        if (!sourceCity) setSourceCity('Delhi');
        if (!destinationCity) setDestinationCity('Varanasi');
      }
    } catch (err) {
      setApiStatus('offline');
      showAlert('error', `Cannot connect to Spring Boot backend: ${err.message}. Please verify the backend is running.`, 0);
    }
  }, [sourceCity, destinationCity]);

  useEffect(() => {
    fetchData();
  }, []);

  // Calculate Shortest Route
  const handleCalculateRoute = async () => {
    if (!sourceCity || !destinationCity) {
      showAlert('error', 'Please select both an origin and destination city.');
      return;
    }

    try {
      setLoadingRoute(true);
      setAlert(null);
      const result = await api.getShortestRoute(sourceCity, destinationCity);
      setRouteResult(result);

      if (!result.reachable) {
        showAlert('info', `Locations ${sourceCity} and ${destinationCity} are disconnected.`);
      } else {
        showAlert('success', `Optimal path found: ${result.totalDistance} km (${result.numberOfRoads} roads).`);
      }
    } catch (err) {
      showAlert('error', err.message || 'Failed to calculate shortest route.');
      setRouteResult(null);
    } finally {
      setLoadingRoute(false);
    }
  };

  // Run BFS
  const handleRunBfs = async (startCity) => {
    try {
      setLoadingTraversal(true);
      const res = await api.getBfsTraversal(startCity);
      setTraversalResult(res);
      showAlert('success', `BFS completed: Visited ${res.totalVisited} cities radially from ${startCity}.`);
    } catch (err) {
      showAlert('error', err.message || 'BFS traversal failed.');
    } finally {
      setLoadingTraversal(false);
    }
  };

  // Run DFS
  const handleRunDfs = async (startCity) => {
    try {
      setLoadingTraversal(true);
      const res = await api.getDfsTraversal(startCity);
      setTraversalResult(res);
      showAlert('success', `DFS completed: Visited ${res.totalVisited} cities deeply from ${startCity}.`);
    } catch (err) {
      showAlert('error', err.message || 'DFS traversal failed.');
    } finally {
      setLoadingTraversal(false);
    }
  };

  // Add Road
  const handleAddRoad = async (cityA, cityB, distance) => {
    await api.addRoad(cityA, cityB, distance);
    showAlert('success', `Successfully added bidirectional road: ${cityA} <===> ${cityB} (${distance} km).`);
    await fetchData();
  };

  // Update Road
  const handleUpdateRoad = async (cityA, cityB, distance) => {
    await api.updateRoad(cityA, cityB, distance);
    showAlert('success', `Successfully updated road: ${cityA} <===> ${cityB} to ${distance} km.`);
    await fetchData();
    // Recalculate if active route is affected
    if (routeResult) {
      handleCalculateRoute();
    }
  };

  // Quick select city by clicking on node in SVG map
  const handleSelectCityFromGraph = (cityName) => {
    if (!sourceCity || (sourceCity && destinationCity)) {
      setSourceCity(cityName);
      setDestinationCity('');
      setRouteResult(null);
      showAlert('info', `Selected ${cityName} as Origin. Now select a Destination.`);
    } else if (sourceCity && !destinationCity) {
      if (sourceCity.toLowerCase() === cityName.toLowerCase()) {
        showAlert('info', `${cityName} is already set as Origin. Pick a different Destination.`);
      } else {
        setDestinationCity(cityName);
        showAlert('info', `Selected ${cityName} as Destination. Click 'Calculate Shortest Route' to find the path.`);
      }
    }
  };

  return (
    <div className="app-layout">
      {/* Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onOpenRoadModal={() => {
          setModalMode('add');
          setPreselectedRoad(null);
          setIsModalOpen(true);
        }}
        apiStatus={apiStatus}
        onRefresh={fetchData}
      />

      <main className="main-content">
        <div className="content-container">
          {/* Demonstration Notice Banner */}
          <div className="demo-disclaimer-banner">
            <span className="disclaimer-badge">Demonstration Data</span>
            <span className="disclaimer-text">
              Notice: All road distances in this application are illustrative simulation values for academic graph modeling and not verified live GPS distances.
            </span>
          </div>

          {/* Active Notifications */}
          <AlertBanner alert={alert} onDismiss={() => setAlert(null)} />

          {/* Tab 1: Route Finder & Map */}
          {activeTab === 'route' && (
            <div className="route-tab-grid">
              <div className="route-column-left">
                <RouteFinderCard
                  cities={cities}
                  sourceCity={sourceCity}
                  setSourceCity={setSourceCity}
                  destinationCity={destinationCity}
                  setDestinationCity={setDestinationCity}
                  onCalculateRoute={handleCalculateRoute}
                  routeResult={routeResult}
                  loading={loadingRoute}
                />
              </div>

              <div className="route-column-right">
                <GraphVisualizer
                  cities={cities}
                  roads={roads}
                  routeResult={routeResult}
                  sourceCity={sourceCity}
                  destinationCity={destinationCity}
                  onSelectCity={handleSelectCityFromGraph}
                />
              </div>
            </div>
          )}

          {/* Tab 2: Road Network Directory */}
          {activeTab === 'roads' && (
            <RoadNetworkView
              roads={roads}
              cities={cities}
              onAddRoad={() => {
                setModalMode('add');
                setPreselectedRoad(null);
                setIsModalOpen(true);
              }}
              onEditRoad={(road) => {
                setModalMode('update');
                setPreselectedRoad(road);
                setIsModalOpen(true);
              }}
            />
          )}

          {/* Tab 3: BFS & DFS Explorer */}
          {activeTab === 'traversal' && (
            <TraversalExplorer
              cities={cities}
              onRunBfs={handleRunBfs}
              onRunDfs={handleRunDfs}
              traversalResult={traversalResult}
              loading={loadingTraversal}
            />
          )}
        </div>
      </main>

      {/* Road Admin Modal */}
      <RoadAdminModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        cities={cities}
        roads={roads}
        initialMode={modalMode}
        preselectedRoad={preselectedRoad}
        onAddRoad={handleAddRoad}
        onUpdateRoad={handleUpdateRoad}
      />

      {/* Footer */}
      <footer className="footer">
        <div className="footer-container">
          <div className="footer-left">
            <p className="footer-brand">Smart City Route Finder</p>
            <p className="footer-sub">B.Tech Computer Science Portfolio Project</p>
          </div>

          <div className="footer-tech-stack">
            <span className="tech-badge"><Cpu size={14} /> Java 17+ / Spring Boot 3</span>
            <span className="tech-badge"><Database size={14} /> Adjacency List Graph</span>
            <span className="tech-badge"><Globe size={14} /> React + Vite</span>
          </div>
        </div>
      </footer>
    </div>
  );
}

