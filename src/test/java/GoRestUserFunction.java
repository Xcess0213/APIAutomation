import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUserFunction {

    private RequestSpecification reqSpec;
    private Object user_id;
    private HashMap<String, String> requestBody;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://gorest.co.in";

        reqSpec = given()
                .log().body()
                .header("Authorization","Bearer df67db584c764e803d2a0a18f665ee09a4a456373dbe8fd736fbfbf367ba44f7")
                .contentType(ContentType.JSON);

        requestBody = new HashMap<>();
        requestBody.put("name", "Abc Code");
        requestBody.put("email", "abc123@comcast.com");
        requestBody.put("gender", "female");
        requestBody.put("status", "active");
    }
    @Test
    public void createUserTest() {

   user_id =  given()
                .spec(reqSpec)
                .body(requestBody)
          .when()
                .post("/public/v2/users")
          .then()
                  .log().body()
                  .statusCode(201)
                  .body("name", equalTo(requestBody.get("name")))
                  .extract().path("id");

    }
    @Test(dependsOnMethods = "createUserTest")
    public void createUserNegativeTest() {

        given()
                .spec(reqSpec)
                .body(requestBody)
          .when()
                .post("/public/v2/users")
           .then()
                .log().body()
                .statusCode(422)
                .body("message[0]", equalTo("has already been taken"));
    }
    @Test(dependsOnMethods = "createUserNegativeTest")
    public void getUserAndValidate() {

        given()
                .spec(reqSpec)
                .when()
                .get("/public/v2/users/" + user_id)
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(requestBody.get("name")))
                .body("email", equalTo(requestBody.get("email")))
                .body("gender", equalTo(requestBody.get("gender")))
                .body("status", equalTo(requestBody.get("status")));
    }

    @Test(dependsOnMethods = "getUserAndValidate")
    public void editUserTest() {
        HashMap<String, String> reqBodyEdit = new HashMap<>();
        reqBodyEdit.put("name", "joe Abc ");

        given()
                .spec(reqSpec)
                .body(reqBodyEdit)
            .when()
                .put("/public/v2/users/" + user_id)
            .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(reqBodyEdit.get("name")));

    }
    @Test(dependsOnMethods = "editUserTest")
    public void deleteUserTest() {
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user_id)
                .then()
                .log().body()
                .statusCode(204);
    }
    @Test(dependsOnMethods = "deleteUserTest")
    public void deleteUserNegativeTest() {

        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user_id)
                .then()
                .log().body()
                .statusCode(404);

    }





}
