package com.example.impactfuldecisions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "criteria")
public class Criteria {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Double weight;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "decision_id")
    private Decision decision;

    @OneToMany(mappedBy = "criteria", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProCon> proConList;

    public Criteria() {
    }

    public Criteria(Long id, String name, Double weight, Decision decision) {
        this.id = id;
        this.name = name;
        this.weight = weight;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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
        return "Criteria{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", decision=" + decision +
                '}';
    }
}
