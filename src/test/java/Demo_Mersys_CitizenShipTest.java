
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Demo_Mersys_CitizenShipTest {
    private RequestSpecification reqSpec;
    private Cookies cookies;
    private String citizen_id;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://demo.mersys.io";

        reqSpec = given()
                .log().body()
                .contentType(ContentType.JSON);
    }
    @Test(priority = 2)
    public void loginCampusTest() {
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("username", "richfield.edu");
        credentials.put("password", "Richfield2020!");
        credentials.put("rememberME", "true");

        cookies = given()
                .spec(reqSpec)
                .body(credentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().detailedCookies();
    }
    @Test(priority = 1)
    public void loginCampusNegativeTest() {
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("username", "richfield.edu");
        credentials.put("password", "Richfield2020");
        credentials.put("rememberME", "true");

        given()
                .spec(reqSpec)
                .body(credentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
    @Test(priority = 3)
    public void createCitizenshipTest() {
        HashMap<String, String> reqBody = new HashMap<>();
        reqBody.put("name", "ABZ123Z");
        reqBody.put("shortName", "ABZZ");

        citizen_id =  given()
                .spec(reqSpec)
                .cookies(cookies)
                .body(reqBody)
                .when()
                .post("/school-service/api/citizenships")
                .then()
                .log().body()
                .statusCode(201)
                .body("name", equalTo(reqBody.get("name")))
                .body("shortName", equalTo(reqBody.get("shortName")))
                .extract().jsonPath().getString("id");
    }
    @Test(priority = 4)
    public void getCitizenshipTest() {
        given()
                .spec(reqSpec)
                .cookies(cookies)
                .when()
                .get("/school-service/api/citizenships/" + citizen_id)
                .then()
                .statusCode(200);
    }
    @Test(priority = 5)
    public void createFeesNegativeTest() {
        HashMap<String, String> reqBody = new HashMap<>();
        reqBody.put("name", "ABZ123Z");
        reqBody.put("shortName", "ABZZ");

        given()
                .spec(reqSpec)
                .cookies(cookies)
                .body(reqBody)
                .when()
                .post("/school-service/api/fee-types")
                .then()
                .log().body()
                .statusCode(400);
    }
    @Test(priority = 6)
    public void editCitizenshipTest() {
        HashMap<String, String> updateReqBody = new HashMap<>();
        updateReqBody.put("id", citizen_id);
        updateReqBody.put("name", "XYZ098A");
        updateReqBody.put("code", "XYZA");

        given()
                .spec(reqSpec)
                .cookies(cookies)
                .body(updateReqBody)
                .when()
                .put("/school-service/api/citizenships")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(updateReqBody.get("name")));
    }
    @Test(priority = 7)
    public void deleteCitizenshipTest() {
        given()
                .spec(reqSpec)
                .cookies(cookies)
                .when()
                .delete("/school-service/api/citizenships/" + citizen_id)
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test(priority = 8)
    public void getCitizenshipNegativeTest() {
        given()
                .spec(reqSpec)
                .cookies(cookies)
                .when()
                .get("/school-service/api/citizenships/" + citizen_id)
                .then()
                .statusCode(400);
    }
    @Test(priority = 9)
    public void deleteFeesNegativeTest() {
        given()
                .spec(reqSpec)
                .cookies(cookies)
                .when()
                .delete("/school-service/api/citizenships/" + citizen_id)
                .then()
                .log().body()
                .statusCode(400);
    }
}
