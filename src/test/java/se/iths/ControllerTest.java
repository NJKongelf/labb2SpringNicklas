package se.iths;

import org.junit.jupiter.api.*;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(ProductsController.class)
@Import({ProductsModelAssembler.class})
public class ControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductRepository repository;

    @BeforeEach
    void setUp() {
        when(repository.findAll()).thenReturn(List.of(new Product(1L, "Tesla Model s", 2008L,1000000), new Product(2L,"Hairgel-pallet",500L,25)));
        when(repository.findById(1L)).thenReturn(Optional.of(new Product(1L,"Tesla Model s", 2008L,1000000)));
        when(repository.existsById(3L)).thenReturn(true);
        when(repository.save(any(Product.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var p = (Product) args[0];
            return new Product(1L, p.getName(),p.getWeightInKg(),p.getPrice());
        });


    }
    @Test
    void getAllReturnsListOfAllProducts() throws Exception {
        mockMvc.perform(
                get("/api/product").contentType("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.productList[0]._links.self.href", is("http://localhost/api/product/1")))
                .andExpect(jsonPath("_embedded.productList[0].name", is("Tesla Model s")));
      }

    @Test
    @DisplayName("Calls Get method with invalid id url /api/product/3")
    void getOneProductWithInValidIdThree() throws Exception {
        mockMvc.perform(
                get("/api/product/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Calls post new products")
    void addNewProductWithPostReturnsCreatedProduct() throws Exception {
        mockMvc.perform(
                post("/api/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"name\":\"Moon\"}"))
                .andExpect(status().isCreated());
    }
    @Test
    void deleteProductAndReturnsOk() throws Exception {
        mockMvc.perform(
                delete("/api/product/3"))
                .andExpect(status().isOk());
    }
}
