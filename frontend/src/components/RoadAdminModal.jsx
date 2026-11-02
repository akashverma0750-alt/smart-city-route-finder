import React, { useState, useEffect } from 'react';
import { X, PlusCircle, Edit3, ArrowLeftRight, Check, AlertCircle } from 'lucide-react';

export default function RoadAdminModal({
  isOpen,
  onClose,
  cities,
  roads,
  initialMode = 'add', // 'add' or 'update'
  preselectedRoad = null,
  onAddRoad,
  onUpdateRoad,
}) {
  const [mode, setMode] = useState(initialMode);
  const [cityA, setCityA] = useState('');
  const [cityB, setCityB] = useState('');
  const [distance, setDistance] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (preselectedRoad) {
      setMode('update');
      setCityA(preselectedRoad.cityA);
      setCityB(preselectedRoad.cityB);
      setDistance(preselectedRoad.distance.toString());
      setError('');
    } else {
      setMode(initialMode);
      setCityA('');
      setCityB('');
      setDistance('');
      setError('');
    }
  }, [preselectedRoad, initialMode, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!cityA.trim()) {
      setError('Please provide the first city name.');
      return;
    }
    if (!cityB.trim()) {
      setError('Please provide the second city name.');
      return;
    }
    if (cityA.trim().toLowerCase() === cityB.trim().toLowerCase()) {
      setError('City A and City B must be distinct. Self-referential roads are not allowed.');
      return;
    }

    const distNum = parseInt(distance, 10);
    if (isNaN(distNum) || distNum <= 0) {
      setError('Road distance must be a valid positive integer greater than 0 km.');
      return;
    }

    try {
      setLoading(true);
      if (mode === 'add') {
        await onAddRoad(cityA.trim(), cityB.trim(), distNum);
      } else {
        await onUpdateRoad(cityA.trim(), cityB.trim(), distNum);
      }
      onClose();
    } catch (err) {
      setError(err.message || 'Operation failed. Please verify input data.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-title-group">
            {mode === 'add' ? (
              <PlusCircle className="text-cyan" size={22} />
            ) : (
              <Edit3 className="text-emerald" size={22} />
            )}
            <div>
              <h3 className="modal-title">{mode === 'add' ? 'Add Bidirectional Road' : 'Update Road Distance'}</h3>
              <p className="modal-subtitle">Directly modifies the live Graph Adjacency List.</p>
            </div>
          </div>

          <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            <X size={20} />
          </button>
        </div>

        {/* Tab Selector */}
        {!preselectedRoad && (
          <div className="modal-tabs">
            <button
              type="button"
              className={`modal-tab ${mode === 'add' ? 'active' : ''}`}
              onClick={() => { setMode('add'); setError(''); }}
            >
              <PlusCircle size={16} />
              <span>Add Road</span>
            </button>
            <button
              type="button"
              className={`modal-tab ${mode === 'update' ? 'active' : ''}`}
              onClick={() => { setMode('update'); setError(''); }}
            >
              <Edit3 size={16} />
              <span>Update Distance</span>
            </button>
          </div>
        )}

        <form onSubmit={handleSubmit} className="modal-form">
          {error && (
            <div className="modal-error-box">
              <AlertCircle size={18} className="text-rose flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* City A Input / Select */}
          <div className="input-group">
            <label className="input-label" htmlFor="modal-city-a">City A (Origin/Endpoint)</label>
            <input
              id="modal-city-a"
              type="text"
              className="text-input"
              list="cities-datalist-a"
              placeholder="e.g. Lucknow, Delhi, Bareilly"
              value={cityA}
              onChange={(e) => setCityA(e.target.value)}
              disabled={loading || (mode === 'update' && preselectedRoad !== null)}
              required
            />
            <datalist id="cities-datalist-a">
              {cities.map((c) => (
                <option key={c.name} value={c.name} />
              ))}
            </datalist>
          </div>

          <div className="two-way-divider">
            <ArrowLeftRight size={16} className="text-cyan" />
            <span className="divider-text">Bidirectional Road (Undirected Edge)</span>
          </div>

          {/* City B Input / Select */}
          <div className="input-group">
            <label className="input-label" htmlFor="modal-city-b">City B (Target/Endpoint)</label>
            <input
              id="modal-city-b"
              type="text"
              className="text-input"
              list="cities-datalist-b"
              placeholder="e.g. Kanpur, Agra, Varanasi"
              value={cityB}
              onChange={(e) => setCityB(e.target.value)}
              disabled={loading || (mode === 'update' && preselectedRoad !== null)}
              required
            />
            <datalist id="cities-datalist-b">
              {cities.map((c) => (
                <option key={c.name} value={c.name} />
              ))}
            </datalist>
          </div>

          {/* Distance Input */}
          <div className="input-group">
            <label className="input-label" htmlFor="modal-distance">
              {mode === 'add' ? 'Road Distance (km)' : 'New Road Distance (km)'}
            </label>
            <input
              id="modal-distance"
              type="number"
              min="1"
              className="text-input"
              placeholder="e.g. 150"
              value={distance}
              onChange={(e) => setDistance(e.target.value)}
              disabled={loading}
              required
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-secondary" onClick={onClose} disabled={loading}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? (
                <span className="btn-spinner-wrap">
                  <span className="spinner"></span>
                  <span>Saving...</span>
                </span>
              ) : (
                <>
                  <Check size={16} />
                  <span>{mode === 'add' ? 'Add Road to Graph' : 'Save Updated Distance'}</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

