package com.example.impactfuldecisions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "procons")
public class ProCon {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

    @Column
    private Double rating;

    @Column
    private String description;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "option_id")
    private Option option;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "criteria_id")
    private Criteria criteria;

    public ProCon() {
    }

    public ProCon(Long id, String type, Double rating, String description, Option option, Criteria criteria) {
        this.id = id;
        this.type = type;
        this.rating = rating;
        this.description = description;
        this.option = option;
        this.criteria = criteria;
    }

    public Long getCriteriaId() {
        return (criteria != null) ? criteria.getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type.toLowerCase();
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "ProCon{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", option=" + option +
                ", criteria=" + criteria +
                '}';
    }
}
