package co.com.nequi.exception;

import co.com.nequi.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends NequiException {
    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }
    public TechnicalException(Throwable throwable, TechnicalMessage technicalMessage) {
        super(throwable, technicalMessage);
    }
}
