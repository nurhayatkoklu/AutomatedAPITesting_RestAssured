import POJO.Fields;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class FieldTest {

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

    public String getRandomCode() {return RandomStringUtils.randomNumeric(3);}

    String fieldsID;
    String fieldCode;
    String fieldName;

    @Test
    public void  AddFields() {

        fieldName=getRandomName();
        fieldCode=getRandomCode();

        Fields fields = new Fields();
        fields.setName(fieldName);
        fields.setCode(fieldCode);
        fields.setType("STRING");
        fields.setSchoolId("5fe07e4fb064ca29931236a5");
        fieldsID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(fields)

                        .when()
                        .post("school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }
    @Test(dependsOnMethods = "AddFields")
    public void AddFieldsNegative() {
        Fields fields = new Fields();
        fields.setName(fieldName);
        fields.setCode(fieldCode);
        fields.setType("STRING");
        fields.setSchoolId("5fe07e4fb064ca29931236a5");

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(fields)

                        .when()
                        .post("school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message", equalTo("The SchoolMessages.EntityField.Title with Code \""+fieldCode+"\" already exists."));

    }
    @Test(dependsOnMethods = "AddFields")
    public void updateFields () {

        fieldName = getRandomName();

        Fields fields = new Fields();
        fields.setId(fieldsID);
        fields.setName(fieldName);
        fields.setCode(fieldCode);
        fields.setType("STRING");
        fields.setSchoolId("5fe07e4fb064ca29931236a5");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(fields)

                .when()
                .put("school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(fieldName));


    }
    @Test(dependsOnMethods = "updateFields")
    public void deleteFieldsById() {

        given()
                .cookies(cookies)
                .pathParam("fieldsID", fieldsID)

                .when()
                .delete("school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body()
                .statusCode(204);


    }
    @Test(dependsOnMethods = "deleteFieldsById")
    public void deleteFieldsByIdNegative(){

        given()
                .cookies(cookies)
                .pathParam("fieldsID", fieldsID)
                .log().uri()
                .when()
                .delete("school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body()
                .statusCode(400);

    }
    @Test (dependsOnMethods = "deleteFieldsById")
    public void updateFieldsNegative(){

        fieldName = getRandomName();

        Fields fields = new Fields();
        fields.setId(fieldsID);
        fields.setName(fieldName);
        fields.setCode(fieldCode);
        fields.setType("STRING");
        fields.setSchoolId("5fe07e4fb064ca29931236a5");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(fields)

                .when()
                .put("school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("EntityField not found"));



    }


    }


