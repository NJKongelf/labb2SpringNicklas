package se.iths;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue
    Long id;
    String name;
    Long weightInKg;

    public Product(Long id,String name,Long weightInKg){
        this.id=id;
        this.name=name;
        this.weightInKg=weightInKg;
    }
}
