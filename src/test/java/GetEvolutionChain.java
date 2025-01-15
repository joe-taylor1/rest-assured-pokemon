import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetEvolutionChain {

    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "https://pokeapi.co/api/v2/evolution-chain/";
    }

    @Test
    public void getEvolutionChain_withID_returns200AndValidBody() {

        int evolutionChainID = 3;
        String pokemonName = "squirtle";

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get(String.valueOf(evolutionChainID))
                        .then()
                        .statusCode(200)
                        .assertThat().body("chain.species.name", equalTo(pokemonName))
                        .assertThat().body("id", equalTo(evolutionChainID))
                        .log().all();

    }

    @Test
    public void getEvolutionChain_withNotFoundID_returns404() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get("100000000000")
                        .then()
                        .statusCode(404);

    }

    @Test
    public void getEvolutionChain_withNoId_returns200AndDefaultPaginatedResponse() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .assertThat().body("count", equalTo(541))
                        .assertThat().body("next", equalTo(baseURI + "?offset=20&limit=20"))
                        .assertThat().body("previous", is(nullValue()))
                        .assertThat().body("results", hasSize(20))
                        .assertThat().body("results[0].url", equalTo(baseURI + "1/"))
                        .assertThat().body("results[19].url", equalTo(baseURI + "20/"));

    }

    @Test
    public void getEvolutionChain_withNoIdAndCustomLimit_returns200() {

        ValidatableResponse validatableResponse =
                given()
                        .queryParam("limit", 50)
                        .queryParam("offset", 100)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .assertThat().body("count", equalTo(541))
                        .assertThat().body("next", equalTo(baseURI + "?offset=150&limit=50"))
                        .assertThat().body("previous", equalTo(baseURI + "?offset=50&limit=50"))
                        .assertThat().body("results", hasSize(50))
                        .assertThat().body("results[0].url", equalTo(baseURI + "101/"))
                        .assertThat().body("results[49].url", equalTo(baseURI + "150/"));

    }

}