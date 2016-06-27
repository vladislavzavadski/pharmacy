package by.training.pharmacy.domain.drug;

/**
 * Created by vladislav on 18.06.16.
 */
public class DrugClass {
    private String name;
    private String description;

    @Override
    public String toString() {
        return "DrugClass{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrugClass drugClass = (DrugClass) o;

        if (name != null ? !name.equals(drugClass.name) : drugClass.name != null) return false;
        return description != null ? description.equals(drugClass.description) : drugClass.description == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

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
}
