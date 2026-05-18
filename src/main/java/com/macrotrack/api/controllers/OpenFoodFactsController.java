package com.macrotrack.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.macrotrack.api.entity.Product;
import com.macrotrack.api.services.OpenFoodFactsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/food")
public class OpenFoodFactsController {

    private static final Logger log = LoggerFactory.getLogger(OpenFoodFactsController.class);

    private final OpenFoodFactsService openFoodFactsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenFoodFactsController(OpenFoodFactsService openFoodFactsService) {
        this.openFoodFactsService = openFoodFactsService;
    }

    @GetMapping("/product/{barcode}")
    public String getProduct(@PathVariable String barcode) {
        return openFoodFactsService.getProductByBarcode(barcode);
    }

    @GetMapping("/product/{barcode}/details")
    public Product getProductDetails(@PathVariable String barcode) {
        String json = openFoodFactsService.getProductByBarcode(barcode);
        return transformToProduct(json);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String q) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> productsList = new ArrayList<>();

        try {
            String offJson = openFoodFactsService.searchProducts(q);
            var root = objectMapper.readTree(offJson);
            var offProducts = root.path("products");

            if (offProducts.isArray()) {
                for (var p : offProducts) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("_id", getTextValue(p, "_id", "id"));
                    product.put("product_name", getTextValue(p, "product_name"));
                    product.put("brands", getTextValue(p, "brands"));
                    product.put("serving_size", getTextValue(p, "serving_size"));

                    var nutriments = p.path("nutriments");
                    if (!nutriments.isMissingNode()) {
                        Map<String, Object> n = new HashMap<>();
                        n.put("energy-kcal_serving", nutriments.path("energy-kcal_serving").asDouble(0));
                        n.put("carbohydrates_serving", nutriments.path("carbohydrates_serving").asDouble(0));
                        n.put("proteins_serving", nutriments.path("proteins_serving").asDouble(0));
                        n.put("fat_serving", nutriments.path("fat_serving").asDouble(0));
                        n.put("sugars_serving", nutriments.path("sugars_serving").asDouble(0));
                        n.put("saturated-fat_serving", nutriments.path("saturated-fat_serving").asDouble(0));
                        n.put("serving_size", nutriments.path("serving_size").asDouble(0));
                        product.put("nutriments", n);
                    }

                    productsList.add(product);
                }
            }

            result.put("count", productsList.size());
            result.put("products", productsList);
            log.info("OpenFoodFacts search for '{}' returned {} results", q, productsList.size());
        } catch (Exception e) {
            log.error("OpenFoodFacts search failed for query '{}'", q, e);
            result.put("count", 0);
            result.put("products", productsList);
        }

        return ResponseEntity.ok(result);
    }

    private Product transformToProduct(String json) {
        Product product = new Product();
        try {
            var root = objectMapper.readTree(json);
            var productNode = root.path("product");
            if (productNode.isMissingNode()) {
                return product;
            }
            product.setId(getTextValue(productNode, "_id"));
            product.setName(getTextValue(productNode, "product_name", "product_name"));
            product.setServing_size(getTextValue(productNode, "serving_size"));
            product.setServing_cal(getTextValue(productNode, "energy-kcal_serving"));
            product.setServing_carbs(getTextValue(productNode, "carbohydrates-total_serving"));
            product.setServing_sugar(getTextValue(productNode, "sugars_serving"));
            product.setServing_fat(getTextValue(productNode, "fat_serving"));
            product.setServing_satFat(getTextValue(productNode, "saturated-fat_serving"));
            product.setServing_Protein(getTextValue(productNode, "proteins_serving"));
        } catch (Exception e) {
            log.error("Failed to transform product JSON", e);
        }
        return product;
    }

    private String getTextValue(com.fasterxml.jackson.databind.JsonNode node, String... fields) {
        for (String field : fields) {
            var value = node.path(field);
            if (!value.isMissingNode() && !value.asText().isEmpty()) {
                return value.asText();
            }
        }
        return null;
    }
}
