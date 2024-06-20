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

    private String category;

    private String location;

    private int sumOfVotes;

    private int yesVotes;

    private int noVotes;

    public void addYesVote() {
        yesVotes = yesVotes + 1;
        sumOfVotes = sumOfVotes + 1;
    }

    public void noYesVote() {
        noVotes = noVotes + 1;
        sumOfVotes = sumOfVotes + 1;
    }
}
