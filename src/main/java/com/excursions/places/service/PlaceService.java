package com.excursions.places.service;

import com.excursions.places.model.Place;
import java.util.List;

public interface PlaceService {

    Place create(String name, String address, String info);
    Place update(Long id, String name, String address, String info);

    Place findById(Long id);

    List<Place> findAll();

    void deleteById(Long id);

    List<Long> findAllIds();
    List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck);
}
