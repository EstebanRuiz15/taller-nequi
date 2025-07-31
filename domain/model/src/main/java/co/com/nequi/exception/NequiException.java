package co.com.nequi.exception;

import co.com.nequi.enums.TechnicalMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NequiException extends Exception {

    private final TechnicalMessage technicalMessage;

    public NequiException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    public NequiException(Throwable throwable, TechnicalMessage technicalMessage) {
        super(throwable);
        this.technicalMessage = technicalMessage;
    }
}
