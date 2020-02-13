package com.excursions.places.service.impl;

import com.excursions.places.model.Place;
import com.excursions.places.repository.PlaceRepository;
import com.excursions.places.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.excursions.places.exception.ServiceException.*;
import static com.excursions.places.log.messages.ServiceLogMessages.*;

@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    public static final String SERVICE_NAME = "PlaceServiceImpl";

    private PlaceRepository placeRepository;
    private EntityManager entityManager;

    private LocalDateTime lastModificationTime;

    @Autowired
    protected PlaceServiceImpl(PlaceRepository placeRepository, EntityManager entityManager) {
        this.placeRepository = placeRepository;
        this.entityManager = entityManager;

        setLastModificationTime();
    }

    @Override
    public Place create(String name, String address, String info) {
        Place placeForSave = new Place(name, address, info);
        Place savedPlace = saveUtil(placeForSave);

        log.debug(SERVICE_LOG_NEW_ENTITY, savedPlace);
        setLastModificationTime();
        return savedPlace;
    }

    @Override
    public Place update(Long id, String name, String address, String info) {
        Place placeForUpdate = findById(id);
        placeForUpdate.setName(name);
        placeForUpdate.setAddress(address);
        placeForUpdate.setInfo(info);
        Place updatedPlace = saveUtil(placeForUpdate);

        log.debug(SERVICE_LOG_UPDATE_ENTITY, id, updatedPlace);
        setLastModificationTime();
        return updatedPlace;
    }

    @Override
    public Place findById(Long id) {
        Optional<Place> optionalPlace = placeRepository.findById(id);
        if(!optionalPlace.isPresent()) {
            throw serviceExceptionNoEntityWithId(SERVICE_NAME, id);
        }

        log.debug(SERVICE_LOG_GET_ENTITY, id);
        return optionalPlace.get();
    }

    @Override
    public List<Place> findAll() {
        List<Place> places = new ArrayList<>();
        placeRepository.findAll().forEach(places::add);

        log.debug(SERVICE_LOG_GET_ALL_ENTITIES);
        return places;
    }

    @Override
    public void deleteById(Long id) {
        Place placeForDelete = findById(id);
        placeRepository.delete(placeForDelete);

        log.debug(SERVICE_LOG_DELETE_ENTITY, id);
        setLastModificationTime();
    }

    @Override
    public List<Long> findAllIds() {
        log.debug(SERVICE_LOG_GET_ALL_ENTITIES_IDS);
        return findAll().stream().map(Place::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck) {

        if(placesIdsForCheck == null) {
            throw serviceExceptionWrongIdsListForCheck(SERVICE_NAME);
        } else if(placesIdsForCheck.size() < 1) {
            throw serviceExceptionWrongIdsListForCheck(SERVICE_NAME);
        }

        List<Long> existPlacesIds = findAllIds();
        List<Long> notExistPlacesIds = new ArrayList<>();

        for(Long placeIdForCheck: placesIdsForCheck) {
            if(!existPlacesIds.contains(placeIdForCheck)) {
                notExistPlacesIds.add(placeIdForCheck);
            }
        }

        log.debug(SERVICE_LOG_GET_NOT_EXIST_PLACES_IDS);
        return notExistPlacesIds;
    }

    @Override
    public LocalDateTime getLastModificationTime() {
        log.debug(SERVICE_LOG_GET_LAST_MODIFICATION_TIME);
        return lastModificationTime;
    }

    private void setLastModificationTime() {
        lastModificationTime = LocalDateTime.now();
        log.debug(SERVICE_LOG_SET_LAST_MODIFICATION_TIME);
    }

    private Place saveUtil(Place placeForSave) {
        Place savedPlace;
        try {
            savedPlace = placeRepository.save(placeForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw serviceExceptionWrongEntity(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        } catch (PersistenceException e) {
            throw serviceExceptionExistEntity(SERVICE_NAME);
        }

        return savedPlace;
    }
}
