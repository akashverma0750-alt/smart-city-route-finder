import React from 'react';
import { ArrowRight, ArrowLeftRight, Navigation, MapPin, Gauge, Flag, AlertTriangle, CheckCircle } from 'lucide-react';

export default function RouteFinderCard({
  cities,
  sourceCity,
  setSourceCity,
  destinationCity,
  setDestinationCity,
  onCalculateRoute,
  routeResult,
  loading,
}) {
  const handleSwap = () => {
    const temp = sourceCity;
    setSourceCity(destinationCity);
    setDestinationCity(temp);
  };

  return (
    <div className="card route-finder-card">
      <div className="card-header">
        <div className="card-title-group">
          <Navigation className="card-icon text-cyan" size={22} />
          <div>
            <h2 className="card-title">Find Shortest Route</h2>
            <p className="card-subtitle">Calculated dynamically using Dijkstra's Algorithm (Min-Heap PriorityQueue)</p>
          </div>
        </div>
        <span className="badge-algo">Dijkstra O((V+E) log V)</span>
      </div>

      <div className="card-body">
        <div className="selector-grid">
          {/* Source City Selector */}
          <div className="input-group">
            <label className="input-label" htmlFor="source-city-select">
              <MapPin size={16} className="text-emerald" />
              <span>Source Location</span>
            </label>
            <select
              id="source-city-select"
              className="select-field"
              value={sourceCity}
              onChange={(e) => setSourceCity(e.target.value)}
              disabled={loading}
            >
              <option value="">-- Choose Origin City --</option>
              {cities.map((city) => (
                <option key={city.name} value={city.name}>
                  {city.name} ({city.roadCount} {city.roadCount === 1 ? 'road' : 'roads'})
                </option>
              ))}
            </select>
          </div>

          {/* Swap Button */}
          <div className="swap-btn-wrap">
            <button
              type="button"
              className="btn-icon-swap"
              onClick={handleSwap}
              disabled={loading || !sourceCity || !destinationCity}
              title="Swap Origin and Destination"
            >
              <ArrowLeftRight size={18} />
            </button>
          </div>

          {/* Destination City Selector */}
          <div className="input-group">
            <label className="input-label" htmlFor="dest-city-select">
              <Flag size={16} className="text-rose" />
              <span>Destination Location</span>
            </label>
            <select
              id="dest-city-select"
              className="select-field"
              value={destinationCity}
              onChange={(e) => setDestinationCity(e.target.value)}
              disabled={loading}
            >
              <option value="">-- Choose Target City --</option>
              {cities.map((city) => (
                <option key={city.name} value={city.name}>
                  {city.name} ({city.roadCount} {city.roadCount === 1 ? 'road' : 'roads'})
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="action-row">
          <button
            className="btn-primary w-full"
            onClick={onCalculateRoute}
            disabled={loading || !sourceCity || !destinationCity}
          >
            {loading ? (
              <span className="btn-spinner-wrap">
                <span className="spinner"></span>
                <span>Calculating Optimal Path...</span>
              </span>
            ) : (
              <>
                <Gauge size={18} />
                <span>Calculate Shortest Route</span>
              </>
            )}
          </button>
        </div>

        {/* Route Result Display */}
        {routeResult && (
          <div className="route-result-container">
            {!routeResult.reachable ? (
              <div className="unreachable-box">
                <AlertTriangle size={24} className="text-amber" />
                <div>
                  <h4 className="unreachable-title">No Connected Route Available</h4>
                  <p className="unreachable-desc">
                    {routeResult.message || `No direct or multi-hop path exists between ${routeResult.source} and ${routeResult.destination}. They are in separate graph components.`}
                  </p>
                </div>
              </div>
            ) : (
              <div className="route-success-box">
                {/* Stat Badges */}
                <div className="stats-grid">
                  <div className="stat-card">
                    <span className="stat-label">Total Road Distance</span>
                    <span className="stat-value text-emerald">{routeResult.totalDistance} <span className="stat-unit">km</span></span>
                  </div>

                  <div className="stat-card">
                    <span className="stat-label">Direct Roads (Edges)</span>
                    <span className="stat-value text-cyan">{routeResult.numberOfRoads}</span>
                  </div>

                  <div className="stat-card">
                    <span className="stat-label">Intermediate Stops</span>
                    <span className="stat-value text-purple">{routeResult.numberOfStops}</span>
                  </div>
                </div>

                {/* Path Breadcrumb */}
                <div className="path-breadcrumb-section">
                  <span className="path-title">Computed Optimal Path:</span>
                  <div className="path-breadcrumb">
                    {routeResult.path.map((city, idx) => (
                      <React.Fragment key={idx}>
                        <span className={`city-chip ${idx === 0 ? 'chip-origin' : idx === routeResult.path.length - 1 ? 'chip-dest' : 'chip-intermediate'}`}>
                          {city}
                        </span>
                        {idx < routeResult.path.length - 1 && (
                          <ArrowRight size={16} className="path-arrow" />
                        )}
                      </React.Fragment>
                    ))}
                  </div>
                </div>

                {/* Turn-by-Turn Leg Table */}
                {routeResult.legs && routeResult.legs.length > 0 && (
                  <div className="legs-table-wrap">
                    <h4 className="legs-title">Turn-by-Turn Leg Breakdown</h4>
                    <table className="legs-table">
                      <thead>
                        <tr>
                          <th>#</th>
                          <th>Departure</th>
                          <th>Arrival</th>
                          <th>Leg Distance</th>
                          <th>Cumulative</th>
                        </tr>
                      </thead>
                      <tbody>
                        {routeResult.legs.map((leg, idx) => (
                          <tr key={idx}>
                            <td>{idx + 1}</td>
                            <td className="font-semibold text-slate">{leg.fromCity}</td>
                            <td className="font-semibold text-slate">{leg.toCity}</td>
                            <td><span className="leg-badge">{leg.distance} km</span></td>
                            <td className="text-emerald font-semibold">{leg.cumulativeDistance} km</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

