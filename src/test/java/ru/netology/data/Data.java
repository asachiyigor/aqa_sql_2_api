package ru.netology.data;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import static io.restassured.RestAssured.given;

@Value
public class Data {

    public Data() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        String code;
    }

    @Value
    public static class VerifyInfo {
        String login;
        String code;
    }

    public static VerifyInfo getVerifyInfo1(String code) {
        return new VerifyInfo("vasya", code);
    }

    @Value
    public static class CardTransferInfo {
        String from;
        String to;
        String amount;
    }

    public static CardTransferInfo getCardTransferInfo(String from, String to, String amount) {
        return new CardTransferInfo(from, to, amount);
    }

    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void login() {
        Gson gson = new Gson();
        String jsonUserData = gson.toJson(getAuthInfo());
        given()
                .spec(requestSpec)
                .body(jsonUserData)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String verify(String code) {
        VerifyInfo verifyData = getVerifyInfo1(code);
        Gson gson = new Gson();
        String jsonVerifyData = gson.toJson(verifyData);
        return given()
                .spec(requestSpec)
                .body(jsonVerifyData)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract().
                jsonPath().getString("token");
    }

    public static void transfer(String token, String jsonCardData) {
        given()
                .header("Authorization", "Bearer " + token)
                .spec(requestSpec)
                .body(jsonCardData)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }

    public static ValidatableResponse showCards(String token){
        return given()
                .header("Authorization", "Bearer " + token)
                .spec(requestSpec)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}