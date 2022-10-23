package Tests;

import POJO.DocumentTypes;
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

public class DocumentTypesTest {
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

    String documentTypesID;
    String documentTypesName;
    String schoolID;
    String[] attachmentStages;

    public String randomName() { return RandomStringUtils.randomAlphabetic(8); }

    @Test
    public void addDocumentTypes() {

        documentTypesName=randomName();
        schoolID="6343bf893ed01f0dc03a509a";
        attachmentStages=new String[]{"CONTRACT"};

        DocumentTypes documentTypes=new DocumentTypes();
        documentTypes.setName(documentTypesName);
        documentTypes.setDescription("Bu bir denemedir");
        documentTypes.setSchoolId(schoolID);
        documentTypes.setAttachmentStages(attachmentStages);



        documentTypesID=

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(documentTypes)

                        .when()
                        .post("school-service/api/attachments")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;
    }
    @Test (dependsOnMethods = "addDocumentTypes")
    public void editDocumentTypes() {
        documentTypesName=randomName();

        DocumentTypes documentTypes=new DocumentTypes();
        documentTypes.setName(documentTypesName);
        documentTypes.setId(documentTypesID);
        documentTypes.setSchoolId(schoolID);
        documentTypes.setAttachmentStages(attachmentStages);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(documentTypes)

                .when()
                .put("school-service/api/attachments")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(documentTypesName))
        ;
    }

    @Test (dependsOnMethods = "editDocumentTypes")
    public void deleteDocumentTypes() {


        given()
                .cookies(cookies)
                .pathParam("documentTypesID", documentTypesID)

                .when()
                .delete("school-service/api/attachments/{documentTypesID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test (dependsOnMethods = "deleteDocumentTypes")
    public void deleteDocumentTypesNegative() {

        given()
                .cookies(cookies)
                .pathParam("documentTypesID", documentTypesID)

                .when()
                .delete("school-service/api/attachments/{documentTypesID}")

                .then()
                .statusCode(400)
                .log().body()
                .body("message", equalTo("GENERAL.ERROR.ATTACHMENT_TYPE_NOT_FOUND"))
        ;
    }

    @Test (dependsOnMethods = "deleteDocumentTypesNegative")
    public void editDocumentTypesNegative() {
        documentTypesName=randomName();
        DocumentTypes documentTypes=new DocumentTypes();
        documentTypes.setId(documentTypesID);
        documentTypes.setName(documentTypesName);
        documentTypes.setSchoolId(schoolID);
        documentTypes.setAttachmentStages(attachmentStages);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(documentTypes)

                .when()
                .put("school-service/api/attachments")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("GENERAL.ERROR.ATTACHMENT_TYPE_NOT_FOUND"))

        ;
    }
}


