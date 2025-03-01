package webApp.models;

public class MaisonEnchere {

    private String name;
    private String description; // Qui sommes-nous
    private Coordonnees coordonnees;

    // Constructor
    public MaisonEnchere(String name, String description, Coordonnees coordonnees) {
        this.name = name;
        this.description = description;
        this.coordonnees = coordonnees;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Coordonnees getCoordonnees() {
        return coordonnees;
    }

    public void setCoordonnees(Coordonnees coordonnees) {
        this.coordonnees = coordonnees;
    }

    @Override
    public String toString() {
        return "MaisonEnchere{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coordonnees=" + coordonnees +
                '}';
    }
}
