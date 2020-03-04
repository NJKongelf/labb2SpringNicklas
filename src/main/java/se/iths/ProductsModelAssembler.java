package se.iths;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ProductsModelAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {

    //http://stateless.co/hal_specification.html

    @Override
    public EntityModel<Product> toModel(Product product) {
        return new EntityModel<>(product,
                linkTo(methodOn(ProductsController.class).one(product.getId())).withSelfRel(),
                linkTo(methodOn(ProductsController.class).all()).withRel("products"));
    }

    @Override
    public CollectionModel<EntityModel<Product>> toCollectionModel(Iterable<? extends Product> entities) {
        var collection = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(collection,
                linkTo(methodOn(ProductsController.class).all()).withSelfRel());
    }
}