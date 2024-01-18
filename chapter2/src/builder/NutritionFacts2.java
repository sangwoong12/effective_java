package builder;

/* 자바빈즈 패턴 - 일관성이 깨지고, 불변으로 만들 수 없다. */
public class NutritionFacts2 {

  private int servingSize = -1; // (ml, 1회 제공량)    필수
  private int servings = -1;    // (회, 총 n회 제공량)  필수
  private int calories = 0;     // (1회 제공량당)       선택
  private int fat = 0;          // (g/1회 제공량)      선택
  private int sodium = 0;       // (mg/1회 제공량)     선택
  private int carbohydrate = 0; // (g/1회 제공량)      선택

  public NutritionFacts2() {}

  public void setCalories(int calories) {
    this.calories = calories;
  }

  public void setCarbohydrate(int carbohydrate) {
    this.carbohydrate = carbohydrate;
  }

  public void setFat(int fat) {
    this.fat = fat;
  }

  public void setServings(int servings) {
    this.servings = servings;
  }

  public void setServingSize(int servingSize) {
    this.servingSize = servingSize;
  }

  public void setSodium(int sodium) {
    this.sodium = sodium;
  }

  /* 객체 하나를 만들려면 메서드를 여러 개 호출해야 하고, 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다. */
  public static void main(String[] args) {
    NutritionFacts2 cocaCola = new NutritionFacts2();
    cocaCola.setServingSize(240);
    cocaCola.setServings(8);
    cocaCola.setCalories(100);
    cocaCola.setSodium(35);
    cocaCola.setCarbohydrate(27);
  }
}
