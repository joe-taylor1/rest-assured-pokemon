import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

import io.restassured.response.ValidatableResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class GetPokemon {

    int squirtleID = 7; //using int vor validation purposes but need to convert to string for Get usage
    String squirtleName = "squirtle";
    String pokemonJsonSchemaPath = "schemas/pokemon-schema.json";


    @BeforeTest
    public void setUp() {

        RestAssured.baseURI = "https://pokeapi.co/api/v2/pokemon/";

        String classpath = System.getProperty("java.class.path");
        System.out.println("Classpath: " + classpath); //for debugging classpath issues 
    }

    @Test
    public void getPokemon_conformsToJsonSchema() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .assertThat()
                        .body(matchesJsonSchemaInClasspath(pokemonJsonSchemaPath));

    }

    @Test
    public void getPokemon_withId_returns200AndValidBody() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get(String.valueOf(squirtleID))
                        .then()
                        .statusCode(200)
                        .assertThat().body("name", equalTo(squirtleName))
                        .assertThat().body("id", equalTo(squirtleID))
                        .log().all(); // logging of the whole test, can use different methods to log specific aspects

    }

    @Test
    public void getPokemon_withName_returns200AndValidBody() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get(squirtleName)
                        .then()
                        .statusCode(200)
                        .assertThat().body("name", equalTo(squirtleName))
                        .assertThat().body("id", equalTo(squirtleID));

    }

    @Test
    public void getPokemon_withNotFoundID_returns404() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get("10000000000000000000")
                        .then()
                        .statusCode(404);

    }

    @Test
    public void getPokemon_withNotFoundName_returns404() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get("TheSun")
                        .then()
                        .statusCode(404);

    }

    @Test
    public void getPokemon_paginated_defaultLimitAndOffset() {

        ValidatableResponse validatableResponse =
                given()
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .assertThat().body("count", equalTo(1302))
                        .assertThat().body("results", hasSize(20))
                        .assertThat().body("results[0].name", equalTo("bulbasaur"))
                        .assertThat().body("results[19].name", equalTo("raticate"));

    }

    @Test
    public void getPokemon_paginated_customLimitAndOffset() {

        ValidatableResponse validatableResponse =
                given()
                        .queryParam("limit", 30) //this adds a query parameter limiting the length of the response
                        .queryParam("offset", 30) //this further tests the params for pagination by requesting the second page
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .assertThat().body("results[0].name", equalTo("nidoqueen")) //asserting the first result returned in the response array
                        .assertThat().body("results[-1].name", equalTo("poliwag")); //[-1] array locator checks the last item in the array, would be the same as 19

    }

    /*TODO
     * For the test above you could use dynamic values instead of the hard coded values in both asserts.
     */

    

}

