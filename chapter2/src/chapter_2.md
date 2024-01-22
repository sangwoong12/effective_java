# 객체 생성과 파괴

## 1. 생성자 대신 정적 팩터리 메서드를 고려하라.

클라이언트가 클래스의 인스턴스를 얻는 전통적인 수단은 public 생성자다.

클래스는 생성자와 별도로 정적 팩터리 메서드(static factory method)를 제공할 수 있다.

```
/* Java Boolean 객체의 valueOf 메서드 */ 
public static Boolean valueOf(boolean b) {
   return b ? Boolean.TRUE : Boolean.FALSE; // boolean -> Boolean 반환
}
```

클래스는 클라이언트에 public 생성자 대신 (혹은 생성자와 함께) 정적 팩터리 메서드를 제공할 수 있다.

### 장점

**첫번 째, 이름을 가질 수 있다.**

생성자에 넘기는 매개변수와 생성자 자체만으로는 반환될 객체의 특정을 제대로 설명하지 못한다. 반면 정적 팩터리는 이름만 잘 지으면 반환될 객체의 특성을 쉽게 묘사할 수 있다.

생성자인 ```Biginteger(int, int, Random)``` 보다 ```Biginteger.probablePrime()```가 '값이 소수인 BigInterger를
반환하라.' 라는 의미를 파악하기 쉽다.

**두번 째, 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.**

이 덕분에 불변 클래스는 인스턴스를 미리 만들어 놓거나 새로 생성한 인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.

