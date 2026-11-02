import React, { useState } from 'react';
import { GitFork, Layers, Compass, Play, Info } from 'lucide-react';

export default function TraversalExplorer({ cities, onRunBfs, onRunDfs, traversalResult, loading }) {
  const [selectedCity, setSelectedCity] = useState('');
  const [algorithm, setAlgorithm] = useState('BFS'); // 'BFS' or 'DFS'

  const handleRun = () => {
    if (!selectedCity) return;
    if (algorithm === 'BFS') {
      onRunBfs(selectedCity);
    } else {
      onRunDfs(selectedCity);
    }
  };

  return (
    <div className="card traversal-card">
      <div className="card-header">
        <div className="card-title-group">
          <GitFork className="card-icon text-purple" size={22} />
          <div>
            <h2 className="card-title">Network Traversal Explorer (BFS & DFS)</h2>
            <p className="card-subtitle">
              Inspect how fundamental graph algorithms explore the transit network from any starting hub.
            </p>
          </div>
        </div>

        <div className="algo-toggle-group">
          <button
            className={`toggle-btn ${algorithm === 'BFS' ? 'active' : ''}`}
            onClick={() => setAlgorithm('BFS')}
          >
            <Layers size={16} />
            <span>BFS (Breadth-First)</span>
          </button>
          <button
            className={`toggle-btn ${algorithm === 'DFS' ? 'active' : ''}`}
            onClick={() => setAlgorithm('DFS')}
          >
            <Compass size={16} />
            <span>DFS (Depth-First)</span>
          </button>
        </div>
      </div>

      <div className="card-body">
        {/* Algorithm Overview Callout */}
        <div className="algo-info-callout">
          <Info size={18} className="text-cyan flex-shrink-0" />
          <div className="text-sm">
            {algorithm === 'BFS' ? (
              <p>
                <strong>Breadth-First Search (Queue-Based):</strong> Explores all immediate neighbors radially (Level 1) before exploring secondary neighbors (Level 2). Guarantees the shortest path in terms of edge hop counts. Time Complexity: <code>O(V + E)</code>.
              </p>
            ) : (
              <p>
                <strong>Depth-First Search (Recursion-Based):</strong> Traverses as deep as possible along each adjacent road branch before backtracking. Ideal for topological sorting, cycle detection, and connectivity exploration. Time Complexity: <code>O(V + E)</code>.
              </p>
            )}
          </div>
        </div>

        {/* Input Bar */}
        <div className="traversal-input-row">
          <div className="input-group flex-1">
            <label className="input-label" htmlFor="traversal-start-city">
              <span>Select Starting City</span>
            </label>
            <select
              id="traversal-start-city"
              className="select-field"
              value={selectedCity}
              onChange={(e) => setSelectedCity(e.target.value)}
              disabled={loading}
            >
              <option value="">-- Choose Starting Hub --</option>
              {cities.map((city) => (
                <option key={city.name} value={city.name}>
                  {city.name} ({city.roadCount} {city.roadCount === 1 ? 'connection' : 'connections'})
                </option>
              ))}
            </select>
          </div>

          <button
            className="btn-primary run-traversal-btn"
            onClick={handleRun}
            disabled={loading || !selectedCity}
          >
            {loading ? (
              <span className="btn-spinner-wrap">
                <span className="spinner"></span>
                <span>Traversing...</span>
              </span>
            ) : (
              <>
                <Play size={16} />
                <span>Run {algorithm} Exploration</span>
              </>
            )}
          </button>
        </div>

        {/* Traversal Output Results */}
        {traversalResult && (
          <div className="traversal-result-box">
            <div className="traversal-summary-row">
              <div>
                <span className="text-muted text-xs uppercase tracking-wider">Algorithm</span>
                <h4 className="text-lg font-bold text-slate">{traversalResult.algorithm} Traversal</h4>
              </div>

              <div>
                <span className="text-muted text-xs uppercase tracking-wider">Starting City</span>
                <p className="font-semibold text-emerald">{traversalResult.startCity}</p>
              </div>

              <div>
                <span className="text-muted text-xs uppercase tracking-wider">Total Cities Visited</span>
                <p className="font-bold text-cyan text-lg">{traversalResult.totalVisited}</p>
              </div>
            </div>

            {/* Sequence Chips */}
            <div className="traversal-sequence-section">
              <span className="section-label">Sequential Discovery Order:</span>
              <div className="traversal-chips-wrap">
                {traversalResult.traversalOrder.map((cityName, idx) => (
                  <div key={cityName} className="traversal-chip">
                    <span className="chip-step-badge">{idx + 1}</span>
                    <span className="chip-city-name">{cityName}</span>
                    {traversalResult.details && traversalResult.details[cityName] !== undefined && (
                      <span className="chip-extra-badge">
                        {traversalResult.algorithm === 'BFS'
                          ? `${traversalResult.details[cityName]} hop${traversalResult.details[cityName] === 1 ? '' : 's'}`
                          : `Step ${traversalResult.details[cityName]}`}
                      </span>
                    )}
                  </div>
                ))}
              </div>
            </div>

            {/* If BFS: Level-by-level Structure */}
            {traversalResult.algorithm === 'BFS' && traversalResult.details && (
              <div className="bfs-levels-section">
                <span className="section-label">Radial Hop Hierarchy (Level Structure):</span>
                <div className="bfs-levels-grid">
                  {Object.entries(
                    traversalResult.traversalOrder.reduce((acc, cityName) => {
                      const level = traversalResult.details[cityName] ?? 0;
                      if (!acc[level]) acc[level] = [];
                      acc[level].push(cityName);
                      return acc;
                    }, {})
                  ).map(([lvl, cityList]) => (
                    <div key={lvl} className="level-card">
                      <div className="level-header">
                        <span className="level-badge">Level {lvl}</span>
                        <span className="level-subtext">
                          {Number(lvl) === 0 ? 'Origin Node' : `${lvl} road hop${Number(lvl) === 1 ? '' : 's'} away`}
                        </span>
                      </div>
                      <div className="level-cities">
                        {cityList.map((c) => (
                          <span key={c} className="level-city-tag">{c}</span>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

