package com.bsuir.annakhomyakova.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RulesAnnKh.
 */
@Entity
@Table(name = "rules")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RulesAnnKh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Lob
    @Column(name = "requirements")
    private byte[] requirements;

    @Column(name = "requirements_content_type")
    private String requirementsContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RulesAnnKh id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public RulesAnnKh name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public RulesAnnKh code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getRequirements() {
        return this.requirements;
    }

    public RulesAnnKh requirements(byte[] requirements) {
        this.setRequirements(requirements);
        return this;
    }

    public void setRequirements(byte[] requirements) {
        this.requirements = requirements;
    }

    public String getRequirementsContentType() {
        return this.requirementsContentType;
    }

    public RulesAnnKh requirementsContentType(String requirementsContentType) {
        this.requirementsContentType = requirementsContentType;
        return this;
    }

    public void setRequirementsContentType(String requirementsContentType) {
        this.requirementsContentType = requirementsContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RulesAnnKh)) {
            return false;
        }
        return id != null && id.equals(((RulesAnnKh) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RulesAnnKh{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", requirements='" + getRequirements() + "'" +
            ", requirementsContentType='" + getRequirementsContentType() + "'" +
            "}";
    }
}
