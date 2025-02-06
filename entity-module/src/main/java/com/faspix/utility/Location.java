package com.faspix.utility;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Location {

    private Double lat;

    private Double lon;

}
