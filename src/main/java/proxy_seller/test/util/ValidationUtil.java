package proxy_seller.test.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import proxy_seller.test.exception.BadRequestException;
import proxy_seller.test.exception.NotFoundException;

import java.lang.reflect.Constructor;

@UtilityClass
public class ValidationUtil {


    @SneakyThrows
    public void validateOrBadRequest(boolean condition, String message) {
        validate(condition, message, BadRequestException.class);
    }

    @SneakyThrows
    public void validateOrNotFound(boolean condition, String message) {
        validate(condition, message, NotFoundException.class);
    }

    @SneakyThrows
    private void validate(boolean condition, String message, Class<? extends RuntimeException> exceptionClazz) {
        if (!condition) {
            Constructor<? extends RuntimeException> defaultConstructor = exceptionClazz.getConstructor(String.class);
            throw defaultConstructor.newInstance(message);
        }
    }
}
