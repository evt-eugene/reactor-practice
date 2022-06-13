package standalone;

public final class Greeting {

  private final Long userId;
  private final String message;

  public Greeting(Long userId, String message) {
    this.userId = userId;
    this.message = message;
  }

  public Long getUserId() {
    return userId;
  }

  public String getMessage() {
    return this.message;
  }

  @Override
  public String toString() {
    return "Greeting{" +
               "'userId'=" + userId + ", " +
               "'message'='" + message
               + "'}";
  }
}
