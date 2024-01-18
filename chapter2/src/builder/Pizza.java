package builder;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/* 계층적으로 설계된 클래스와 잘 어울리는 빌더 패턴 */
public abstract class Pizza {

  final Set<Topping> toppings;

  Pizza(Builder<?> builder) {
    toppings = builder.toppings.clone();
  }

  public enum Topping {
    HAM, MUSHROOM, ONION, PEPPER, SAUSAGE
  }

  abstract static class Builder<T extends Builder<T>> {

    EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

    public T addTopping(Topping topping) {
      toppings.add(Objects.requireNonNull(topping));
      return self();
    }

    abstract Pizza build();

    // 하위 클래스는 이 메더르르 재정의하여 "this"를 반환하도록 해야 한다.
    protected abstract T self();
  }
}
