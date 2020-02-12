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

import java.util.List;
import java.util.stream.Collectors;

import static com.excursions.places.log.messages.ControllerLogMessages.*;

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
    @ResponseBody
    public List<PlaceDto> findAll() {
        List<PlaceDto> allPlaces =  placeService.findAll()
                .stream()
                .map(book -> modelMapper.map(book, PlaceDto.class))
                .collect(Collectors.toList());
        log.debug(CONTROLLER_LOG_GET_ALL_ENTITIES);
        return allPlaces;
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public PlaceDto findById(@PathVariable("id") Long id) {
        Place place = placeService.findById(id);
        PlaceDto fundedPlace =  modelMapper.map(place, PlaceDto.class);
        log.debug(CONTROLLER_LOG_GET_ENTITY, id);
        return fundedPlace;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlaceDto create(@Validated @RequestBody PlaceDto placeDto) {
        Place place = placeService.create(
                placeDto.getName(),
                placeDto.getAddress(),
                placeDto.getInfo()
        );

        PlaceDto createPlace = modelMapper.map(place, PlaceDto.class);
        log.debug(CONTROLLER_LOG_NEW_ENTITY, createPlace);
        return createPlace;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlaceDto update(@Validated @RequestBody PlaceDto placeDto, @PathVariable("id") Long id) {
        Place place = placeService.update(
                id,
                placeDto.getName(),
                placeDto.getAddress(),
                placeDto.getInfo()
        );
        PlaceDto updatedPlace = modelMapper.map(place, PlaceDto.class);
        log.debug(CONTROLLER_LOG_UPDATE_ENTITY, id, updatedPlace);
        return updatedPlace;
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) {
        placeService.deleteById(id);
        log.debug(CONTROLLER_LOG_DELETE_ENTITY, id);
    }

    @GetMapping(value = "/id")
    @ResponseBody
    public List<Long> findAllIds() {
        log.debug(CONTROLLER_LOG_GET_ALL_ENTITIES_IDS);
        return placeService.findAllIds();
    }

    @GetMapping(value = "/id/check")
    @ResponseBody
    public List<Long> checkIds(@RequestBody List<Long> placesIdsForCheck) {
        log.debug(CONTROLLER_LOG_GET_NOT_EXIST_PLACES_IDS);
        return placeService.getNotExistPlacesIds(placesIdsForCheck);
    }

}
