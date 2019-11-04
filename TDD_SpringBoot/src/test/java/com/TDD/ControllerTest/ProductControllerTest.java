package com.TDD.ControllerTest;

import com.TDD.model.Product;
import com.TDD.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.swing.text.html.Option;
import java.util.Optional;


import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// ProductControllerTest
@ExtendWith(SpringExtension.class) // gets the SpringExtension class and then the junit settings
@SpringBootTest // create an application context and load all the spring class
@AutoConfigureMockMvc // creates a MockMVC and autowire
public class ProductControllerTest {

    @MockBean // product service - creates a copy of the product service for the test case - prototype
    private ProductService productService;

    @Autowired // create the mockito object and automatically inject into the constructor using the @Autowired
    private MockMvc mockMvc;

    // Successful test case
    @Test
    @DisplayName("Get /product/1 - Found")
    void testGetProductByIdFound() throws Exception {
        // create a mock product
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        // mock service layer returns a mock product created above since test is for controller and not for service layer
        doReturn(Optional.of(mockProduct)).when(productService).findbyId((long) 1);

        // Execute the Get request
        // mockMVC invokes the controller method using the get request url.
        // when the service layer is invoked in the controller, it uses the mock product service mock layer.
        mockMvc.perform(get("/product/{id}", 1))
        // andExpect is a result matcher that matches the returned values from the get request with the expected values.

         // validate the response code and content type
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

         //Validate the headers
        .andExpect(header().string(HttpHeaders.ETAG, "\"1\"")) // valiate the ETAG
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1")) // validate the Location

        // Validate the returned fields
        .andExpect((ResultMatcher) jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect((ResultMatcher) jsonPath("$.quantity", is(10)))
                .andExpect((ResultMatcher) jsonPath("$.version", is(1)));

    } // end of successful test case

    // test case for negative test
    @Test
    @DisplayName("GET /product/1 - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        // Setup our mocked service without the mock product being created
        doReturn(Optional.empty()).when(productService).findbyId((long) 1);

        //Execute the Get request
        // test a situation where the a failed request is returned
        // without the mock product being created, the test will fail when the mockmvc is created
        mockMvc.perform(get("/product/{id}", 1))
                .andExpect(status().isNotFound());
    }// test case for failed code test

    @Test
    @DisplayName("POST /product - Success")
    void testCreateProduct() throws Exception {
        // Setup mocked service; product that is post over the url and "saved" to database
        Product postProduct  = new Product("Product Name", 10);
        // result of saving into database. an id and version number should be auto generated
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        // mockProduct should be returned when the save method is called.
        doReturn(mockProduct).when(productService).save(any());

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postProduct)))
                // validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                //validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                // validate the returned fields
                .andExpect((ResultMatcher) jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect((ResultMatcher) jsonPath("$.quantity", is(10)))
                .andExpect((ResultMatcher) jsonPath("$.version", is(1)));
    } // end of post method test

    // successful put test
    @Test
    @DisplayName("PUT /product/1 - Success")
    void testProductPutSuccess() throws Exception {
        // new product changes to be sent as the body of the http request
        Product putProduct = new Product("Product Name", 10, 2);
        // after changes, the product that should be returned
        Product mockProduct = new Product(1, "Product Name", 10, 1);

        // return optional of mock-product when the product service find by id is used in this code base.
        doReturn(Optional.of(mockProduct)).when(productService).findbyId((long) 1);

        // method return type of update is boolean
        doReturn(true).when(productService).update(any());

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                //validate the returned fields
                .andExpect((ResultMatcher) jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect((ResultMatcher) jsonPath("$.quantity", is(10)))
                .andExpect((ResultMatcher) jsonPath("$.version", is(2)));
    } // end of put method

    // version mismatch - failed put request test
    @Test
    @DisplayName("PUT /product/1 - Version Mismatch")
    void testProductPutVersionMisMatch() throws Exception {
        // product to be sent in body of request
        Product putProduct = new Product("Product Name", 10);
        // product to be received when the product service is called, product has wrong version number
        Product mockProduct = new Product(1, "Product Name", 10, 2);
        // return optional mock product
        doReturn(Optional.of(mockProduct)).when(productService).findbyId((long) 1);
        // method return type is boolean
        doReturn(true).when(productService).update(any());

        mockMvc.perform(put("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // validate the response code and content type
                .andExpect(status().isConflict());
    } // end of put test method

    //  product not found test case

    @Test
    @DisplayName("PUT /product/1 - Not Found")
    void testProductPutNotFound() throws Exception {
        Product putProduct  = new Product("Product Name", 10);
        // return empty result, should return product not found
        doReturn(Optional.empty()).when(productService).findbyId((long) 1);

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                //validate the response code and content type
                .andExpect(status().isNotFound());
    } // end of put not found test method

    @Test
    @DisplayName("DELETE /product/1 - Success")
    void testProductDeleteSuccess() throws Exception {
        Product mockProduct = new Product(1, "Product Name", 10, 1);

        // return optional mock product when deletion is carried out
        doReturn(Optional.of(mockProduct)).when(productService).findbyId((long) 1);
        // return type of method is a boolean
        doReturn(true).when(productService).delete((long)1);

        mockMvc.perform(delete("/product/{id}", 1))
                .andExpect(status().isOk());
    } // end of successful delete method

    @Test
    @DisplayName("DELETE /product/1 - Not Found")
    void testProductDeleteNotFound() throws Exception {
        doReturn(Optional.empty()).when(productService).findbyId((long)1);

        mockMvc.perform(delete("/product/{id}",1))
                .andExpect(status().isNotFound());
    } // end of delete method not found

    @Test
    @DisplayName("DELETE /product/1 - Failure")
    void testProductDeleteFailure() throws Exception {
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        // return optional
        doReturn(Optional.of(mockProduct)).when(productService).findbyId((long) 1);
        // set return type of method to boolean
        doReturn(false).when(productService).delete((long)1);

        mockMvc.perform(delete("/product/{id}",1))
                .andExpect(status().isInternalServerError());
    } // end product delete failure test case





// converts the object to Json String
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
