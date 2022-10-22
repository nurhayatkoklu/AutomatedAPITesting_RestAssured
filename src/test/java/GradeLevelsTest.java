import POJO.gradeLevels;
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

public class GradeLevelsTest {

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

    String gradeLevelID;
    String gradeLevelName;
    String gradeLevelshortName;
    String nextGradeLevel;
    String order;

    public String randomCode() { return RandomStringUtils.randomAlphanumeric(4).toLowerCase(); }
    public String randomOrder() { return RandomStringUtils.randomNumeric(4); }

    @Test
    public void addGradeLevel() {

        gradeLevelName=randomName();
        gradeLevelshortName=randomCode();
        nextGradeLevel=null;
        order=randomOrder();


        gradeLevels gradeLevel=new gradeLevels();
        gradeLevel.setName(gradeLevelName);
        gradeLevel.setShortName(gradeLevelshortName);
        gradeLevel.setOrder(order);

        gradeLevelID=

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(gradeLevel)

                        .when()
                        .post("school-service/api/grade-levels")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;
    }

    @Test (dependsOnMethods = "addGradeLevel")
    public void addGradeLevelNegative() {

        nextGradeLevel=null;



        gradeLevels gradeLevel=new gradeLevels();
        gradeLevel.setName(gradeLevelName);
        gradeLevel.setShortName(gradeLevelshortName);
        gradeLevel.setOrder(order);


                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(gradeLevel)

                        .when()
                        .post("school-service/api/grade-levels")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message", equalTo("The Grade Level with Name \""+gradeLevelName+"\" already exists."))

        ;
    }
    @Test (dependsOnMethods = "addGradeLevelNegative")
    public void editGradeLevel() {

        gradeLevelName=randomName();
        gradeLevelshortName=randomCode();
        nextGradeLevel=null;
        order=randomOrder();

        gradeLevels gradeLevel=new gradeLevels();
        gradeLevel.setName(gradeLevelName);
        gradeLevel.setShortName(gradeLevelshortName);
        gradeLevel.setOrder(order);
        gradeLevel.setId(gradeLevelID);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(gradeLevel)

                .when()
                .put("school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(gradeLevelName))
        ;
    }



    @Test (dependsOnMethods = "editGradeLevel")
    public void deleteGradeLevel() {


        given()
                .cookies(cookies)
                .pathParam("gradeLevelID", gradeLevelID)

                .when()
                .delete("school-service/api/grade-levels/{gradeLevelID}")

                .then()
                .statusCode(200)
        ;
    }

    @Test (dependsOnMethods = "deleteGradeLevel")
    public void deleteGradeLevelNegative() {


        given()
                .cookies(cookies)
                .pathParam("gradeLevelID", gradeLevelID)

                .when()
                .delete("school-service/api/grade-levels/{gradeLevelID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Grade Level not found."))
        ;
    }

    @Test (dependsOnMethods = "deleteGradeLevelNegative")
    public void editGradeLevelNegative() {

        gradeLevelName=randomName();
        gradeLevelshortName=randomCode();
        nextGradeLevel=null;
        order=randomOrder();


        gradeLevels gradeLevel=new gradeLevels();
        gradeLevel.setName(gradeLevelName);
        gradeLevel.setShortName(gradeLevelshortName);
        gradeLevel.setOrder(order);
        gradeLevel.setId(gradeLevelID);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(gradeLevel)

                .when()
                .put("school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Grade Level not found."))

        ;
    }



    public String randomName() {

        return RandomStringUtils.randomAlphabetic(8);
    }

}
