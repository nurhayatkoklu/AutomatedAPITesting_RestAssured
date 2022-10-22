import POJO.Nationalities;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class NationalitiesTest {

    Cookies cookies;


    @BeforeClass
    public void loginCampus() {

        baseURI="https://demo.mersys.io/";


        Map<String, String> credential = new HashMap<>();
        credential.put("username", "richfield.edu");
        credential.put("password", "Richfield2020!");

        cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(credential)

                        .when()

                        .post("auth/login")

                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
        ;
    }

    String nationalitiesID;
    String nationalitiesName;

    public String randomName() { return RandomStringUtils.randomAlphabetic(8); }

    @Test
    public void addNationalities() {

        nationalitiesName=randomName();

        Nationalities nationalities=new Nationalities();
        nationalities.setName(nationalitiesName);

        nationalitiesID=

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(nationalities)

                        .when()
                        .post("school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;
    }

    @Test (dependsOnMethods = "addNationalities")
    public void addNationalitiesNegative() {


        Nationalities nationalities=new Nationalities();
        nationalities.setName(nationalitiesName);

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(nationalities)

                        .when()
                        .post("school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message", equalTo("The Nationality with Name \""+nationalitiesName+"\" already exists."))

        ;
    }

    @Test (dependsOnMethods = "addNationalitiesNegative")
    public void editNationalities() {

        nationalitiesName=randomName();

        Nationalities nationalities=new Nationalities();
        nationalities.setName(nationalitiesName);
        nationalities.setId(nationalitiesID);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(nationalities)

                .when()
                .put("school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(nationalitiesName))
        ;
    }

    @Test (dependsOnMethods = "editNationalities")
    public void deleteNationalities() {


        given()
                .cookies(cookies)
                .pathParam("nationalitiesID", nationalitiesID)

                .when()
                .delete("school-service/api/nationality/{nationalitiesID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test (dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegative() {

        given()
                .cookies(cookies)
                .pathParam("nationalitiesID", nationalitiesID)

                .when()
                .delete("school-service/api/nationality/{nationalitiesID}")

                .then()
                .statusCode(400)
                .body("message", equalTo("Nationality not  found"))
        ;
    }

    @Test (dependsOnMethods = "deleteNationalitiesNegative")
    public void editNationalitiesNegative() {
        nationalitiesName=randomName();

        Nationalities nationalities=new Nationalities();
        nationalities.setName(nationalitiesName);
        nationalities.setId(nationalitiesID);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(nationalities)

                .when()
                .put("school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Nationality"))

        ;
    }
}

