"""
Anomaly Detection Module
Detects unusual patterns and anomalies in consumable data
"""
import statistics
from typing import List, Dict, Tuple

class AnomalyDetector:
    """Detects anomalies in consumption and pricing data"""
    
    def __init__(self):
        self.z_threshold = 2.0  # Standard deviations for statistical anomaly
        self.percentage_change_threshold = 0.5  # 50% change threshold
        
    def detect_statistical_anomaly(self, values: List[float]) -> Tuple[List[int], List[float]]:
        """
        Detect anomalies using Z-score method (for normally distributed data)
        Returns: (anomaly_indices, z_scores)
        """
        if len(values) < 3:
            return [], []
        
        try:
            mean = statistics.mean(values)
            stdev = statistics.stdev(values)
            
            if stdev == 0:
                return [], [0] * len(values)
            
            z_scores = [(v - mean) / stdev for v in values]
            anomaly_indices = [i for i, z in enumerate(z_scores) if abs(z) > self.z_threshold]
            
            return anomaly_indices, z_scores
        except:
            return [], []
    
    def detect_iqr_anomaly(self, values: List[float]) -> List[int]:
        """
        Detect anomalies using Interquartile Range (IQR) method
        More robust for non-normal distributions
        """
        if len(values) < 4:
            return []
        
        sorted_values = sorted(values)
        q1_idx = len(sorted_values) // 4
        q3_idx = 3 * len(sorted_values) // 4
        
        q1 = sorted_values[q1_idx]
        q3 = sorted_values[q3_idx]
        iqr = q3 - q1
        
        lower_bound = q1 - 1.5 * iqr
        upper_bound = q3 + 1.5 * iqr
        
        anomaly_indices = [i for i, v in enumerate(values) if v < lower_bound or v > upper_bound]
        return anomaly_indices
    
    def detect_trend_anomaly(self, values: List[float], window_size: int = 7) -> List[Dict]:
        """
        Detect abrupt changes in trend
        Returns list of anomalies with metadata
        """
        if len(values) < window_size + 1:
            return []
        
        anomalies = []
        for i in range(window_size, len(values)):
            window = values[i - window_size:i]
            current = values[i]
            avg = statistics.mean(window)
            
            if avg != 0:
                percent_change = abs((current - avg) / avg)
                if percent_change > self.percentage_change_threshold:
                    anomalies.append({
                        'index': i,
                        'value': current,
                        'expected': avg,
                        'percent_change': percent_change,
                        'type': 'TREND_ANOMALY'
                    })
        
        return anomalies
    
    def calculate_anomaly_score(self, values: List[float], include_outlier_index: int = None) -> int:
        """
        Calculate anomaly severity score (0-100)
        """
        if not values or include_outlier_index is None or include_outlier_index >= len(values):
            return 0
        
        score = 0
        
        # Check Z-score
        anomaly_indices, z_scores = self.detect_statistical_anomaly(values)
        if include_outlier_index in anomaly_indices:
            score += min(40, abs(z_scores[include_outlier_index]) * 10)
        
        # Check IQR
        iqr_anomalies = self.detect_iqr_anomaly(values)
        if include_outlier_index in iqr_anomalies:
            score += 30
        
        # Check trend
        trend_anomalies = self.detect_trend_anomaly(values)
        for anomaly in trend_anomalies:
            if anomaly['index'] == include_outlier_index:
                score += min(30, anomaly['percent_change'] * 100)
        
        return min(score, 100)
    
    def classify_anomaly(self, score: int) -> str:
        """Classify anomaly severity based on score"""
        if score >= 70:
            return 'CRITICAL'
        elif score >= 50:
            return 'HIGH'
        elif score >= 30:
            return 'MEDIUM'
        else:
            return 'LOW'


# Singleton instance
anomaly_detector = AnomalyDetector()


def detect_consumption_anomaly(consumption_history: List[float], current_value: float) -> Dict:
    """
    Detect if current consumption is anomalous
    """
    all_values = consumption_history + [current_value]
    score = anomaly_detector.calculate_anomaly_score(all_values, len(all_values) - 1)
    severity = anomaly_detector.classify_anomaly(score)
    
    return {
        'is_anomaly': severity != 'LOW',
        'score': score,
        'severity': severity,
        'current_value': current_value,
        'expected_range': {
            'min': min(consumption_history) if consumption_history else 0,
            'max': max(consumption_history) if consumption_history else current_value,
            'average': statistics.mean(consumption_history) if consumption_history else current_value
        }
    }


def detect_price_anomaly(price_history: List[float], current_price: float, threshold: float = 0.25) -> Dict:
    """
    Detect if current price is anomalous
    """
    if not price_history:
        return {
            'is_anomaly': False,
            'reason': 'Insufficient history',
            'current_price': current_price
        }
    
    avg_price = statistics.mean(price_history)
    price_change = abs((current_price - avg_price) / avg_price) if avg_price > 0 else 0
    
    is_anomalous = price_change > threshold
    severity = 'HIGH' if price_change > 0.5 else 'MEDIUM' if is_anomalous else 'LOW'
    
    return {
        'is_anomaly': is_anomalous,
        'severity': severity,
        'current_price': current_price,
        'average_price': avg_price,
        'price_change_percent': price_change * 100,
        'threshold': threshold * 100
    }
