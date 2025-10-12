
/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class BaseTestClass {
    private static final String PRIVATE_CONSTANT = "Base";
    protected static final String PROTECTED_CONSTANT = "Class";
    public static final String PUBLIC_CONSTANT = "Test";

    public String publicField;
    protected String protectedField;
    private String privateField;

    public BaseTestClass() { }

    public BaseTestClass(String param) { }

    private BaseTestClass(int param) { }

    public void publicMethod() {}

    protected void protectedMethod() {}

    private void privateMethod() {}
}
