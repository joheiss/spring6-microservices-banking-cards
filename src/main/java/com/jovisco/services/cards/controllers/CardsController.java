package com.jovisco.services.cards.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jovisco.services.cards.constants.CardsConstants;
import com.jovisco.services.cards.dtos.CardDto;
import com.jovisco.services.cards.dtos.CardsContactInfoDto;
import com.jovisco.services.cards.dtos.CreateCardDto;
import com.jovisco.services.cards.dtos.ErrorResponseDto;
import com.jovisco.services.cards.dtos.ResponseDto;
import com.jovisco.services.cards.services.CardsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "CRUD REST APIs for Cards in Banking Microservices", description = "CRUD REST APIs to CREATE, READ, UPDATE and DELETE Cards in Banking Microservices")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CardsController {

    public static final String CARDS_PATH = "/cards";
    public static final String CARDS_MOBILENUMBER_PATH = CARDS_PATH + "/{mobileNumber}";
    public static final String CARDS_VERSION_PATH = CARDS_PATH + "/version";

    private final CardsService cardsService;

    private final CardsContactInfoDto cardsContactInfoDto;

    private final Environment environment;

    @Value("${build.version}")
    private String buildVersion;

    @Operation(summary = "Fetch a single card by the customer's mobile number", description = "Fetch data from card for a given mobile number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "404", description = "HTTP Status NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/+122234567890\", \"errorCode\": \"404\", \"errorMessage\": \"Card not found ...\", \"errorTime\": \"2024-07-04T11:12:13\"}")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @GetMapping(CARDS_MOBILENUMBER_PATH)
    public ResponseEntity<CardDto> fetchCard(@PathVariable String mobileNumber) {

        var cardDto = cardsService.fetchCard(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardDto);
    }

    @Operation(summary = "Create a card", description = "Create a card")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED", content = @Content(schema = @Schema(implementation = ResponseDto.class), examples = {
                    @ExampleObject(value = "{\"statusCode\": \"201\", \"statusMessage\": \"Card created successfully\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "HTTP Status BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @PostMapping(CARDS_PATH)
    public ResponseEntity<ResponseDto> createCard(@Valid @RequestBody CreateCardDto createCardDto) {

        // create card
        cardsService.createCard(createCardDto);

        // store mobile number in location header
        var headers = new HttpHeaders();
        headers.add("Location", CARDS_PATH + "/" + createCardDto.getMobileNumber());

        var body = ResponseDto.builder()
                .statusCode(CardsConstants.STATUS_201)
                .statusMessage(CardsConstants.MESSAGE_201)
                .build();

        return new ResponseEntity<ResponseDto>(body, headers, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a card", description = "Update a card")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "400", description = "HTTP Status BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "HTTP Status NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/+122234567890\", \"errorCode\": \"404\", \"errorMessage\": \"Card not found ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @PutMapping(CARDS_PATH)
    public ResponseEntity<ResponseDto> updateCard(@Valid @RequestBody CardDto cardDto) {

        var isUpdated = cardsService.updateCard(cardDto);

        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ResponseDto.builder()
                            .statusCode(CardsConstants.STATUS_200)
                            .statusMessage(CardsConstants.MESSAGE_200)
                            .build());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.builder()
                            .statusCode(CardsConstants.STATUS_500)
                            .statusMessage(CardsConstants.MESSAGE_500)
                            .build());

        }
    }

    @Operation(summary = "Delete a card", description = "Delete a card by mobile number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "404", description = "HTTP Status NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/+122234567890\", \"errorCode\": \"404\", \"errorMessage\": \"Card not found ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @DeleteMapping(CARDS_MOBILENUMBER_PATH)
    public ResponseEntity<ResponseDto> deleteCard(@PathVariable String mobileNumber) {

        var isDeleted = cardsService.deleteCard(mobileNumber);

        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ResponseDto.builder()
                            .statusCode(CardsConstants.STATUS_200)
                            .statusMessage(CardsConstants.MESSAGE_200)
                            .build());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.builder()
                            .statusCode(CardsConstants.STATUS_500)
                            .statusMessage(CardsConstants.MESSAGE_500)
                            .build());
        }
    }

    @Operation(summary = "Get build information", description = "Get the current build version that is deployed for this service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK", content = @Content(examples = {
                    @ExampleObject(value = "1.0.0") }, mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/version\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @GetMapping(path = CARDS_VERSION_PATH, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }

    @Operation(summary = "Get Java version", description = "Get the current Java version that is deployed for this service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK", content = @Content(examples = {
                    @ExampleObject(value = "1.0.0") }, mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/java-version\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @GetMapping(path = CARDS_PATH + "/java-version", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @Operation(summary = "Get environment variable", description = "Get the current value of an environment variable that is deployed for this service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK", content = @Content(examples = {
                    @ExampleObject(value = "anything") }, mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/java-version\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @GetMapping(path = CARDS_PATH + "/env-variable/{envVariable}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getEnvVariable(@PathVariable String envVariable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty(envVariable));
    }

    @Operation(summary = "Get contact information", description = "Get contact information for this service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK", content = @Content(schema = @Schema(implementation = CardsContactInfoDto.class), examples = {
                    @ExampleObject(value = "{\"message\": \"Welcome to ...\", \"contact\": {\"name\": \"Jane Doe\", \"email\": \"jane@example.com\"}, \"support\": [\"+1 222 333 4444\", \"+1 555 666 7777\"]}") }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class), examples = {
                    @ExampleObject(value = "{\"apiPath\": \"uri=/api/v1/cards/contact-info\", \"errorCode\": \"500\", \"errorMessage\": \"An error occurred ...\", \"errorTime\": \"2024-07-04T11:12:13\"}") }, mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    @GetMapping(path = CARDS_PATH + "/contact-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CardsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardsContactInfoDto);
    }

}
