"""
Forecasting Module
Implements time series forecasting for consumption prediction
"""
import statistics
from typing import List, Dict, Tuple
from datetime import datetime, timedelta

class SimpleMovingAverage:
    """Simple forecasting using moving averages"""
    
    @staticmethod
    def forecast(values: List[float], periods: int = 7, window: int = 7) -> List[float]:
        """
        Simple Moving Average forecast
        periods: number of periods to forecast
        window: window size for averaging
        """
        if not values or len(values) < window:
            return []
        
        forecasts = []
        for _ in range(periods):
            # Use last 'window' values for prediction
            avg = statistics.mean(values[-window:])
            forecasts.append(avg)
            values.append(avg)  # Add forecast to values for next iteration
        
        return forecasts
    
    @staticmethod
    def forecast_with_trend(values: List[float], periods: int = 7) -> List[float]:
        """
        Moving average with trend adjustment
        """
        if len(values) < 2:
            return [statistics.mean(values)] * periods if values else []
        
        # Calculate trend
        recent_avg = statistics.mean(values[-3:]) if len(values) >= 3 else values[-1]
        older_avg = statistics.mean(values[:5]) if len(values) >= 5 else values[0]
        trend = (recent_avg - older_avg) / max(len(values), 1)
        
        forecasts = []
        last_value = values[-1]
        for i in range(1, periods + 1):
            forecast = last_value + (trend * i)
            forecasts.append(max(forecast, 0))  # Ensure non-negative
        
        return forecasts


class ExponentialSmoothing:
    """Exponential Smoothing forecasting method"""
    
    alpha = 0.3  # Smoothing parameter (0-1)
    
    @staticmethod
    def forecast(values: List[float], periods: int = 7) -> List[float]:
        """
        Exponential Smoothing forecast
        """
        if not values:
            return []
        
        # Initialize with first value
        smoothed = values[0]
        
        # Smooth existing values
        for value in values[1:]:
            smoothed = ExponentialSmoothing.alpha * value + (1 - ExponentialSmoothing.alpha) * smoothed
        
        # Forecast future periods
        forecasts = [smoothed] * periods
        return forecasts
    
    @staticmethod
    def forecast_with_trend(values: List[float], periods: int = 7, beta: float = 0.1) -> List[float]:
        """
        Exponential Smoothing with trend (Holt's method)
        """
        if len(values) < 2:
            return values[-1:] * periods if values else []
        
        alpha = ExponentialSmoothing.alpha
        
        # Initialize
        level = values[0]
        trend = values[1] - values[0]
        
        # Smooth values
        for value in values[1:]:
            prev_level = level
            level = alpha * value + (1 - alpha) * (level + trend)
            trend = beta * (level - prev_level) + (1 - beta) * trend
        
        # Forecast
        forecasts = []
        for i in range(1, periods + 1):
            forecasts.append(level + i * trend)
        
        return [max(f, 0) for f in forecasts]


def forecast_consumption(historical_consumption: List[float], periods: int = 30, method: str = 'sma') -> Dict:
    """
    Forecast future consumption
    periods: days to forecast
    method: 'sma' (Simple Moving Average), 'es' (Exponential Smoothing)
    """
    if not historical_consumption:
        return {
            'forecasts': [],
            'method': method,
            'confidence': 0,
            'error': 'No historical data'
        }
    
    if method == 'sma':
        forecasts = SimpleMovingAverage.forecast_with_trend(historical_consumption, periods)
    elif method == 'es':
        forecasts = ExponentialSmoothing.forecast_with_trend(historical_consumption, periods)
    else:
        forecasts = SimpleMovingAverage.forecast(historical_consumption, periods)
    
    # Calculate confidence based on data consistency
    if len(historical_consumption) > 1:
        variance = statistics.variance(historical_consumption)
        mean_val = statistics.mean(historical_consumption)
        cv = (variance ** 0.5) / mean_val if mean_val > 0 else 0
        confidence = max(0, 100 - (cv * 100))
    else:
        confidence = 50
    
    return {
        'forecasts': forecasts,
        'method': method,
        'confidence': confidence,
        'next_30_days': {
            'average': statistics.mean(forecasts) if forecasts else 0,
            'total': sum(forecasts) if forecasts else 0
        }
    }


def calculate_reorder_point(consumption_rate: float, lead_time_days: int, safety_stock_coef: float = 1.65) -> Dict:
    """
    Calculate optimal reorder point using EOQ-like approach
    consumption_rate: average daily consumption
    lead_time_days: supplier lead time
    safety_stock_coef: z-score for service level (1.65 = 95% service level)
    """
    reorder_point = (consumption_rate * lead_time_days)
    
    # Add safety stock (simplified - assumes standard deviation = 20% of mean)
    std_dev = consumption_rate * 0.2
    safety_stock = safety_stock_coef * std_dev * (lead_time_days ** 0.5)
    
    optimal_reorder_point = reorder_point + safety_stock
    
    return {
        'reorder_point': optimal_reorder_point,
        'reorder_point_without_safety': reorder_point,
        'safety_stock': safety_stock,
        'service_level': 95.0  # Service level percentage
    }
