package builder;

/* 빌더 패턴 - 점층적 생성자 패턴과 자바빈즈 패턴의 장점만 취했다. */
public class NutritionFacts3 {

  private final int servingSize; // (ml, 1회 제공량)    필수
  private final int servings;    // (회, 총 n회 제공량)  필수
  private final int calories;    // (1회 제공량당)       선택
  private final int fat;         // (g/1회 제공량)      선택
  private final int sodium;      // (mg/1회 제공량)     선택
  private final int carbohydrate;// (g/1회 제공량)      선택

  private NutritionFacts3(Builder builder) {
    this.servingSize = builder.servingSize;
    this.servings = builder.servings;
    this.calories = builder.calories;
    this.fat = builder.fat;
    this.sodium = builder.sodium;
    this.carbohydrate = builder.carbohydrate;
  }

  public static class Builder {

    // 필수 매개변수
    private final int servingSize;
    private final int servings;

    // 선택 매개변수 - 기본값으로 초기화한다.
    private int calories = 0;
    private int fat = 0;
    private int sodium = 0;
    private int carbohydrate = 0;

    public Builder(int servingSize, int servings) {
      this.servingSize = servingSize;
      this.servings = servings;
    }

    public Builder calories(int val) {
      calories = val;
      return this;
    }

    public Builder fat(int val) {
      fat = val;
      return this;
    }

    public Builder sodium(int val) {
      sodium = val;
      return this;
    }

    public Builder carbohydrate(int val) {
      carbohydrate = val;
      return this;
    }

    public NutritionFacts3 build() {
      return new NutritionFacts3(this);
    }
  }
  /* 명명된 선택적 매개변수를 흉내 낸 것이다. */
  public static void main(String[] args) {
    NutritionFacts3 cocaCola = new Builder(240, 8)
        .calories(100)
        .sodium(35)
        .carbohydrate(27)
        .build();
  }
}
