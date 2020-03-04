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
        log.debug("All persons called");
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
    public ResponseEntity<Product> createPerson(@RequestBody Product person) {
        log.info("POST create Product " + person);
        var p = repository.save(person);
        log.info("Saved to repository " + p);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(ProductsController.class).slash(p.getId()).toUri());
        //headers.add("Location", "/api/persons/" + p.getId());
        return new ResponseEntity<>(p, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePerson(@PathVariable Long id) {
        if (repository.existsById(id)) {
            //log.info("Product deleted");
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    ResponseEntity<Product> replacePerson(@RequestBody Product newPerson, @PathVariable Long id) {
        return repository.findById(id)
                .map(person -> {
                    person.setName(newPerson.getName());
                    repository.save(person);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(ProductsController.class).slash(person.getId()).toUri());
                    return new ResponseEntity<>(person, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    ResponseEntity<Product> modifyPerson(@RequestBody Product newPerson, @PathVariable Long id) {
        return repository.findById(id)
                .map(person -> {
                    if (newPerson.getName() != null)
                        person.setName(newPerson.getName());

                    repository.save(person);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(ProductsController.class).slash(person.getId()).toUri());
                    return new ResponseEntity<>(person, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
