package com.unafamilia;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class EventResourceTest {

    @BeforeEach
    @Transactional
    public void clear_database() {
        Event.deleteAll();
    }

    @Test
    public void should_create_new_event() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "event_name": "test_raid",
                        "date": "2012-04-23T18:25:43.511Z",
                        "organizer_id": 1,
                        "drafted_players" : [{"character_id" : 1, "status": "ABSENT"}, {"character_id" : 2, "status": "ACCEPTED"}],
                        "activity_name" : "Raid"}
                        """)
                .post("/v1/event")
                .then()
                .statusCode(201)
                .header("Location", containsString("/v1/event/"));

        Assertions.assertEquals(1, Event.count());
    }

    @Test
    public void should_create_new_event_and_retrieve_it() {
        var response = given()
                .when()
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "event_name": "test_raid",
                        "date": "2012-04-23T18:25:43.511Z",
                        "organizer_id": 1,
                        "drafted_players" : [{"character_id" : 1, "status": "ABSENT"}, {"character_id" : 2, "status": "ACCEPTED"}],
                        "activity_name" : "Raid"}
                        """)
                .post("/v1/event");

        var url = response.getHeader("Location");

        given()
                .when()
                .get(url)
                .then()
                .statusCode(200);
        Assertions.assertEquals(1, Event.count());
    }

    @Test
    @Transactional
    public void should_return_all_events() {
        //Given
        int expectedNumberOfEvents = 10;
        IntStream.range(0, expectedNumberOfEvents)
                .forEach(x -> {
                    given()
                            .contentType(ContentType.JSON)
                            .body("""
                                    {
                                    "event_name": "test_raid",
                                    "date": "2012-04-23T18:25:43.511Z",
                                    "organizer_id": 1,
                                    "drafted_players" : [{"character_id" : 1, "status": "ABSENT"}, {"character_id" : 2, "status": "ACCEPTED"}],
                                    "activity_name" : "Raid"}
                                    """)
                            .post("/v1/event")
                            .then()
                            .statusCode(201);
                });

        //Then
        given()
                .when()
                .get("/v1/event")
                .then()
                .statusCode(200)
                .body("size()", is(expectedNumberOfEvents));
    }
}