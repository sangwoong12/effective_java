package companion_clazz;

public class TestTypes {

  // 인스턴스화 불가
  private TestTypes() {
  }

  // 정적 메서드
  public static TestType createTestTypes() {
    return new TestTypeImpl();
  }

  // package-private 클래스
  private static class TestTypeImpl implements TestType {

    @Override
    public void print() {
      System.out.println("print TestTypeImpl!!!");
    }
  }
}
