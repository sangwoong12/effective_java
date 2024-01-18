package builder;

import java.util.Objects;

/* 뉴욕 피자 */
public class NyPizza extends Pizza {

  private final Size size;

  private NyPizza(Builder builder) {
    super(builder);
    size = builder.size;
  }

  public enum Size {SMALL, MEDIUM, LARGE}

  public static class Builder extends Pizza.Builder<Builder> {

    private final Size size;

    public Builder(Size size) {
      this.size = Objects.requireNonNull(size);
    }

    @Override
    public NyPizza build() {
      return new NyPizza(this);
    }

    @Override
    protected Builder self() {
      return this;
    }
  }
}
