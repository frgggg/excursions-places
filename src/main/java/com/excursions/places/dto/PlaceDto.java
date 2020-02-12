package com.excursions.places.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.excursions.places.model.Place.*;

@Data
@Builder
@AllArgsConstructor
public class PlaceDto {

    private Long id;

    @NotNull(message = PLACE_NAME_VALIDATION_MESSAGE)
    @Size(min = PLACE_NAME_LEN_MIN, max = PLACE_NAME_LEN_MAX, message = PLACE_NAME_VALIDATION_MESSAGE)
    private String name;


    @NotNull(message = PLACE_ADDRESS_VALIDATION_MESSAGE)
    @Size(min = PLACE_ADDRESS_LEN_MIN, max = PLACE_ADDRESS_LEN_MAX, message = PLACE_ADDRESS_VALIDATION_MESSAGE)
    private String address;


    @NotNull(message = PLACE_INFO_VALIDATION_MESSAGE)
    @Size(min = PLACE_INFO_LEN_MIN, max = PLACE_INFO_LEN_MAX, message = PLACE_INFO_VALIDATION_MESSAGE)
    private String info;

    public PlaceDto() {}
}
