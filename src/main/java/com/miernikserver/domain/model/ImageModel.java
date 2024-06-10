package com.miernikserver.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "images")
@NoArgsConstructor
@Getter
@Setter
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @CollectionTable
    @ElementCollection
    private List<Integer> votes = new ArrayList<>();
}
