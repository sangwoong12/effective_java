# 객체 생성과 파괴

## 생성자 대신 정적 팩터리 메서드를 고려하라.

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

1. 이름을 가질 수 있다.

생성자에 넘기는 매개변수와 생성자 자체만으로는 반환될 객체의 특정을 제대로 설명하지 못한다. 반면 정적 팩터리는 이름만 잘 지으면 반환될 객체의 특성을 쉽게 묘사할 수 있다.

생성자인 ```Biginteger(int, int, Random)``` 보다 ```Biginteger.probablePrime()```가 '값이 소수인 BigInterger를
반환하라.' 라는 의미를 파악하기 쉽다.

2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.

이 덕분에 불변 클래스는 인스턴스를 미리 만들어 놓거나 새로 생성한 인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.

대표적 예인 Boolean.valueOf(boolean) 메서드는 객체를 아예 생성하지 않는다. 따라서 생성 비용이 큰 객체가 자주 요청되는 상황이라면 성능을 상당히 끌어올려
준다. 또한 인스턴스 생성을
통제하여 [싱글턴](https://ko.wikipedia.org/wiki/%EC%8B%B1%EA%B8%80%ED%84%B4_%ED%8C%A8%ED%84%B4) 패턴 으로 만들
수도, 인스턴스화 불가로 만들 수 있다.

[플라이웨이트](https://ko.wikipedia.org/wiki/%ED%94%8C%EB%9D%BC%EC%9D%B4%EC%9B%A8%EC%9D%B4%ED%8A%B8_%ED%8C%A8%ED%84%B4)
패턴도 이와 비슷한 기법이라 할 수 있다.

3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

Java8 전에는 인터페이스에 정적 메서드를 선언할 수 없었다. 그렇기 때문에 이름이 "Type"인 인터페이스를 반환하는 정적 메서드가 필요하면, "Types"라는 (인스턴스화
불가인) 동반 클래스를 만들어 그안에 정의하는 것이 관례였다.

자바 컬렉션 프레임워크는 핵심 인터페이스들에 수정 불가나 동기화 등의 기능을 덧붙인 총 45개의 유틸리티 구현체를 제공하는데, 이 구현체 대부분을 단 하나의 인스턴스화 불가
클래스인 [java.util.Collections](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html) 에
정적 팩터리 메서드를 통해 얻도록 했다.

컬렉션 프레임워크는 이 45개 클래스를 공개하지 않기 때문에 API 외견을 훨씬 작게 만들 수 있었다. 또한 정적 팩터리 메서드를 사용하는 클라이언트는 얻은 객체를 인터페이스만으로
다루게 된다.(이는 일반적으로 좋은 습관)

> Q. Java8 이후로는 인터페이스도 정적 메서드를 가질 수 있는데 동반 클래스(Types)가 필요한가?
>
> A. 둘 이유가 없지만 정적 메서드를 구현하기 위한 코드 중 많은 부분은 여전히 별도의 package-private 클래스에 두어야 할 수 있다. 자바 9에서는 private 정적 메서드까지 허락하지만 정적 필드와 정적 맴버 클래스는 여전히 public 이어야 한다. [예시](/companion_clazz)

4. 입력 매개변수에 따라 매번 다른 클래스의객체를 반환할 수 있다.

반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다. 심지어 다음 릴리스에서는 또 다른 클래스의 객체를 반환해도 된다.

가령 EnumSet 클래스는 public 생성자 없이 오직 정적 팩터리만 제공하는데, OpenJDK에서는 우너소의 수에 따라 두 가지 하위 클래스 중 하나의 인스턴스를 반환한다.