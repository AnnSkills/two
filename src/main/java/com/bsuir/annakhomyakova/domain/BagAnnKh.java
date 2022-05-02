package com.bsuir.annakhomyakova.domain;

import com.bsuir.annakhomyakova.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BagAnnKh.
 */
@Entity
@Table(name = "bag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BagAnnKh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "bag_name", nullable = false)
    private String bagName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @OneToMany(mappedBy = "bag")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "bag" }, allowSetters = true)
    private Set<TaskAnnKh> tasks = new HashSet<>();

    @ManyToOne
    private UserAnnKh user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BagAnnKh id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBagName() {
        return this.bagName;
    }

    public BagAnnKh bagName(String bagName) {
        this.setBagName(bagName);
        return this;
    }

    public void setBagName(String bagName) {
        this.bagName = bagName;
    }

    public String getDescription() {
        return this.description;
    }

    public BagAnnKh description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public BagAnnKh status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<TaskAnnKh> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<TaskAnnKh> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setBag(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setBag(this));
        }
        this.tasks = tasks;
    }

    public BagAnnKh tasks(Set<TaskAnnKh> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public BagAnnKh addTask(TaskAnnKh task) {
        this.tasks.add(task);
        task.setBag(this);
        return this;
    }

    public BagAnnKh removeTask(TaskAnnKh task) {
        this.tasks.remove(task);
        task.setBag(null);
        return this;
    }

    public UserAnnKh getUser() {
        return this.user;
    }

    public void setUser(UserAnnKh user) {
        this.user = user;
    }

    public BagAnnKh user(UserAnnKh user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BagAnnKh)) {
            return false;
        }
        return id != null && id.equals(((BagAnnKh) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BagAnnKh{" +
            "id=" + getId() +
            ", bagName='" + getBagName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
