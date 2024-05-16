package at.aau.serg.websocketdemoapp.helper;

public enum CardType {
    GAIA("gaia", ""),
    GOLDEN_SICKLE("golden_sickle", ""),
    MISTLETOE("mistletoe", ""),
    GREEN("green", "green"),
    YELLOW("yellow", "yellow"),
    RED("red", "red"),
    BLUE("blue", "blue"),
    PURPLE("purple", "purple");

    private final String name;
    private final String color;

    CardType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    String getName() {
        return name;
    }

    String getColor() {
        return color;
    }

}