package se.iths;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/product")
@Slf4j
public class ProductsController {

    final ProductRepository repository;
    private final ProductsModelAssembler assembler;

    public ProductsController(ProductRepository dataStorage,ProductsModelAssembler personsModelAssembler) {
        this.repository = dataStorage;
        this.assembler=personsModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Product>> all() {
        log.debug("All Products listed");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<Product>> one(@PathVariable long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("POST create Product " + product);
        var p = repository.save(product);
        log.info("Saved to repository " + p);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(ProductsController.class).slash(p.getId()).toUri());
        return new ResponseEntity<>(p, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (repository.existsById(id)) {
            //log.info("Product deleted");
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    ResponseEntity<Product> replaceProduct(@RequestBody Product newProducts, @PathVariable Long id) {
        return repository.findById(id)
                .map(product -> {
                    product.setName(newProducts.getName());
                    repository.save(product);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(ProductsController.class).slash(product.getId()).toUri());
                    return new ResponseEntity<>(product, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    ResponseEntity<Product> modifyProduct(@RequestBody Product newProduct, @PathVariable Long id) {
        return repository.findById(id)
                .map(product -> {
                    if (newProduct.getName() != null)
                        product.setName(newProduct.getName());

                    repository.save(product);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(ProductsController.class).slash(product.getId()).toUri());
                    return new ResponseEntity<>(product, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
