package builder;

/* 칼조네 피자 */
public class Calzone extends Pizza {

  private final boolean sauceInside;

  private Calzone(Builder builder) {
    super(builder);
    sauceInside = builder.sauceInside;
  }

  public static class Builder extends Pizza.Builder<Builder> {

    private boolean sauceInside = false; // 기본값

    public Builder sauceInside() {
      sauceInside = true;
      return this;
    }

    @Override
    public Calzone build() {
      return new Calzone(this);
    }

    @Override
    protected Builder self() {
      return this;
    }
  }

}
