package com.example.impactfuldecisions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "decision_id")
    private Decision decision;

    @OneToMany(mappedBy = "option", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProCon> proConList;


    public Option() {
    }

    public Option(Long id, String name, Decision decision) {
        this.id = id;
        this.name = name;
        this.decision = decision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public List<ProCon> getProConList() {
        return proConList;
    }

    public void setProConList(List<ProCon> proConList) {
        this.proConList = proConList;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", decision=" + decision +
                '}';
    }
}
