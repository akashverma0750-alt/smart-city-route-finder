import React from 'react';
import { Compass, Route, Network, PlusCircle, Activity, GitFork } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab, onOpenRoadModal, apiStatus, onRefresh }) {
  return (
    <header className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <div className="brand-icon">
            <Compass className="icon-pulse" size={26} />
          </div>
          <div>
            <div className="brand-title-wrap">
              <h1 className="brand-title">Smart City Route Finder</h1>
              <span className="brand-badge">DSA Project</span>
            </div>
            <p className="brand-subtitle">Graph Algorithms • Dijkstra • BFS • DFS • Spring Boot & React</p>
          </div>
        </div>

        <nav className="navbar-tabs">
          <button
            className={`tab-btn ${activeTab === 'route' ? 'active' : ''}`}
            onClick={() => setActiveTab('route')}
          >
            <Route size={18} />
            <span>Route Finder & Map</span>
          </button>

          <button
            className={`tab-btn ${activeTab === 'roads' ? 'active' : ''}`}
            onClick={() => setActiveTab('roads')}
          >
            <Network size={18} />
            <span>Road Network</span>
          </button>

          <button
            className={`tab-btn ${activeTab === 'traversal' ? 'active' : ''}`}
            onClick={() => setActiveTab('traversal')}
          >
            <GitFork size={18} />
            <span>BFS / DFS Explorer</span>
          </button>
        </nav>

        <div className="navbar-actions">
          <button className="btn-secondary add-road-btn" onClick={onOpenRoadModal}>
            <PlusCircle size={16} />
            <span>Manage Roads</span>
          </button>

          <div
            className={`api-status-pill ${apiStatus === 'online' ? 'status-online' : apiStatus === 'checking' ? 'status-checking' : 'status-offline'}`}
            title={apiStatus === 'online' ? 'Backend API Connected' : 'Backend API Offline'}
            onClick={onRefresh}
          >
            <span className="status-dot"></span>
            <span className="status-text">
              {apiStatus === 'online' ? 'API Online' : apiStatus === 'checking' ? 'Connecting...' : 'API Offline'}
            </span>
            <Activity size={14} className="status-icon" />
          </div>
        </div>
      </div>
    </header>
  );
}

