package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

    private Long existingMovieId, nonExistingMovieId;
    private String movieTitle;

    private Map<String, Object> postMovieInstance;

    private String admUsername, admPassword, clientUsername, clientPassword, admToken, clientToken;

    @BeforeEach
    void setUp() throws Exception{
        baseURI = "http://localhost:8080";

        existingMovieId = 1L;
        nonExistingMovieId = 10000L;

        movieTitle = "Star";

        admUsername = "maria@gmail.com";
        admPassword= "123456";
        admToken = TokenUtil.obtainAccessToken(admUsername, admPassword);

        clientUsername = "joao@gmail.com";
        clientPassword = "123456";
        clientToken = TokenUtil.obtainAccessToken(clientUsername,clientPassword);

        postMovieInstance = new HashMap<>();
        postMovieInstance.put("title","Como Treinar Seu Dragão");
        postMovieInstance.put("score",5.0);
        postMovieInstance.put("count", 3);
        postMovieInstance.put("image","https://image.tmdb.org/t/p/original/yfj27wcg80MjSn7Il6iGQ2wkTth.jpg");
    }
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

        given()
                .get("/movies")
                .then()
                .statusCode(200)
                .body("content.title[0]", equalTo("The Witcher"))
                .body("content.title[1]", equalTo("Venom: Tempo de Carnificina"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
        given()
                .get("/movies?title={movieTitle}", movieTitle)
                .then()
                .statusCode(200)
                .body("content.title", hasItems("Star Wars: A Guerra dos Clones","Rogue One: Uma História Star Wars"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
        given()
                .get("/movies/{id}", existingMovieId)
                .then()
                .statusCode(200)
                .body("title", equalTo("The Witcher"))
                .body("score", is(4.5F))
                .body("count", is(2))
                .body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .get("/movies/{id}", nonExistingMovieId)
                .then()
                .statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {

        postMovieInstance.put("title","    ");
        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + admToken)
                .body(newMovie)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(422)
                .body("error", equalTo("Dados inválidos"))
                .body("errors.fieldName[0]",equalTo("title"))
                .body("errors.message[0]", equalTo("Campo requerido"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newMovie)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-Type","application/json")
                .header("Authorization", "Bearer " + clientToken + "aaaaaaaa")
                .body(newMovie)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(401);
	}
}
