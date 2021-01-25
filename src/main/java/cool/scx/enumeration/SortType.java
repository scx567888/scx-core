package cool.scx.enumeration;

public enum SortType {
    ASC(" ASC "),
    DESC(" DESC ");

    private final String sort_str;

    SortType(String code) {
        this.sort_str = code;
    }

    @Override
    public String toString() {
        return this.sort_str;
    }
}
