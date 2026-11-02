import React, { useMemo } from 'react';
import { Eye, Info, Sparkles } from 'lucide-react';

const DEFAULT_COORDINATES = {
  Delhi: { x: 120, y: 110 },
  Bareilly: { x: 340, y: 100 },
  Agra: { x: 180, y: 240 },
  Kanpur: { x: 330, y: 310 },
  Lucknow: { x: 470, y: 200 },
  Gorakhpur: { x: 670, y: 170 },
  Prayagraj: { x: 530, y: 350 },
  Varanasi: { x: 690, y: 340 },
};

export default function GraphVisualizer({
  cities,
  roads,
  routeResult,
  sourceCity,
  destinationCity,
  onSelectCity,
}) {
  // Compute positions for all cities (default coords or distributed for new cities)
  const nodePositions = useMemo(() => {
    const positions = { ...DEFAULT_COORDINATES };
    const extraCities = cities.filter((c) => !positions[c.name]);

    if (extraCities.length > 0) {
      const centerX = 400;
      const centerY = 230;
      const radius = 180;
      extraCities.forEach((city, idx) => {
        const angle = (idx / extraCities.length) * 2 * Math.PI - Math.PI / 2;
        positions[city.name] = {
          x: Math.round(centerX + radius * Math.cos(angle)),
          y: Math.round(centerY + radius * Math.sin(angle)),
        };
      });
    }
    return positions;
  }, [cities]);

  // Determine active route edges for highlighting
  const activeRouteEdges = useMemo(() => {
    if (!routeResult || !routeResult.reachable || !routeResult.path || routeResult.path.length < 2) {
      return new Set();
    }
    const edgeSet = new Set();
    for (let i = 0; i < routeResult.path.length - 1; i++) {
      const u = routeResult.path[i].toLowerCase();
      const v = routeResult.path[i + 1].toLowerCase();
      edgeSet.add(`${u}---${v}`);
      edgeSet.add(`${v}---${u}`);
    }
    return edgeSet;
  }, [routeResult]);

  const pathNodesSet = useMemo(() => {
    if (!routeResult || !routeResult.reachable || !routeResult.path) return new Set();
    return new Set(routeResult.path.map((name) => name.toLowerCase()));
  }, [routeResult]);

  return (
    <div className="card graph-card">
      <div className="card-header">
        <div className="card-title-group">
          <Eye className="card-icon text-cyan" size={22} />
          <div>
            <h2 className="card-title">Interactive Transit Network Map</h2>
            <p className="card-subtitle">
              Visual graph representation (Adjacency List). Click any city node to set as origin or destination.
            </p>
          </div>
        </div>
        <div className="graph-legend">
          <span className="legend-item"><span className="legend-dot origin-dot"></span> Origin</span>
          <span className="legend-item"><span className="legend-dot dest-dot"></span> Target</span>
          <span className="legend-item"><span className="legend-dot route-dot"></span> Optimal Route</span>
        </div>
      </div>

      <div className="graph-container">
        <svg viewBox="0 0 800 460" className="network-svg">
          <defs>
            {/* Glow filter for active route */}
            <filter id="route-glow" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="3.5" result="blur" />
              <feComposite in="SourceGraphic" in2="blur" operator="over" />
            </filter>
            {/* Radial gradient for nodes */}
            <radialGradient id="node-gradient" cx="30%" cy="30%" r="70%">
              <stop offset="0%" stopColor="#38bdf8" />
              <stop offset="100%" stopColor="#0284c7" />
            </radialGradient>
            <radialGradient id="active-origin-grad" cx="30%" cy="30%" r="70%">
              <stop offset="0%" stopColor="#34d399" />
              <stop offset="100%" stopColor="#059669" />
            </radialGradient>
            <radialGradient id="active-dest-grad" cx="30%" cy="30%" r="70%">
              <stop offset="0%" stopColor="#fb7185" />
              <stop offset="100%" stopColor="#e11d48" />
            </radialGradient>
          </defs>

          {/* 1. Draw Inactive Roads (Background Edges) */}
          {roads.map((road, idx) => {
            const p1 = nodePositions[road.cityA];
            const p2 = nodePositions[road.cityB];
            if (!p1 || !p2) return null;

            const isRouteEdge = activeRouteEdges.has(`${road.cityA.toLowerCase()}---${road.cityB.toLowerCase()}`);
            if (isRouteEdge) return null; // Drawn in foreground

            const midX = (p1.x + p2.x) / 2;
            const midY = (p1.y + p2.y) / 2;

            return (
              <g key={`inactive-edge-${idx}`} className="road-edge-group">
                <line
                  x1={p1.x}
                  y1={p1.y}
                  x2={p2.x}
                  y2={p2.y}
                  className="road-edge-line"
                />
                {/* Distance Badge */}
                <rect
                  x={midX - 22}
                  y={midY - 10}
                  width="44"
                  height="20"
                  rx="6"
                  className="edge-badge-bg"
                />
                <text x={midX} y={midY + 4} className="edge-badge-text">
                  {road.distance} km
                </text>
              </g>
            );
          })}

          {/* 2. Draw Active Shortest Route Edges (Highlighted with glow) */}
          {roads.map((road, idx) => {
            const p1 = nodePositions[road.cityA];
            const p2 = nodePositions[road.cityB];
            if (!p1 || !p2) return null;

            const isRouteEdge = activeRouteEdges.has(`${road.cityA.toLowerCase()}---${road.cityB.toLowerCase()}`);
            if (!isRouteEdge) return null;

            const midX = (p1.x + p2.x) / 2;
            const midY = (p1.y + p2.y) / 2;

            return (
              <g key={`active-edge-${idx}`} className="road-edge-group-active">
                <line
                  x1={p1.x}
                  y1={p1.y}
                  x2={p2.x}
                  y2={p2.y}
                  className="road-edge-active-glow"
                  filter="url(#route-glow)"
                />
                <line
                  x1={p1.x}
                  y1={p1.y}
                  x2={p2.x}
                  y2={p2.y}
                  className="road-edge-active"
                />
                {/* Distance Badge */}
                <rect
                  x={midX - 24}
                  y={midY - 11}
                  width="48"
                  height="22"
                  rx="6"
                  className="edge-badge-bg-active"
                />
                <text x={midX} y={midY + 4} className="edge-badge-text-active">
                  {road.distance} km
                </text>
              </g>
            );
          })}

          {/* 3. Draw City Nodes */}
          {cities.map((city) => {
            const pos = nodePositions[city.name];
            if (!pos) return null;

            const isSource = sourceCity && sourceCity.toLowerCase() === city.name.toLowerCase();
            const isDest = destinationCity && destinationCity.toLowerCase() === city.name.toLowerCase();
            const isInPath = pathNodesSet.has(city.name.toLowerCase());

            let fillGrad = 'url(#node-gradient)';
            let nodeClass = 'city-node';

            if (isSource) {
              fillGrad = 'url(#active-origin-grad)';
              nodeClass = 'city-node node-source';
            } else if (isDest) {
              fillGrad = 'url(#active-dest-grad)';
              nodeClass = 'city-node node-destination';
            } else if (isInPath) {
              nodeClass = 'city-node node-in-path';
            }

            return (
              <g
                key={city.name}
                className="city-node-group"
                onClick={() => onSelectCity && onSelectCity(city.name)}
                style={{ cursor: 'pointer' }}
              >
                {/* Pulse ring for source or destination */}
                {(isSource || isDest) && (
                  <circle
                    cx={pos.x}
                    cy={pos.y}
                    r="24"
                    className={`node-pulse-ring ${isSource ? 'pulse-emerald' : 'pulse-rose'}`}
                  />
                )}

                {/* Node Circle */}
                <circle
                  cx={pos.x}
                  cy={pos.y}
                  r="16"
                  fill={fillGrad}
                  className={nodeClass}
                />

                {/* Node Label Text Background */}
                <rect
                  x={pos.x - 38}
                  y={pos.y + 20}
                  width="76"
                  height="18"
                  rx="4"
                  className="node-label-bg"
                />
                {/* Node Label Text */}
                <text
                  x={pos.x}
                  y={pos.y + 33}
                  className={`node-label ${isSource ? 'label-source' : isDest ? 'label-dest' : isInPath ? 'label-path' : ''}`}
                >
                  {city.name}
                </text>
              </g>
            );
          })}
        </svg>

        <div className="graph-footer-hint">
          <Info size={14} className="text-cyan" />
          <span>
            {routeResult && routeResult.reachable ? (
              <>Showing optimal route: <strong>{routeResult.path.join(' ➔ ')}</strong> ({routeResult.totalDistance} km)</>
            ) : (
              <>Click on any city node above to quickly designate it as an origin or destination.</>
            )}
          </span>
        </div>
      </div>
    </div>
  );
}

