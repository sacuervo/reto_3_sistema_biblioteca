package me.santiagocuervo;

public class User {

    private final String id;
    private final String nombre;

    public User(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

}
