package com.bsuir.annakhomyakova.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SupportAnnKh.
 */
@Entity
@Table(name = "support")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SupportAnnKh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "topic")
    private String topic;

    @NotNull
    @Pattern(regexp = "^(.+)@(\\\\S+)$")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Pattern(regexp = "^\\\\+(?:[0-9] ?){6,14}[0-9]$")
    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private UserAnnKh user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SupportAnnKh id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return this.topic;
    }

    public SupportAnnKh topic(String topic) {
        this.setTopic(topic);
        return this;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getEmail() {
        return this.email;
    }

    public SupportAnnKh email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public SupportAnnKh phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return this.description;
    }

    public SupportAnnKh description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserAnnKh getUser() {
        return this.user;
    }

    public void setUser(UserAnnKh user) {
        this.user = user;
    }

    public SupportAnnKh user(UserAnnKh user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupportAnnKh)) {
            return false;
        }
        return id != null && id.equals(((SupportAnnKh) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupportAnnKh{" +
            "id=" + getId() +
            ", topic='" + getTopic() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
