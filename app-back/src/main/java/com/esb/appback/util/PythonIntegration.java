package com.esb.appback.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.*;

/**
 * Integration layer for Python ML modules
 * Executes Python scripts and parses results
 */
@Component
public class PythonIntegration {
    
    private static final String PYTHON_ENV = "python";
    private static final String MODULES_PATH = "./modules/";
    private ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Execute Python anomaly detection
     */
    public Map<String, Object> detectAnomalies(List<Double> values, double currentValue) throws Exception {
        String script = "from anomaly_detection_module import detect_consumption_anomaly\n" +
            "import json\n" +
            "result = detect_consumption_anomaly(" + values + ", " + currentValue + ")\n" +
            "print(json.dumps(result))";
        
        return executePythonScript(script);
    }
    
    /**
     * Execute Python forecasting
     */
    public Map<String, Object> forecastConsumption(List<Double> history, int periods, String method) throws Exception {
        String script = "from forecasting_module import forecast_consumption\n" +
            "import json\n" +
            "result = forecast_consumption(" + history + ", " + periods + ", '" + method + "')\n" +
            "print(json.dumps(result, default=str))";
        
        return executePythonScript(script);
    }
    
    /**
     * Execute OCR processing
     */
    public Map<String, Object> processInvoiceOCR(String text) throws Exception {
        String escapedText = text.replace("\"", "\\\"").replace("\n", "\\n");
        String script = "from ocr_module import process_invoice_text\n" +
            "import json\n" +
            "result = process_invoice_text(\"\"\"" + escapedText + "\"\"\")\n" +
            "print(json.dumps(result, default=str))";
        
        return executePythonScript(script);
    }
    
    /**
     * Execute stock optimization
     */
    public Map<String, Object> optimizeStock(double annualDemand, double orderingCost, 
                                             double holdingCost, int leadTime, 
                                             double stdDevDemand, String serviceLevel) throws Exception {
        String script = "from stock_optimization_module import optimize_stock_levels\n" +
            "import json\n" +
            "result = optimize_stock_levels(" + annualDemand + ", " + orderingCost + ", " + 
            holdingCost + ", " + leadTime + ", " + stdDevDemand + ", '" + serviceLevel + "')\n" +
            "print(json.dumps(result, default=str))";
        
        return executePythonScript(script);
    }
    
    /**
     * Generic Python script execution
     */
    private Map<String, Object> executePythonScript(String script) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(PYTHON_ENV, "-c", script);
        pb.directory(new File(MODULES_PATH));
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        
        int exitCode = process.waitFor();
        
        if (exitCode == 0 && output.length() > 0) {
            return objectMapper.readValue(output.toString(), Map.class);
        }
        
        return Map.of("error", "Python execution failed");
    }
}
