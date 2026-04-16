package bloodmatch.interfaces.rest.shared;

import bloodmatch.domain.shared.valueObjects.DomainID;

import java.util.UUID;

public final class RequestValidationSupport {

  private RequestValidationSupport() {
  }

  public static DomainID parseDomainId(String value, String fieldName) {
    try {
      return new DomainID(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(fieldName + " must be a valid UUID");
    }
  }

  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
