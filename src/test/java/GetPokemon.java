import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

import io.restassured.response.ValidatableResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class GetPokemon {

    int statusCode200 = 200;
    int statusCode404 = 404;
    int pokemonId = 7; //using int vor validation purposes but need to convert to string for Get usage
    String pokemonSquirtle = "squirtle";
    
    @BeforeTest
    public void setUp() {

        RestAssured.baseURI = "https://pokeapi.co/api/v2/pokemon/";

        String classpath = System.getProperty("java.class.path");
        System.out.println("Classpath: " + classpath); //for debugging classpath issues 
    }

    @Test
    public void getPokemon_conformsToJsonSchema() {

        given()
                .when()
                .get()
                .then()
                .statusCode(statusCode200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/pokemon-schema.json"));
    }

    @Test
    public void getPokemon_withId_returns200AndValidBody() {


        
        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get(String.valueOf(pokemonId))
                        .then()
                        .statusCode(statusCode200)
                        .assertThat().body("name", equalTo(pokemonSquirtle))
                        .assertThat().body("id", equalTo(pokemonId))
                        .log().all(); // logging of the whole test, can use different methods to log specific aspects

    }

    @Test
    public void getPokemon_withName_returns200AndValidBody() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get(pokemonSquirtle)
                        .then()
                        .statusCode(statusCode200)
                        .assertThat().body("name", equalTo(pokemonSquirtle))
                        .assertThat().body("id", equalTo(pokemonId));

    }

    @Test
    public void getPokemon_withNotFoundID_returns404() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get("10000000000000000000")
                        .then()
                        .statusCode(statusCode404);

    }

    @Test
    public void getPokemon_withNotFoundName_returns404() {

        
        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get("TheSun")
                        .then()
                        .statusCode(statusCode404);

    }

    @Test
    public void getPokemon_paginated_defaultLimit() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get()
                        .then()
                        .statusCode(statusCode200)
                        .assertThat().body("count", equalTo(1302))
                        .assertThat().body("results", hasSize(20));

    }

    @Test
    public void getPokemon_paginated_customLimit() {

        ValidatableResponse validatableResponse =
                given()
                        .queryParam("limit", 50)
                        .when()
                        .get()
                        .then()
                        .statusCode(statusCode200)
                        .assertThat().body("count", equalTo(1302))
                        .assertThat().body("results", hasSize(50));

    }

    @Test
    public void getPokemon_paginated_customOffset() {

        ValidatableResponse validatableResponse =
                given()
                        .queryParam("limit", 20) //this adds a query parameter limiting the length of the response
                        .queryParam("offset", 20) //this further tests the params for pagination by requesting the second page
                        .when()
                        .get()
                        .then()
                        .statusCode(statusCode200)
                        .assertThat().body("results[0].name", equalTo("spearow")) //asserting the first result returned in the response array
                        .assertThat().body("results[-1].name", equalTo("wigglytuff")); //[-1] array locator checks the last item in the array, would be the same as 19
    }

    /*TODO
    * For the test above you could use dynamic values instead of the hard coded values in both asserts.
    * To do this you could send another get request within the test to get the information for the Pokemon with an ID of 21
    * as the ID of 21 would be the 20th item in a total array.
    *
    * You would then store the information for pokemon 21 and compare it to what is returned in the main request.
    * The benefits of this would be that if the data changes e.g the name of the pokemon
    * in position 21, then this test would not still pass and continue to test the pagination functionality*/

}

