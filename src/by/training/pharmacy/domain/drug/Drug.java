package by.training.pharmacy.domain.drug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vladislav on 15.06.16.
 */
public class Drug {
    private int id;
    private String name;
    private byte[] drugImage;
    private String description;
    private float price;
    private boolean prescriptionEnable;
    private boolean inStock;
    private DrugType type;
    private String activeSubstance;
    private List<Integer> dosages = new ArrayList<>();
    private DrugManufacturer drugManufacturer;
    private DrugClass drugClass;

    @Override
    public String toString() {
        return "Drug{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", drugImage=" + Arrays.toString(drugImage) +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", prescriptionEnable=" + prescriptionEnable +
                ", inStock=" + inStock +
                ", type=" + type +
                ", activeSubstance='" + activeSubstance + '\'' +
                ", dosages=" + dosages +
                ", drugManufacturer=" + drugManufacturer +
                ", drugClass=" + drugClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drug drug = (Drug) o;

        if (id != drug.id) return false;
        if (Float.compare(drug.price, price) != 0) return false;
        if (prescriptionEnable != drug.prescriptionEnable) return false;
        if (inStock != drug.inStock) return false;
        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        if (!Arrays.equals(drugImage, drug.drugImage)) return false;
        if (description != null ? !description.equals(drug.description) : drug.description != null) return false;
        if (type != drug.type) return false;
        if (activeSubstance != null ? !activeSubstance.equals(drug.activeSubstance) : drug.activeSubstance != null)
            return false;
        if (dosages != null ? !dosages.equals(drug.dosages) : drug.dosages != null) return false;
        if (drugManufacturer != null ? !drugManufacturer.equals(drug.drugManufacturer) : drug.drugManufacturer != null)
            return false;
        return drugClass != null ? drugClass.equals(drug.drugClass) : drug.drugClass == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(drugImage);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + (prescriptionEnable ? 1 : 0);
        result = 31 * result + (inStock ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (activeSubstance != null ? activeSubstance.hashCode() : 0);
        result = 31 * result + (dosages != null ? dosages.hashCode() : 0);
        result = 31 * result + (drugManufacturer != null ? drugManufacturer.hashCode() : 0);
        result = 31 * result + (drugClass != null ? drugClass.hashCode() : 0);
        return result;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getDrugImage() {
        return drugImage;
    }

    public void setDrugImage(byte[] drugImage) {
        this.drugImage = drugImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isPrescriptionEnable() {
        return prescriptionEnable;
    }

    public void setPrescriptionEnable(boolean prescriptionEnable) {
        this.prescriptionEnable = prescriptionEnable;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public DrugType getType() {
        return type;
    }

    public void setType(DrugType type) {
        this.type = type;
    }

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public List<Integer> getDosages() {
        return dosages;
    }

    public void setDosages(List<Integer> dosages) {
        this.dosages = dosages;
    }

    public DrugManufacturer getDrugManufacturer() {
        return drugManufacturer;
    }

    public void setDrugManufacturer(DrugManufacturer drugManufacturer) {
        this.drugManufacturer = drugManufacturer;
    }

    public DrugClass getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(DrugClass drugClass) {
        this.drugClass = drugClass;
    }
}