package companion_clazz;

import companion_clazz.TestType.TestTypeImpl;

public class TestMain {

  public static void main(String[] args) {
    /* 인터페이스에 package class 선언시 TestTypeImpl 가 노출됨 (public 이기 때문에) */
    TestType testType = TestType.createTestType();
    Class<TestTypeImpl> testTypeClass = TestTypeImpl.class;

    /* 동반 클래스 사용시 노출되지 않음 */
    TestType testTypes = TestTypes.createTestTypes();
    //TestTypes.TestTypeImpl.class;

  }
}
