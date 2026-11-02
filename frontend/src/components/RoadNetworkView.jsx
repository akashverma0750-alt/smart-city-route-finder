import React, { useState } from 'react';
import { Network, Search, Edit3, PlusCircle, ArrowLeftRight } from 'lucide-react';

export default function RoadNetworkView({ roads, cities, onEditRoad, onAddRoad }) {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredRoads = roads.filter((road) => {
    const term = searchTerm.toLowerCase();
    return road.cityA.toLowerCase().includes(term) || road.cityB.toLowerCase().includes(term);
  });

  const totalKm = roads.reduce((sum, r) => sum + r.distance, 0);

  return (
    <div className="card road-network-card">
      <div className="card-header">
        <div className="card-title-group">
          <Network className="card-icon text-cyan" size={22} />
          <div>
            <h2 className="card-title">Intercity Road Directory</h2>
            <p className="card-subtitle">
              All bidirectional roads and distances currently modeled in the adjacency list graph.
            </p>
          </div>
        </div>

        <div className="network-header-actions">
          <button className="btn-secondary" onClick={onAddRoad}>
            <PlusCircle size={16} />
            <span>Add New Road</span>
          </button>
        </div>
      </div>

      <div className="card-body">
        {/* Metric Badges */}
        <div className="stats-grid mb-6">
          <div className="stat-card">
            <span className="stat-label">Total Cities</span>
            <span className="stat-value text-cyan">{cities.length}</span>
          </div>

          <div className="stat-card">
            <span className="stat-label">Direct Roads</span>
            <span className="stat-value text-emerald">{roads.length}</span>
          </div>

          <div className="stat-card">
            <span className="stat-label">Aggregate Network Length</span>
            <span className="stat-value text-purple">{totalKm} <span className="stat-unit">km</span></span>
          </div>
        </div>

        {/* Search Bar */}
        <div className="search-bar-wrap">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search by city name (e.g. Lucknow, Delhi, Agra)..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Roads Table */}
        <div className="table-responsive">
          <table className="network-table">
            <thead>
              <tr>
                <th>#</th>
                <th>City A</th>
                <th>Connectivity</th>
                <th>City B</th>
                <th>Distance (km)</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredRoads.length === 0 ? (
                <tr>
                  <td colSpan="6" className="text-center py-8 text-muted">
                    No roads found matching "{searchTerm}".
                  </td>
                </tr>
              ) : (
                filteredRoads.map((road, idx) => (
                  <tr key={`${road.cityA}-${road.cityB}`}>
                    <td className="text-muted">{idx + 1}</td>
                    <td className="font-semibold text-slate">{road.cityA}</td>
                    <td>
                      <span className="bidirectional-indicator">
                        <ArrowLeftRight size={14} />
                        <span>Two-Way</span>
                      </span>
                    </td>
                    <td className="font-semibold text-slate">{road.cityB}</td>
                    <td>
                      <span className="distance-badge">{road.distance} km</span>
                    </td>
                    <td className="text-right">
                      <button
                        className="btn-table-action"
                        onClick={() => onEditRoad(road)}
                        title="Update road distance"
                      >
                        <Edit3 size={14} />
                        <span>Edit Distance</span>
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

