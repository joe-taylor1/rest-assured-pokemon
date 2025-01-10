import io.restassured.RestAssured;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetEvolutionChain {

    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "https://pokeapi.co/api/v2/evolution-chain/";
    }

    @Test
    public void getEvolutionChain_withID_returns200AndValidBody() {
        given()
                .when()
                .get("3")
                .then()
                .statusCode(200)
                .assertThat().body("chain.species.name", equalTo("squirtle"))
                .assertThat().body("id", equalTo(3))
                .log().all();
    }

    @Test
    public void getEvolutionChain_withNotFoundID_returns404() {
        given()
                .when()
                .get("100000000000")
                .then()
                .statusCode(404);
    }

    @Test
    public void getEvolutionChain_withNoId_returns200AndPaginatedResponse() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .assertThat().body("chain.species.name", equalTo("squirtle"));
        //Todo fix the assert
    }


}
