package com.hse.knopkabackend.models;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = "description")
public class Description {
    @Id
    @Column(
            name = "description_knopka_id",
            updatable = true
    )
    private Long knopkaWithThisKnopkaId;

    @Column(
            name = "description_text",
            nullable = true,
            columnDefinition = "VARCHAR(2048)"
    )
    private String text;

    @Column(
            name = "encoded_image",
            nullable = true
    )
    private byte[] encodedImage; //for now just one picture

    @ElementCollection
    @CollectionTable(
            name="tags",
            joinColumns=@JoinColumn(name="description_knopka_id")
    )
    @Column(name="tags")
    private List<String> tags;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "description_knopka_id")
    @JsonBackReference
    private Knopka knopka;


    public Description() {
    }

    public Long getKnopkaWithThisKnopkaId() {
        return knopkaWithThisKnopkaId;
    }

    public void setKnopkaWithThisKnopkaId(Long knopkaWithThisKnopkaId) {
        this.knopkaWithThisKnopkaId = knopkaWithThisKnopkaId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(byte[] encodedImage) {
        this.encodedImage = encodedImage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Knopka getKnopka() {
        return knopka;
    }

    public void setKnopka(Knopka knopka) {
        this.knopka = knopka;
    }
}
