package com.excursions.places.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.excursions.places.validation.message.ValidationMessagesComponents.STRING_FIELD_NOTNULL_MIN_MAX;
import static com.excursions.places.validation.message.ValidationMessagesComponents.STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE;

@Data
@Entity
@Table(
        name = "places",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "address"})}
        )
public class Place {

    public static final String PLACE_NAME_FIELD_NAME = "name";
    public static final int PLACE_NAME_LEN_MIN = 1;
    public static final int PLACE_NAME_LEN_MAX = 90;
    public static final String PLACE_NAME_VALIDATION_MESSAGE =
            PLACE_NAME_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + PLACE_NAME_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + PLACE_NAME_LEN_MAX;

    public static final String PLACE_ADDRESS_FIELD_NAME = "address";
    public static final int PLACE_ADDRESS_LEN_MIN = 1;
    public static final int PLACE_ADDRESS_LEN_MAX = 100;
    public static final String PLACE_ADDRESS_VALIDATION_MESSAGE =
            PLACE_ADDRESS_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + PLACE_ADDRESS_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + PLACE_ADDRESS_LEN_MAX;

    public static final String PLACE_INFO_FIELD_NAME = "info";
    public static final int PLACE_INFO_LEN_MIN = 1;
    public static final int PLACE_INFO_LEN_MAX = 200;
    public static final String PLACE_INFO_VALIDATION_MESSAGE =
            PLACE_INFO_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + PLACE_INFO_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + PLACE_INFO_LEN_MAX;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = PLACE_NAME_LEN_MAX, nullable = false)
    @NotNull(message = PLACE_NAME_VALIDATION_MESSAGE)
    @Size(min = PLACE_NAME_LEN_MIN, max = PLACE_NAME_LEN_MAX, message = PLACE_NAME_VALIDATION_MESSAGE)
    private String name;

    @Column(name = "address", length = PLACE_ADDRESS_LEN_MAX, nullable = false)
    @NotNull(message = PLACE_ADDRESS_VALIDATION_MESSAGE)
    @Size(min = PLACE_ADDRESS_LEN_MIN, max = PLACE_ADDRESS_LEN_MAX, message = PLACE_ADDRESS_VALIDATION_MESSAGE)
    private String address;

    @Column(name = "info", length = PLACE_INFO_LEN_MAX, nullable = false)
    @NotNull(message = PLACE_INFO_VALIDATION_MESSAGE)
    @Size(min = PLACE_INFO_LEN_MIN, max = PLACE_INFO_LEN_MAX, message = PLACE_INFO_VALIDATION_MESSAGE)
    private String info;

    protected Place() {}

    public Place(String name, String address, String info) {
        this.name = name;
        this.address = address;
        this.info = info;
    }
}
