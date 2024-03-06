package emsi.iir4.pathogene.service.dto;

import java.io.Serializable;

public class MaladieInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nom;
    private String modeleFileName;
    private Long width;
    private Long height;

    private Double normalizationValue;

    // Default constructo

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getModeleFileName() {
        return modeleFileName;
    }

    public void setModeleFileName(String modeleFileName) {
        this.modeleFileName = modeleFileName;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Double getNormalizationValue() {
        return normalizationValue;
    }

    public void setNormalizationValue(Double normalizationValue) {
        this.normalizationValue = normalizationValue;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return (
            "MaladieInfoDTO{" +
            ", nom='" +
            nom +
            '\'' +
            ", modeleFileName='" +
            modeleFileName +
            '\'' +
            ", width=" +
            width +
            ", height=" +
            height +
            // Add toString representation for additional fields
            '}'
        );
    }
}
