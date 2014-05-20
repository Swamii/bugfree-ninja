package ad.enums;

/**
 * Created by swami on 14/05/14.
 */
public enum Color {

    VARIOUS("various"),
    BLACK("black"),
    BROWN("brown"),
    PURPLE("purple"),
    GREEN("green"),
    KHAKI("khaki"),
    BLUE("blue"),
    TURQUOISE("turquoise"),
    RED("red"),
    PINK("pink"),
    ORANGE("orange"),
    GOLD("gold"),
    YELLOW("yellow"),
    GREY("grey"),
    BEIGE("beige"),
    SILVER("silver"),
    WHITE("white");

    private String name;

    private Color(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String n = super.toString();
        return n.substring(0, 1).toUpperCase() +
               n.substring(1).toLowerCase();
    }
}
