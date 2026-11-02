import React from 'react';
import { AlertCircle, CheckCircle2, Info, X } from 'lucide-react';

export default function AlertBanner({ alert, onDismiss }) {
  if (!alert) return null;

  const { type, message } = alert;

  const icons = {
    error: <AlertCircle className="alert-icon error" size={20} />,
    success: <CheckCircle2 className="alert-icon success" size={20} />,
    info: <Info className="alert-icon info" size={20} />,
  };

  return (
    <div className={`alert-banner alert-${type}`}>
      <div className="alert-content">
        {icons[type] || icons.info}
        <span className="alert-text">{message}</span>
      </div>
      {onDismiss && (
        <button className="alert-close-btn" onClick={onDismiss} aria-label="Dismiss alert">
          <X size={16} />
        </button>
      )}
    </div>
  );
}

