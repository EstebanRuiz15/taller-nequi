package co.com.nequi.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TechnicalMessageTest {
    @Test
    public void shouldGetInternalServerErrorMessage() {
        Assertions.assertEquals("¡No pudimos hacer esto! Estamos tratando de arreglarlo.", TechnicalMessage.INTERNAL_SERVER_ERROR.getMessage());
        Assertions.assertEquals("500", TechnicalMessage.INTERNAL_SERVER_ERROR.getExternalCode());
        Assertions.assertEquals("11-500", TechnicalMessage.INTERNAL_SERVER_ERROR.getCode());
    }

    @Test
    public void shouldGetGenericErrorWhenExternalCodeNotExists() {
        TechnicalMessage technicalMessage = TechnicalMessage.findByExternalCode("4000");
        Assertions.assertEquals(TechnicalMessage.GENERIC_ERROR, technicalMessage);
        Assertions.assertEquals("Tuvimos un problema, estamos tratando de arreglarlo.", technicalMessage.getMessage());
        Assertions.assertEquals("", technicalMessage.getExternalCode());
        Assertions.assertEquals("500", technicalMessage.getCode());
    }
}
