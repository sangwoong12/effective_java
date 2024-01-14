package companion_clazz;

public interface TestType {
  // public 만 허용
  public String A = "a";

  /* 여기서는 'private'가 허용되지 않음. */
  //private String B = "b";

  void print();

  static TestType createTestType() {
    return new TestTypeImpl();
  }

  /* private 불가능 */
  //private class TestTypeImpl implements TestType {
  class TestTypeImpl implements TestType {
    private TestTypeImpl(){}

    @Override
    public void print() {
      System.out.println("print TestTypeImpl!!!!");
    }
  }
}
