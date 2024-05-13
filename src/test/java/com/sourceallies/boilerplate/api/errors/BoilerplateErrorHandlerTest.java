package com.sourceallies.boilerplate.api.errors;

import net.bytebuddy.utility.RandomString;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoilerplateErrorHandlerTest {
    private BoilerplateErrorHandler sut;

    @BeforeEach
    void setup() {
        sut = new BoilerplateErrorHandler();
    }

    @Test
    void givenA_BoilerplateConflictingResourceException_whenHandlingErrorThenFormatTheErrorProperly() {
        String expectedMessage = RandomString.make();
        var actual = sut.conflictingResource(new BoilerplateConflictingResourceException(expectedMessage));
        assertThat(actual.getReason()).isEqualTo(expectedMessage);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    void givenA_BoilerplateNotFoundException_whenHandlingErrorThenFormatTheErrorProperly() {
        String expectedMessage = RandomString.make();
        var actual = sut.resourceNotFound(new BoilerplateNotFoundException(expectedMessage));
        assertThat(actual.getReason()).isEqualTo(expectedMessage);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void givenA_DataIntegrityViolationException_whenHandlingErrorThenFormatTheErrorProperly() {
        var actual = sut.dataIntegrityViolation(new DataIntegrityViolationException(RandomString.make()));
        assertThat(actual.getReason()).isEqualTo("A conflict occurred with the request.");
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    }

    private static FieldError makeError() {
        return new FieldError(
            RandomString.make(),
            RandomString.make(),
            RandomString.make(),
            false,
            null,
            null,
            RandomString.make()
        );
    }

    @Test
    void givenA_MethodArgumentNotValidException_whenHandlingErrorThenFormatTheErrorProperly() {
        var errorOne = makeError();
        var errorTwo = makeError();
        var errors = List.of(errorOne, errorTwo);
        var err = mock(MethodArgumentNotValidException.class);

        when(err.getFieldErrors()).thenReturn(errors);

        var actual = sut.methodArgumentNotValid(err);
        SoftAssertions.assertSoftly(softly -> {
            var props = actual.getBody().getProperties();
            softly.assertThat(props).isNotNull();
            softly.assertThat(props).containsKey("errors");
            softly.assertThat(props.get("errors"))
                .asList()
                .containsExactlyInAnyOrder(errors.stream().map(e ->
                    BoilerplateErrorHandler.Error.builder()
                        .field(e.getField())
                        .reason(e.getDefaultMessage())
                        .invalidValue(e.getRejectedValue())
                        .build()
                ).toArray())
            ;
        });
    }
}