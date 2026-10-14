package fr.pandamcbe.mGConstructorAPI.arenas;

public enum Modes {
    SINGLE("Single"), DOUBLE("Double");

    private final String string;
    Modes(String single) {
        this.string = single;
    }

    public String getString() {
        return string;
    }
}
