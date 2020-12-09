package me.skiincraft.discord.ousu.crawler;

public enum InputType {

    Mouse("Mouse", 1), Keyboard("Keyboard", 2), Tablet("Table", 3), Touch("Touchpad", 4);

    private final String name;
    private final int id;

    InputType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
