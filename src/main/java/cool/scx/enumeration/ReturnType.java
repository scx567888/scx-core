package cool.scx.enumeration;

public enum ReturnType {
    JSON("JSON"),
    HTML("HTML"),
    FILE("FILE"),
    AUTO("AUTO");

    private final String return_type_str;

    ReturnType(String returnTypeStr) {
        this.return_type_str = returnTypeStr;
    }

    @Override
    public String toString() {
        return this.return_type_str;
    }
}
