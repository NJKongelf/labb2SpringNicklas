package se.iths;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class SetupData {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if( repository.count() == 0) {
                //New empty database, add some persons
                log.info("Added to database " + repository.save(new Product(0L, "Tesla Model s", 2008L,1000000)));
                log.info("Added to database " + repository.save(new Product(0L, "Hairgel-pallet",500L,25)));
                log.info("Added to database " + repository.save(new Product(0L, "Moon-rocket",25000L,25000000)));
                log.info("Added to database " + repository.save(new Product(0L, "Wolkswagen Beatle",1500L,10000)));
            }
        };
    }
/*
    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
*/
}
