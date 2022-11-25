package com.unafamilia.order;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class OrderResourceTest {

    @Test
    public void testCreateOrder() {
        var response = given()
                .when()
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "discord_message_id": 1111,
                        "discord_user_id": 1,
                        "wow_user_id": null,
                        "character_name" : "Lockedupnyly",
                        "items" : [
                            { "item_id": 1, "quantity": 20 },
                            { "item_id": 2, "quantity": 20 }
                        ]
                        }
                        """)
                .post("/v1/order");

        String url = response.getHeader("Location");

                response.then()
                .statusCode(201)
                .header("Location", containsString("/v1/order/"));


        given()
                .when().get(url)
                .then()
                .statusCode(200);
    }

    @Test
    public void testPayForOrderData() {
        var url = createNewOrderAndReturnLocation();

        given()
                .contentType(ContentType.JSON)
                .when().post(url + "/paid")
                .then()
                .statusCode(200);
    }

    @Test
    public void testFulfillOrder() {
        var url = createNewOrderAndReturnLocation();
        given()
                .contentType(ContentType.JSON)
                .when().post(url + "/fulfill")
                .then()
                .statusCode(200);
    }

    @Test
    public void testAddAmountDueForOrder() {
        var url = createNewOrderAndReturnLocation();
        long amountDue = 10000;
        given()
                .contentType(ContentType.JSON)
                .when().post(url + "/due/"  + amountDue)
                .then()
                .statusCode(200);
    }

    @Test
    public void testCancelForOrderData() {
        var url = createNewOrderAndReturnLocation();
        given()
                .contentType(ContentType.JSON)
                .when().post(url + "/cancel")
                .then()
                .statusCode(200);
    }

    private String createNewOrderAndReturnLocation() {
        var response = given()
                .when()
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "discord_message_id": 1111,
                        "discord_user_id": 1,
                        "wow_user_id": null,
                        "character_name" : "Lockedupnyly",
                        "items" : [
                            { "item_id": 1, "quantity": 20 },
                            { "item_id": 2, "quantity": 20 }
                        ]
                        }
                        """)
                .post("/v1/order");

        return response.getHeader("Location");
    }
}