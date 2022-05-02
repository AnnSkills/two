package com.bsuir.annakhomyakova.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FileAnnKh.
 */
@Entity
@Table(name = "file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FileAnnKh implements Serializable {

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

    @OneToMany(mappedBy = "file")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "file" }, allowSetters = true)
    private Set<VersionFileAnnKh> versionFiles = new HashSet<>();

    @ManyToOne
    private UserAnnKh user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FileAnnKh id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public FileAnnKh name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getSourceCode() {
        return this.sourceCode;
    }

    public FileAnnKh sourceCode(byte[] sourceCode) {
        this.setSourceCode(sourceCode);
        return this;
    }

    public void setSourceCode(byte[] sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCodeContentType() {
        return this.sourceCodeContentType;
    }

    public FileAnnKh sourceCodeContentType(String sourceCodeContentType) {
        this.sourceCodeContentType = sourceCodeContentType;
        return this;
    }

    public void setSourceCodeContentType(String sourceCodeContentType) {
        this.sourceCodeContentType = sourceCodeContentType;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    public FileAnnKh creationDate(Instant creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Set<VersionFileAnnKh> getVersionFiles() {
        return this.versionFiles;
    }

    public void setVersionFiles(Set<VersionFileAnnKh> versionFiles) {
        if (this.versionFiles != null) {
            this.versionFiles.forEach(i -> i.setFile(null));
        }
        if (versionFiles != null) {
            versionFiles.forEach(i -> i.setFile(this));
        }
        this.versionFiles = versionFiles;
    }

    public FileAnnKh versionFiles(Set<VersionFileAnnKh> versionFiles) {
        this.setVersionFiles(versionFiles);
        return this;
    }

    public FileAnnKh addVersionFile(VersionFileAnnKh versionFile) {
        this.versionFiles.add(versionFile);
        versionFile.setFile(this);
        return this;
    }

    public FileAnnKh removeVersionFile(VersionFileAnnKh versionFile) {
        this.versionFiles.remove(versionFile);
        versionFile.setFile(null);
        return this;
    }

    public UserAnnKh getUser() {
        return this.user;
    }

    public void setUser(UserAnnKh user) {
        this.user = user;
    }

    public FileAnnKh user(UserAnnKh user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileAnnKh)) {
            return false;
        }
        return id != null && id.equals(((FileAnnKh) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileAnnKh{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", sourceCode='" + getSourceCode() + "'" +
            ", sourceCodeContentType='" + getSourceCodeContentType() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