대표적 예인 Boolean.valueOf(boolean) 메서드는 객체를 아예 생성하지 않는다. 따라서 생성 비용이 큰 객체가 자주 요청되는 상황이라면 성능을 상당히 끌어올려
준다. 또한 인스턴스 생성을
통제하여 [싱글턴](https://ko.wikipedia.org/wiki/%EC%8B%B1%EA%B8%80%ED%84%B4_%ED%8C%A8%ED%84%B4) 패턴 으로 만들
수도, 인스턴스화 불가로 만들 수 있다.

[플라이웨이트](https://ko.wikipedia.org/wiki/%ED%94%8C%EB%9D%BC%EC%9D%B4%EC%9B%A8%EC%9D%B4%ED%8A%B8_%ED%8C%A8%ED%84%B4)
패턴도 이와 비슷한 기법이라 할 수 있다.

**세번 째, 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.**

Java8 전에는 인터페이스에 정적 메서드를 선언할 수 없었다. 그렇기 때문에 이름이 "Type"인 인터페이스를 반환하는 정적 메서드가 필요하면, "Types"라는 (인스턴스화
불가인) 동반 클래스를 만들어 그안에 정의하는 것이 관례였다.

자바 컬렉션 프레임워크는 핵심 인터페이스들에 수정 불가나 동기화 등의 기능을 덧붙인 총 45개의 유틸리티 구현체를 제공하는데, 이 구현체 대부분을 단 하나의 인스턴스화 불가
클래스인 [java.util.Collections](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html) 에
정적 팩터리 메서드를 통해 얻도록 했다.

컬렉션 프레임워크는 이 45개 클래스를 공개하지 않기 때문에 API 외견을 훨씬 작게 만들 수 있었다. 또한 정적 팩터리 메서드를 사용하는 클라이언트는 얻은 객체를 인터페이스만으로
다루게 된다.(이는 일반적으로 좋은 습관)

> Q. Java8 이후로는 인터페이스도 정적 메서드를 가질 수 있는데 동반 클래스(Types)가 필요한가?
>
> A. 둘 이유가 없지만 정적 메서드를 구현하기 위한 코드 중 많은 부분은 여전히 별도의 package-private 클래스에 두어야 할 수 있다. 자바 9에서는 private 정적 메서드까지 허락하지만 정적 필드와 정적 맴버 클래스는 여전히 public 이어야 한다. [예시](./companion_clazz/TestMain.java)

**네번 째, 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.**

반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다. 심지어 다음 릴리스에서는 또 다른 클래스의 객체를 반환해도 된다.

예를 들어 EnumSet 클래스 원소가 64개 이하면 원소들을 long 변수 하나로 관리하는 RegularEnumSet의 인스턴스를, 65개 이상이면 long 배열로 관리하는
JunboRnumSet의 인스턴스를 반환한다.

```
/* EnumSet 메서드의 일부 */
public static <E extends Enum<E>> EnumSet<E> of(E e) {
    EnumSet<E> result = noneOf(e.getDeclaringClass());
    result.add(e);
    return result;
}

public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
    Enum<?>[] universe = getUniverse(elementType);
    if (universe == null)
        throw new ClassCastException(elementType + " not an enum");

    if (universe.length <= 64)
        return new RegularEnumSet<>(elementType, universe);
    else
        return new JumboEnumSet<>(elementType, universe);
}
```

사용자 입장에서 EnumSet의 내부 구조를 알 필요 없이 of()라는 정적 팩토리 메서드를 사용하면 된다.

**다섯 번째, 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.**

### 단점

**첫번 째, 상속을 하려면 public 이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다**

이 제약은 상속보다 컴포지션을 사용하도록 유도하고 불변 타입으로 만들려면 이 제약을 지켜야 한다는 점에서 오히려 장점으로 받아들일 수도 있다.

**두번 째, 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.**

생성자처럼 API 설명에 명확히 드러나지 않으니 사용자는 정적 팩터리 메서드 방식 클래스를 인스턴스화 방법을 알아내야 한다.

> 핵심 정리 : 정적 팩터리 메서드와 public 생성자는 각자의 쓰임새가 있으니 상대적인 장단점을 이해하고 사용하는 것이 좋다. 그렇다고 하더라도 정적 팩터리를 사용하는 게 유리한 경우가 더 많으므로 무작정 public 생성자를 제공하던 습관이 있다면 고치자.

<br/>

---
<br/>

## 2. 생성자에 매개변수가 많다면 빌더를 고려하라.

정적 팩터리와 생성자에는 똑같은 제약이 하나 있다. 선택적 매개변수가 많을 때 적절히 대응하기 어렵다.

필수 매개변수과 선택 매개변수이 존재할 때 선택 매개변수은 대부분 0으로 채워지게 되는데 이를 해결하기 위해 아래와 같은 방법이 있다.

### 점층적 생성자 패턴

[점층적 생성자 패턴](./builder/NutritionFacts.java) 방식은 필수 매개변수만 받는 생성자, 필수 매개변수와 선택 매개변수 1개를 받는 생성자, 선택
매개변수를 2개까지 받는 생성자, ... 형태로 선택 매개변수를 전부다 받는 생성자까지 늘려가는 방식이다.

```NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);```

위의 예시를 보면 사용자가 설정하길 원치 않는 매개변수까지 포함하게 되고, 어쩔 수 없이 그 값을 별도의 0을 설정해줘야 한다.

이러한 **점층적 생성자 패턴도 쓸 수는 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.**

### 자바빈즈 패턴

[자바빈즈 패턴](./builder/NutritionFacts2.java) 방식은 매개변수가 없는 생성자로 객체를 만든 후 세터 메서드들을 호출해 원하는 매개변수의 값을 설정하는
방식이다.

자바빈즈 패턴은 점층적 생성자의 단점을 해결하였지만, **객체 하나를 만들려면 메서드를 여러 개 호출해야 되고, 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게
된다.**

또한, 일관성이 무너지기 때문에 **자바빈즈 패턴에서는 클래스를 불변으로 만들 수 없으며** 스레드 안전성을 얻으려면 프로그래머가 추가 작업을 헤줘야 한다.

### 빌더 패턴

[빌더 패턴](./builder/NutritionFacts3.java)은 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정한다. 마지막으로 매개변수가 없는 build 메서드를
호출해 필요한 객체를 얻는 방식이다.

빌더 패턴 형식은 불변 클래스로 만들수 있고, 모든 매개변수의 기본값들을 한곳에 모아 둘 수 있다. 빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다.

### 빌더 패턴 활용

빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다. 각 계층의 클래스에 관련 빌더를 맴버로 정의하자. 추상 클래스는 추상 빌더를, 구체 클래스는 구체 빌더를 갖게 한다.

예를 들어. [피자](./builder/Pizza.java)라는 상위 추상 클래스가 추상 빌더를 가지도록 설계 하고, 하위 구체
클래스로 [뉴욕피자](./builder/NyPizza.java), [칼초네피자](./builder/Calzone.java) 가 있다고 가정하자.

이 경우 상위 추상 클래스가 정의한 반환 타입이 아닌, 그 하위 구체 클래스가 타입을 정의해 반환하는 기능을 공변 반환 타이핑이라고 하는데, 이 기능을 이용하면 클라이언트가
형변환에 신경 쓰지 않고도 빌더를 사용할 수 있다.

> 핵심 정리 : 생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다. 매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다.

<br/>

---
<br/>

## 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라.

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다. 싱글턴의 전형적인 예로는 함수와 같은 무상태 객체나 설계상 유일해야 하는 시스템 컴포넌트를 들 수 있다.
그런데 **클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트는 테스트하기가 어려워질 수 있다.** 타입을 인터페이스로 정의한 다음 그 인터페이스를 구현해서 만든 싱글턴이 아니라면
싱글턴 인스턴스를 가짜(mock) 구현으로 대체할 수 없기 때문이다. 싱글턴을 만드는 방식은 다음과 같다.

### final 필드 방식의 싱글턴

```java
/* public static final 필드 방식의 싱글턴 */
public class Elvis {

  // Elvis.INSTANCE 로 접근
  public static final Elvis INSTANCE = new Elvis();

  private Elvis() {
    /* 리플렉션 공격 방어 방법 */
    if (INSTANCE != null) {
      throw new AssertionError();
    }
  }

  public void leaveTheBuilding() {
    System.out.println("success");
  }
}
```

private 생성자는 public static final 필드인 Elvis.INSTANCE를 초기화할 때 딱 한 번만 호출된다. public, protected 생성자가 없으므로
Elvis 클래스가 초기화될 때 만들어진 인스턴스가 전체 시스템에서 하나뿐임이 보장된다.

하지만, 자바 리플렉션 ```AccessibleObject.setAccessible```을 이용해 private 생성자를 호출할 수 있다. 이러한 공격을 방어할려면 생성자를
수정하여 두 번째 객체가 생성되려 할 때 예외를 던지도록 설계해야한다.

### 정적 팩터리 방식의 싱글턴

```java
/* 정적 팩터리 방식의 싱글턴 */
public class Elvis {

  // Elvis.getInstance() 로 접근
  private static final Elvis INSTANCE = new Elvis();

  private Elvis() {
  }

  public static Elvis getInstance() {
    return INSTANCE;
  }

  public void leaveTheBuilding() {
    System.out.println("success");
  }
}
```

```Elvis.getInstance```는 항상 같은 객체의 참조를 반환하므로 제2의 Elvis 인스턴스란 결코 만들어지지 않는다. (이 방식도 리플렉션을 통한 예외는 똑같이
적용된다.)

이 방식의 장점은 다음과 같다.

1. API를 바꾸지않고도 싱글턴이 아니게 변경할 수 있다. 유일한 인스턴스를 반환하던 팩터리 메서드가 호출하는 스레드별로 다른 인스턴스를 넘겨주게 할 수 있다.
    - ```public static Elvis getInstance() { return new Elvis();}```
2. 원한다면 정적팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다.
3. 정적팩터리의 메서드 참조를 공급자(supplier)로 사용할 수 있다.
    - ```Supplier<Elvis> supplier = Elivis::getInstance;```

### final 필드, 정적 팩토리 방식의 싱글턴의 문제점

두 방식의 경우 클래스를 역직렬화할 때 새로운 인스턴스가 생성되어 싱글턴 속성을 위반하게 된다. 이를 해결하기 위해 모든 인스턴스 핃드를 일시적(transient) 선언하거나,
readResolve 메서드를 제공해야 한다.

```
// 싱글턴임을 보장해주는 readResolve 메서드 */
private Object readResolve() {
  // 진짜 'Elvis 를 반환하고, 가짜 Elvis는 가비지 컬렉터에 맡긴다.
  return INSTANCE;
}
```

### 열거 타입 방식의 싱글턴 - 바람직한 방법

```java
/* 열거 타입 방식의 싱글턴 */
public enum Elvis {
  INSTANCE;

  public void leaveTheBuilding() {
    System.out.println("success");
  }
}
```

public 필드 방식과 비슷하지만, 더 간결하고 **추가적인 노력없이 직렬화가 가능**(enum 특징)하고 또한, 아주 복잡한 직렬화 상황이나 리플렉션 공격에도 제2의 인스턴스가
생기는 일을 완벽히 막아준다.

조금 자연스러워 보일 수는 있으나 **인스턴스가 JVM 내에 하나만 존재한다는 것이 100% 보장 되므로, 대부분 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은
방법이다.**

<br/>

---
<br/>

## 4. 인스턴스화를 막으려거든 private 생성자를 사용하라

생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어준다. 즉, 매개변수를 받지 않은 public 생성자가 만들어지며, 사용자는 이 생성자가 자동 생성된 것인지 구분할
수 없다.

```java
public class UtilityClass {

  private UtilityClass() {
    throw new AssertionError();
  }
  //...
}
```

명시적 생성자가 private 로 설정하였기 때문에 클래스 바깥에서는 접근할 수 없다. 꼭 ```AssertionError```를 던질 필요는 없지만, 클래스 안에서 실수로라도
생성자를 호출하지 않도록 해준다.

<br/>

---

<br/>

## 5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

많은 클래스가 하나 이상의 자원에 의존한다. 예를 들어 맞춤법 검사기는 사전에 의존하는데, 이런 클래스를 정적 유틸리티 클래스로 구현한 모습을 드물지 않게 볼 수 있다.

```java
/* 정적 유틸리티를 잘못 사용한 예 - 유연하지 않고 테스트하기 어렵다. */
public class SpellChecker {

  private static final Lexion dictionary = ...;

  private SpellChecker() {
  }

  public static boolean isValid(String word) {
    //...
  }

  public static List<String> suggestions(String typo) {
    //...
  }
}
```

```java
/* 정적 유틸리티를 잘못 사용한 예 - 유연하지 않고 테스트하기 어렵다. */
public class SpellChecker {

  private final Lexion dictionary = ...;
  private static SpellChecker INSTANCE = new SpellChecker(...);

  private SpellChecker(...) {
  }

  public static boolean isValid(String word) {
    //...
  }

  public static List<String> suggestions(String typo) {
    //...
  }
}
```

두 방식 모두 사전을 단 하나만 사용한다고 가정한다는 점에서 그리 휼룡해 보이지 않다. 실전에서는 언어별, 특수 어휘용 사전을 별도로 두기도 한다.

간단히 final 한정자를 지우고 사전을 변경하는 메서드로 구현할 수 있지만, 이 방식은 어색하고 오류를 내기 쉬우며 멀티스레드 환경에서는 쓸 수 없다. **사용하는 자원에 따라
동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다**

```java
/* 의존 객체 주입 */
public class SpellChecker {

  private final Lexicon dictionary;

  // 생성자 주입
  public SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }

  public static boolean isValid(String word) {
    //...
  }

  public static List<String> suggestions(String typo) {
    //...
  }
}
```

이 방식으로 설계할 경우 dictionary라는 딱 하나의 자원만 사용하지만, 자원이 몇 개든 의존 관계가 어떻든 상관없이 잘 동작한다. 또한 불변을 보장하여 여러 클라이언트가
의존 객체들을 안심하고 공유할 수 있다.

> 핵심 정리 : 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다. 이 자원들을 클래스가 직접 만들게 해서도 안 된다. 대신 필요한 자원을 생성자에 넘겨주자. 의존 객체 주입이라 하는 이 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 기막히게 개선해준다.

<br/>

---

<br/>

## 6. 불필요한 객체 생성을 피해라

### 재사용

똑같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 나을 때가 많다. 재사용은 빠르고 세련되다. 특히 불변 객체는 언제든 재사용할 수 있다.

```
/* 정규표현식 예시 */
static boolean isRomanNumeral(String s) {
  record s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
   + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```

이 방식의 문제는 String.matches 메서드를 사용한다는 데 있다. **String.matches는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만, 성능이
중요한 상황에서 반복해 사용하기엔 적합하지 않다.** 이 메서드가 내부에서 만드는 정규표현식용 Pattern 인스턴스는, 한 번 쓰고 버려져서 곧바로 가비지 컬렉션 대상이 된다.
또한 유한 상태 머신을 만들기 때문에 인스턴스 생성 비용이 높다.

```java
/* 재사용성을 높인 코드 */
public class RomanNumerals {

  // 클래스 초기화 단계에 미리 캐싱
  private static final Pattern ROMAN = Pattern.compile(
      "^(?=.)M*(C[MD]|D?C{0,3})"
          + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

  // 이후 사용할 때 마다 재사용
  static boolean isRomanNumeral(String s) {
    return ROMAN.matcher(s).matches();
  }
}
```

하지만, 한번도 호출하지 않는다면 쓸대없이 초기화한 꼴이된다. 메서드가 처음 호출될 때 필드를 초기화하는 지연 초기화로 불필요한 초기화를 없앨 수는 있지만, 성능이 크게 개선되지
않을 때가 많기 때문에 권장하지 않는다.

### 오토박싱

오토박싱은 프로그래머가 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환해주는 기술이다. **오토박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을
흐려주지만, 완전히 없애주는 것은 아니다.**

```
private static long sum() {
   Long sum = 0L; // long 아닌 Long 으로 선언
   for (long l = 0; l <= Integer.MAX_VALUE; l++) {
      sum += l;
   }
   return sum;
}
```

sum 타입을 long 이 아닌 Long 으로 선언해서 불필요한 Long 인스턴스가 약 2^31 개가 생성되고 성능 또한 떨어지게 된다. 그렇기 떄문에 **박싱된 기본 타입보다는
기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의해야한다.**

<br/>

---

<br/>

## 7. 다 쓴 객체 참조를 해제하라

```java
/* 간단히 구현한 Stack */
public class Stack {

  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private Object[] elements;
  private int size = 0;

  public Stack() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY];
  }

  public void push(Object e) {
    ensureCapacity();
    elements[size++] = e;
  }

  // size 를 줄여 접근은 막았지만 elements[size] 는 존재함.
  public Object pop() {
    if (size == 0) {
      throw new EmptyStackException();
    }
    return elements[--size];
  }

  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}
```

객체 참조 하나를 살려두면 가비지 컬렉터는 그 객체뿐 아니라 그 객체가 참조하는 모든 객체를 회수해가지 못한다. 위 코드를 보게되면 pop 메서드는 해당 참조를 지우는 것이 아닌
사이즈를 줄이고 있다. (여전히 elements[size] 는 참조 대상)

### null 처리

```
/* 제대로 구현한 pop 메서드 */
public Object pop() {
  if (size == 0) {
    throw new EmptyStackException();
  }
  Object result = elements[--size];
  elements[size] = null; // 다 쓴 참조 해제
  retrun result;
}
```

위와 같이 반환 후 더 이상 사용되지 않을 객체에 대해서 null을 대입함으로써 메모리 누수가 없는 안전한 코드를 완성할 수 있다.

이렇게 함으로써 얻을 수 있는 부가적인 장점은 누군가가 악의적으로 혹은 실수로 해당 객체를 다시 사용하려 했을 때 NullPointException이 발생하며 사전에 에러를
발생시킬 수 있다는 점이다.

하지만 이렇게 일일히 null을 대입하는 것은 프로그램을 필요 이상으로 지저분하게 만들 수 있다.

이러한 방법을 사용하는 경우는 위 예제 코드처럼 자기 메모리를 직접 관리하는 경우이다. 예를 들어 Stack은 특정 메모리를 사전에 할당받고 활성영역과 비활성영역을 size라는
기준을 통해서 나눈다. 하지만 이 비활성 영역이 더이상 사용되지 않는다는 것은 개발한 프로그래머만이 아는 사실이다. 따라서 이러한 경우 null을 통해 가비지 컬렉션에게 해당
객체가 더이상 사용되지 않는다고 알려야한다.

### 유효 범위

다 쓴 참조를 해제하는 가장 좋은 방법은 해당 참조를 담은 변수를 유효 범위(scope) 밖으로 밀어내는 것이다. 즉, 최대한 작은 범위를 가지는 지역변수를 사용해 해당 범위에
대한 코드 실행이 끝나면 자연스럽게 참조는 해제되고 해당 객체는 가비지 컬렉션의 대상이 된다

> 핵심 정리 : 이외에도 캐시, 리스너, 콜백함수와 같은 것들이 메모리 누수를 발생시킬 수 있다. 따라서 사용되지 않는 객체가 있다면 프로그래밍 단계에서 반드시 해제를 해주는 등의 조치를 해야 한다.
>
> 메모리 누수는 겉으로 잘 드러나지 않아 시스템에 수년간 잠복하는 사례도 있다. 이러한 메모리 누수는 철저한 코드 리뷰와 힙 프로파일러 같은 디버깅 도구를 동원해야 발견할 수 있다. 따라서 이와같은 예방법을 익혀두는 것이 매우 중요하다.

<br/>

---

<br/>

