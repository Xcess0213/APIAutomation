import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class GoRestUserTest {
    private RequestSpecification reqSpec;
    private HashMap<String, String> requestBody;
    private Object userId;

    @BeforeClass
    public void setup() {

        RestAssured.baseURI = "https://gorest.co.in";

        reqSpec = given()
                .log().uri()
                .header("Authorization", "Bearer df67db584c764e803d2a0a18f665ee09a4a456373dbe8fd736fbfbf367ba44f7")
                .contentType(ContentType.JSON);

        requestBody = new HashMap<>();
        requestBody.put("name", "Tom Cruise");
        requestBody.put("email", "xyz123@technostudy.com");
        requestBody.put("gender", "female");
        requestBody.put("status", "active");
    }

    @Test
    public void createUserTest() {

        userId = given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .body("name", equalTo(requestBody.get("name")))
                .statusCode(201)
                .extract().path("id");
    }

    @Test(dependsOnMethods = "createUserTest")
    public void editUserTest() {
        HashMap<String, String> editUser = new HashMap<>();
        editUser.put("name", "Tom Cruise");
        editUser.put("email", "Abc098@technostudy.com");
        editUser.put("gender", "female");
        editUser.put("status", "active");

        given()
                .spec(reqSpec)
                .body(editUser)
                .when()
                .put("/public/v2/users/" + userId)
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "editUserTest")
    public void deleteUserTest() {

        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + userId)
                .then()
                .log().body()
                .statusCode(204);


    }
}
