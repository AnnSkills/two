package com.bsuir.annakhomyakova.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A VersionFileAnnKh.
 */
@Entity
@Table(name = "version_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class VersionFileAnnKh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "source_code")
    private byte[] sourceCode;

    @Column(name = "source_code_content_type")
    private String sourceCodeContentType;

    @Column(name = "creation_date")
    private Instant creationDate;

    @ManyToOne
    @JsonIgnoreProperties(value = { "versionFiles", "user" }, allowSetters = true)
    private FileAnnKh file;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VersionFileAnnKh id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public VersionFileAnnKh name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getSourceCode() {
        return this.sourceCode;
    }

    public VersionFileAnnKh sourceCode(byte[] sourceCode) {
        this.setSourceCode(sourceCode);
        return this;
    }

    public void setSourceCode(byte[] sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCodeContentType() {
        return this.sourceCodeContentType;
    }

    public VersionFileAnnKh sourceCodeContentType(String sourceCodeContentType) {
        this.sourceCodeContentType = sourceCodeContentType;
        return this;
    }

    public void setSourceCodeContentType(String sourceCodeContentType) {
        this.sourceCodeContentType = sourceCodeContentType;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    public VersionFileAnnKh creationDate(Instant creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public FileAnnKh getFile() {
        return this.file;
    }

    public void setFile(FileAnnKh file) {
        this.file = file;
    }

    public VersionFileAnnKh file(FileAnnKh file) {
        this.setFile(file);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VersionFileAnnKh)) {
            return false;
        }
        return id != null && id.equals(((VersionFileAnnKh) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VersionFileAnnKh{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", sourceCode='" + getSourceCode() + "'" +
            ", sourceCodeContentType='" + getSourceCodeContentType() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
