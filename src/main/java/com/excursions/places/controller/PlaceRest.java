package com.excursions.places.controller;

import com.excursions.places.dto.PlaceDto;
import com.excursions.places.model.Place;
import com.excursions.places.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.excursions.places.log.message.PlaceControllerLogMessages.*;

@Slf4j
@RestController
@RequestMapping("/place")
public class PlaceRest {

    private PlaceService placeService;
    private ModelMapper modelMapper;

    @Autowired
    protected PlaceRest(PlaceService placeService, ModelMapper modelMapper) {
        this.placeService = placeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PlaceDto> findAll() {
        List<Place> places =  placeService.findAll();
        List<PlaceDto> placeDtos = null;
        if(places != null) {
            if(places.size() > 0) {
                placeDtos = places
                        .stream()
                        .map(book -> modelMapper.map(book, PlaceDto.class))
                        .collect(Collectors.toList());
            }
        }

        if(placeDtos == null) {
            placeDtos = new ArrayList<>();
        }
        log.debug(PLACE_CONTROLLER_LOG_GET_ALL_PLACES);
        return placeDtos;
    }

    @GetMapping(value = "/{id}")
    public PlaceDto findById(@PathVariable("id") Long id) {
        Place place = placeService.findById(id);
        PlaceDto fundedPlace =  modelMapper.map(place, PlaceDto.class);
        log.debug(PLACE_CONTROLLER_LOG_GET_PLACE, id);
        return fundedPlace;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceDto create(@Validated @RequestBody PlaceDto placeDto) {
        Place place = placeService.create(
                placeDto.getName(),
                placeDto.getAddress(),
                placeDto.getInfo()
        );

        PlaceDto createPlace = modelMapper.map(place, PlaceDto.class);
        log.debug(PLACE_CONTROLLER_LOG_NEW_PLACE, createPlace);
        return createPlace;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlaceDto update(@Validated @RequestBody PlaceDto placeDto, @PathVariable("id") Long id) {
        Place place = placeService.update(
                id,
                placeDto.getName(),
                placeDto.getAddress(),
                placeDto.getInfo()
        );
        PlaceDto updatedPlace = modelMapper.map(place, PlaceDto.class);
        log.debug(PLACE_CONTROLLER_LOG_UPDATE_PLACE, id, updatedPlace);
        return updatedPlace;
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) {
        placeService.deleteById(id);
        log.debug(PLACE_CONTROLLER_LOG_DELETE_PLACE, id);
    }

    @GetMapping(value = "/id")
    public List<Long> findAllIds() {
        log.debug(PLACE_CONTROLLER_LOG_GET_ALL_PLACES_IDS);
        return placeService.findAllIds();
    }

    @GetMapping(value = "/check")
    public List<Long> getNotExistPlacesIds(@RequestParam(name = "places-ids-for-check") List<Long> placesIdsForCheck) {
        log.debug(PLACE_CONTROLLER_LOG_GET_NOT_EXIST_PLACES_IDS);
        return placeService.getNotExistPlacesIds(placesIdsForCheck);
    }

}
