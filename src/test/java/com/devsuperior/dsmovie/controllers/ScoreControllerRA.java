package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ScoreControllerRA {

    private Map<String, Object> putScoreInstance;

    private String admUsername, admPassword, clientUsername, clientPassword, admToken, clientToken;

    @BeforeEach
    void setUp() throws Exception{
        baseURI = "http://localhost:8080";

        admUsername = "maria@gmail.com";
        admPassword= "123456";
        admToken = TokenUtil.obtainAccessToken(admUsername, admPassword);

        clientUsername = "joao@gmail.com";
        clientPassword = "123456";
        clientToken = TokenUtil.obtainAccessToken(clientUsername,clientPassword);

        putScoreInstance = new HashMap<>();

        putScoreInstance.put("movieId", 1L);
        putScoreInstance.put("score", 3.3);
    }
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
        putScoreInstance.put("movieId",1000000L);
        JSONObject newScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + admToken)
                .body(newScore)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
        putScoreInstance.put("movieId", "   ");
        JSONObject newScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + admToken)
                .body(newScore)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("error",equalTo("Dados inválidos"))
                .body("errors.fieldName[0]",equalTo("movieId"))
                .body("errors.message[0]",equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
        putScoreInstance.put("score", -1.0);
        JSONObject newScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + admToken)
                .body(newScore)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("error",equalTo("Dados inválidos"))
                .body("errors.fieldName[0]",equalTo("score"))
                .body("errors.message[0]",equalTo("Valor mínimo 0"));
	}
}
