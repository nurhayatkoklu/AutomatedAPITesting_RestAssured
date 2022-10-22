
import POJO.Discount;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DiscountTest {

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

    String discountDescription;
    String discountCode;
    String discountPriority;

    String discountID;

    @Test
    public void addDiscount() {

        discountCode=getRandomCode();
        discountDescription=getRandomName();
        discountPriority=getRandomCode();

        Discount discount = new Discount();
        discount.setCode(discountCode);
        discount.setPriority(discountPriority);
        discount.setDescription(discountDescription);
        discountID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(discount)

                        .when()
                        .post("school-service/api/discounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");


    }

    @Test(dependsOnMethods = "addDiscount")
    public void addDiscountNegative() {

        Discount discount = new Discount();
        discount.setCode(discountCode);
        discount.setPriority(discountPriority);
        discount.setDescription(discountDescription);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(discount)

                .when()
                .post("school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Discount with Description \""+discountDescription+"\" already exists."));


    }

    @Test(dependsOnMethods = "addDiscount")
    public void updateDiscount() {

        discountDescription = getRandomName();

        Discount discount = new Discount();
        discount.setId(discountID);
        discount.setDescription(discountDescription);
        discount.setCode(discountCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(discount)

                .when()
                .put("school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(null));


    }

    @Test(dependsOnMethods = "updateDiscount")
    public void deleteDiscountById() {


        given()
                .cookies(cookies)
                .pathParam("discountID", discountID)


                .when()
                .delete("school-service/api/discounts/{discountID}")

                .then()
                .log().body()
                .statusCode(200);

    }

    @Test(dependsOnMethods = "deleteDiscountById")
    public void deleteDiscountByIdNegative() {


        given()
                .cookies(cookies)
                .pathParam("discountID", discountID)
                .log().uri()
                .when()
                .delete("school-service/api/discounts/{discountID}")


                .then()
                .log().body()
                .statusCode(400);

    }

    @Test(dependsOnMethods = "deleteDiscountById")
    public void updateDiscountNegative(){

        discountDescription = getRandomName();

        Discount discount = new Discount();
        discount.setId(discountID);
        discount.setDescription(discountDescription);
        discount.setCode(discountCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(discount)

                .when()
                .put("school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Discount.Error.DISCOUNT_NOT_FOUND"))
        ;

    }


}
