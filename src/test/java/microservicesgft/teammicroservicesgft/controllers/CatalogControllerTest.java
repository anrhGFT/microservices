 package microservicesgft.teammicroservicesgft.controllers;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import microservicesgft.teammicroserviciesgft.controllers.CatalogController;
import microservicesgft.teammicroserviciesgft.models.Product;
import microservicesgft.teammicroserviciesgft.services.CatalogService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogControllerTest {
    
   
    @Mock
    private CatalogService catalogService;
    @InjectMocks
    private CatalogController catalogController;
    
    private MockMvc mockMvc;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(catalogController).build();
    
    }
    @Test
    public void testGetAllProducts() throws Exception {
       
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "Product A", "Description A","CategoryA", 10.0, 20));
        productList.add(new Product(2, "Product B", "Description B","CategoryB", 20.0, 30));

       
        when(catalogService.findAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemId", is(1)))
                .andExpect(jsonPath("$[0].name", is("Product A")))
                .andExpect(jsonPath("$[0].description", is("Description A")))
                .andExpect(jsonPath("$[0].category", is("CategoryA")))
                .andExpect(jsonPath("$[0].price", is(10.0)))
                .andExpect(jsonPath("$[0].stock", is(20)))
                .andExpect(jsonPath("$[1].itemId", is(2)))
                .andExpect(jsonPath("$[1].name", is("Product B")))
                .andExpect(jsonPath("$[1].description", is("Description B")))
                .andExpect(jsonPath("$[1].category", is("CategoryB")))
                .andExpect(jsonPath("$[1].price", is(20.0)))
                .andExpect(jsonPath("$[1].stock", is(30)));

       
        verify(catalogService, times(1)).findAllProducts();
    }
    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(10.0);
        product.setStock(100);
    
        when(catalogService.createProduct(any(Product.class))).thenReturn(product);
    
        mockMvc.perform(post("/catalog/createproduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(10.0)))
                .andExpect(jsonPath("$.stock", is(100)));
    
            }
    @Test
    public void createProductTest() {
      Product product = new Product();
      product.setName("Product 1");
      product.setDescription("Product 1 description");
      product.setPrice(100.0);
      product.setStock(10);
      when(catalogService.createProduct(any(Product.class))).thenReturn(product);
  
      ResponseEntity<Product> responseEntity = catalogController.createData(product);
  
      assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
      assertEquals(product, responseEntity.getBody());
    } 


    @Test
    void testUpdateProduct() {
        
        int productId = 1;
        Product productToUpdate = new Product();
        productToUpdate.setItemId(productId);
        productToUpdate.setName("Updated Product Name");
        productToUpdate.setDescription("Updated Product Description");
        productToUpdate.setPrice(10.0);
        productToUpdate.setStock(10);
        Product updatedProduct = new Product();
        updatedProduct.setItemId(productId);
        updatedProduct.setName("Updated Product Name");
        updatedProduct.setDescription("Updated Product Description");
        updatedProduct.setPrice(10.0);
        updatedProduct.setStock(10);
        when(catalogService.updateProduct(any(Product.class))).thenReturn(updatedProduct);
        
        
        ResponseEntity<Product> response = catalogController.updateProduct(productId, productToUpdate);
        
        
        verify(catalogService).updateProduct(productToUpdate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
    }

    @Test
    void testDeleteProduct() throws Exception {
        int productId = 1;
        Product mockProduct = new Product();
        mockProduct.setItemId(productId);
        mockProduct.setName("Mock Product");
        mockProduct.setDescription("Mock Description");
        mockProduct.setPrice(9.99);
        mockProduct.setStock(15);
        
        doNothing().when(catalogService).deleteProduct(productId);
        
        mockMvc.perform(delete("/catalog/deleteproduct/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().string("Product with id " + productId + " has been deleted"));
        
        verify(catalogService, times(1)).deleteProduct(productId);
       
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        
        String category = "test category";
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Product 1", "Test product 1", category,10.00,2));
        products.add(new Product(2, "Product 2", "Test product 2", category,13.00,5));
        when(catalogService.findProductsByCategory(category)).thenReturn(products);

        
        mockMvc.perform(get("/catalog/categories/{category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].itemId").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].description").value("Test product 1"))
                .andExpect(jsonPath("$[0].category").value(category))
                .andExpect(jsonPath("$[0].price").value(10.00))
                .andExpect(jsonPath("$[0].stock").value(2))
                .andExpect(jsonPath("$[1].itemId").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].description").value("Test product 2"))
                .andExpect(jsonPath("$[1].category").value(category))
                .andExpect(jsonPath("$[1].price").value(13.00))
                .andExpect(jsonPath("$[1].stock").value(5));

        
        verify(catalogService, times(1)).findProductsByCategory(category);
    }

    @Test
    public void testGetProductsByCategoryNotFound() throws Exception {
        
        String category = "non-existent category";
        when(catalogService.findProductsByCategory(category)).thenReturn(Collections.emptyList());

        
        mockMvc.perform(get("/catalog/categories/{category}", category))
                .andExpect(status().isNotFound());

        
        verify(catalogService, times(1)).findProductsByCategory(category);
    }
}

