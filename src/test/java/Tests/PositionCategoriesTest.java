package Tests;

import POJO.PositionCategories;
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

public class PositionCategoriesTest {

    Cookies cookies;

    @BeforeClass
    public void loginCampus() {

        baseURI = "https://demo.mersys.io/";

        Map<String, String> credential = new HashMap<>();
        credential.put("username", "richfield.edu");
        credential.put("password", "Richfield2020!");
        credential.put("rememberMe", "true");
        cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(credential)

                        .when()
                        .post("auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();
    }
    public String getRandomName() {return RandomStringUtils.randomAlphabetic(8).toLowerCase(); }

    String positionCategoryID;
    String positionCategoryName;

    @Test
    public void  AddPositionCategory() {
        positionCategoryName=getRandomName();

        PositionCategories positionCategory=new PositionCategories();


        positionCategory.setName(positionCategoryName);
        positionCategoryID=

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(positionCategory)

                        .when()
                        .post("school-service/api/position-category")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }
    @Test(dependsOnMethods = "AddPositionCategory")
    public void AddPositionCategoryNegative() {

        PositionCategories positionCategory=new PositionCategories();
        positionCategory.setName(positionCategoryName);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(positionCategory)

                .when()
                .post("school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Position Category with Name \""+positionCategoryName+"\" already exists."));

    }
    @Test(dependsOnMethods = "AddPositionCategoryNegative")
    public void updatePositionCategory () {

        positionCategoryName = getRandomName();

        PositionCategories positionCategory=new PositionCategories();
        positionCategory.setName(positionCategoryName);
        positionCategory.setId(positionCategoryID);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(positionCategory)

                .when()
                .put("school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(positionCategoryName));


    }
    @Test(dependsOnMethods = "updatePositionCategory")
    public void deletePositionCategoryById() {

        given()
                .cookies(cookies)
                .pathParam("positionCategoryID", positionCategoryID)

                .when()
                .delete("school-service/api/position-category/{positionCategoryID}")

                .then()
                .log().body()
                .statusCode(204);


    }
    @Test(dependsOnMethods = "deletePositionCategoryById")
    public void deletePositionCategoryByIdNegative(){

        given()
                .cookies(cookies)
                .pathParam("positionCategoryID", positionCategoryID)
                .log().uri()
                .when()
                .delete("school-service/api/position-category/{positionCategoryID}")

                .then()
                .log().body()
                .statusCode(400);

    }
    @Test (dependsOnMethods = "deletePositionCategoryByIdNegative")
    public void updatePositionCategoryNegative(){

        positionCategoryName = getRandomName();
        PositionCategories positionCategory=new PositionCategories();
        positionCategory.setName(positionCategoryName);
        positionCategory.setId(positionCategoryID);




        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(positionCategory)

                .when()
                .put("school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Position Category"));



    }
}
