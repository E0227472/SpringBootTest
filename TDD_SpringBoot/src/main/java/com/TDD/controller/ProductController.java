package com.TDD.controller;

import com.TDD.model.Product;
import com.TDD.service.ProductService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
public class ProductController {
    @Autowired
   private ProductService productService;
    //ResponseEntity returns the status code, headers and body.

    // get the product based on the id
    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return productService.findbyId(id)
                .map(product -> {
                   try {
                       return ResponseEntity
                               .ok()
                               .eTag(Integer.toString(product.getVersion()))
                               // if product does not exist, URI will give error
                               .location(new URI("/product/" + product.getId()))
                               .body(product);
                   } catch (URISyntaxException e) {
                       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                   }
                })
                .orElse(ResponseEntity.notFound().build()); // if the product does not exist return not found status
    }

    // get all products from the url, return type iterable which is the collection interface
    // implementations can be returned later
    @GetMapping("/products")
    public Iterable<Product> getProducts() {
        return productService.findAll();
    } // end of get method

    // create product in the database, return the ResponseEntity object
    @PostMapping("/product")
    public ResponseEntity<Product> createProduct (@RequestBody Product product) {
        // create new product
        Product newProduct = productService.save(product);

        try {
            return ResponseEntity
                    .created(new URI("/product/" + newProduct.getId()))// change the uri once created
                    .eTag(Integer.toString(newProduct.getVersion()))
                    .body(newProduct); // return the product object
        } catch (URISyntaxException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // returns an object
        }
    } // end of post method

    // modify the product
    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct (@RequestBody Product product,
                                            @PathVariable Long id,
                                            @RequestHeader("iF-Match") Integer ifMatch) {

        Optional<Product> existingProduct = productService.findbyId(id);
        return existingProduct.map(
                p -> {
                    if(p.getVersion() != ifMatch ) { // check if the product version number matches the ifMatch
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                    // update the product
                    p.setName(product.getName());
                    p.setQuantity(product.getQuantity());
                    p.setVersion(product.getVersion());

                    try {
                        if(productService.update(p)) { // if successful update of the product
                            return ResponseEntity
                                    .ok()
                                    .location(new URI("/product/" + p.getId()))
                                    .eTag(Integer.toString(p.getVersion()))
                                    .body(p);
                        } else { // if update of product not successful
                            return ResponseEntity.notFound().build();
                        }
                    } catch (URISyntaxException e) { // if the syntax is not returned due to missing product id / product
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());

    } // end of put method
    // delete the product
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {

        Optional<Product> existingProduct = productService.findbyId(id);

        return existingProduct.map(p -> {
            if(productService.delete(p.getId()))
                // if delete is successful, return status ok
                return ResponseEntity.ok().build();
            else
                // if delete is unsuccessful, return failed build
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }).orElse(ResponseEntity.notFound().build());
    }


}
