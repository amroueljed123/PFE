"""
Stock Optimization Module
Implements algorithms for optimal stock levels and ordering
"""
import math
from typing import Dict

class StockOptimization:
    """Stock optimization algorithms"""
    
    @staticmethod
    def calculate_eoq(annual_demand: float, ordering_cost: float, holding_cost: float) -> Dict:
        """
        Calculate Economic Order Quantity (EOQ)
        EOQ = sqrt(2*D*S / H)
        D: Annual demand
        S: Ordering/Setup cost per order
        H: Holding/Storage cost per unit per year
        """
        if annual_demand <= 0 or ordering_cost <= 0 or holding_cost <= 0:
            return {'error': 'Invalid inputs', 'eoq': 0}
        
        eoq = math.sqrt((2 * annual_demand * ordering_cost) / holding_cost)
        
        # Calculate metrics
        annual_ordering_cost = (annual_demand / eoq) * ordering_cost
        annual_holding_cost = (eoq / 2) * holding_cost
        total_cost = annual_ordering_cost + annual_holding_cost
        order_frequency = annual_demand / eoq
        
        return {
            'eoq': eoq,
            'annual_demand': annual_demand,
            'optimal_order_size': eoq,
            'orders_per_year': order_frequency,
            'days_between_orders': 365 / order_frequency if order_frequency > 0 else 0,
            'annual_ordering_cost': annual_ordering_cost,
            'annual_holding_cost': annual_holding_cost,
            'total_annual_cost': total_cost
        }
    
    @staticmethod
    def calculate_abc_classification(items: list) -> Dict:
        """
        ABC Analysis (Pareto Analysis)
        Classify items into A (high value, 20%), B (medium, 30%), C (low, 50%)
        items: list of dicts with 'name', 'quantity', 'unit_price'
        """
        if not items:
            return {'error': 'No items', 'classifications': []}
        
        # Calculate values
        item_values = []
        for item in items:
            value = item.get('quantity', 0) * item.get('unit_price', 0)
            item_values.append({
                'name': item.get('name'),
                'quantity': item.get('quantity', 0),
                'unit_price': item.get('unit_price', 0),
                'value': value
            })
        
        # Sort by value descending
        item_values.sort(key=lambda x: x['value'], reverse=True)
        
        total_value = sum(item['value'] for item in item_values)
        
        if total_value == 0:
            return {'error': 'Total value is zero', 'classifications': []}
        
        # Classify
        cumulative_percentage = 0
        classifications = []
        
        for item in item_values:
            cumulative_percentage += (item['value'] / total_value) * 100
            
            if cumulative_percentage <= 80:
                category = 'A'  # Top 20% value
            elif cumulative_percentage <= 95:
                category = 'B'  # Next 30% value  
            else:
                category = 'C'  # Last 50% value (low value)
            
            classifications.append({
                'name': item['name'],
                'value': item['value'],
                'percentage_of_total': (item['value'] / total_value) * 100,
                'category': category
            })
        
        return {
            'total_value': total_value,
            'classifications': classifications,
            'a_items': len([c for c in classifications if c['category'] == 'A']),
            'b_items': len([c for c in classifications if c['category'] == 'B']),
            'c_items': len([c for c in classifications if c['category'] == 'C'])
        }
    
    @staticmethod
    def calculate_safety_stock(std_dev_demand: float, z_score: float, lead_time: float) -> Dict:
        """
        Calculate safety stock level
        std_dev_demand: Standard deviation of demand
        z_score: Service level coefficient (1.65 for 95%, 2.33 for 98%)
        lead_time: Lead time in days
        """
        safety_stock = z_score * std_dev_demand * math.sqrt(lead_time)
        
        return {
            'safety_stock': safety_stock,
            'z_score': z_score,
            'service_level': {
                1.0: '84%',
                1.65: '95%',
                2.0: '97.7%',
                2.33: '99%',
                3.0: '99.87%'
            }.get(z_score, 'Custom'),
            'std_dev_demand': std_dev_demand,
            'lead_time': lead_time
        }


def optimize_stock_levels(annual_demand: float, ordering_cost: float, holding_cost: float,
                          lead_time: int, std_dev_demand: float, service_level: str = '95%') -> Dict:
    """
    Comprehensive stock optimization
    """
    # Convert service level to z-score
    z_scores = {'84%': 1.0, '90%': 1.28, '95%': 1.65, '97.7%': 2.0, '99%': 2.33}
    z_score = z_scores.get(service_level, 1.65)
    
    # Calculate EOQ
    eoq_result = StockOptimization.calculate_eoq(annual_demand, ordering_cost, holding_cost)
    
    # Calculate safety stock
    safety_stock_result = StockOptimization.calculate_safety_stock(std_dev_demand, z_score, lead_time)
    
    # Calculate reorder point
    daily_demand = annual_demand / 365
    reorder_point = (daily_demand * lead_time) + safety_stock_result['safety_stock']
    
    # Calculate maximum stock
    max_stock = reorder_point + eoq_result['eoq']
    
    return {
        'eoq': {
            'optimal_order_quantity': eoq_result['eoq'],
            'total_annual_cost': eoq_result['total_annual_cost']
        },
        'safety_stock': safety_stock_result['safety_stock'],
        'reorder_point': reorder_point,
        'maximum_stock_level': max_stock,
        'minimum_stock_level': safety_stock_result['safety_stock'],
        'average_stock_level': reorder_point + (eoq_result['eoq'] / 2),
        'service_level': service_level
    }
