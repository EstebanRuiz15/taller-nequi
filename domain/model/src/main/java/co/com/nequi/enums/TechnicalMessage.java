package co.com.nequi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {
    GENERIC_ERROR("500", "", "Tuvimos un problema, estamos tratando de arreglarlo."),
    INTERNAL_SERVER_ERROR("11-500", "500", "¡No pudimos hacer esto! Estamos tratando de arreglarlo."),
    DATABASE_ERROR("1-99", "99", "¡Ups! Tenemos un problema, Estamos tratando de arreglarlo."),
    FINACLE_SYSTEM_ERROR("2-60017", "60017", "¡No pudimos hacer esto! Reinténtalo en un rato."),
    INVALID_PARAMETER("400", "400", "Uno o más parámetros son inválidos o faltan."),
    USER_NOT_FOUND("404", "404", "No se encontró el usuario solicitado.");

    private final String code;
    private final String externalCode;
    private final String message;

    public static TechnicalMessage findByExternalCode(String code) {
        return Arrays.stream(TechnicalMessage.values())
                .filter(msg -> msg.getExternalCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(GENERIC_ERROR);
    }
}
