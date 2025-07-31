package co.com.nequi.exception;

import co.com.nequi.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends NequiException {
    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }
    public BusinessException(String message, TechnicalMessage technicalMessage) {
        super(message, technicalMessage);
    }
}
