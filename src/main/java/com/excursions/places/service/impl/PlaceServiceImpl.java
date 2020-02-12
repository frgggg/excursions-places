package com.excursions.places.service.impl;

import com.excursions.places.exception.ServiceException;
import com.excursions.places.model.Place;
import com.excursions.places.repository.PlaceRepository;
import com.excursions.places.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    private LocalDateTime lastChangeTime;

    @Autowired
    protected PlaceServiceImpl(PlaceRepository placeRepository, EntityManager entityManager) {
        this.placeRepository = placeRepository;
        this.entityManager = entityManager;
        setLastChangeTimeToNow();
    }

    @Override
    public Place create(String name, String address, String info) {
        Place placeForSave = new Place(name, address, info);
        Place savedPlace = saveUtil(placeForSave);

        setLastChangeTimeToNow();

        log.debug(SERVICE_LOG_NEW_ENTITY, savedPlace);
        return savedPlace;
    }

    @Override
    public Place update(Long id, String name, String address, String info) {
        Place placeForUpdate = findById(id);
        placeForUpdate.setName(name);
        placeForUpdate.setAddress(address);
        placeForUpdate.setInfo(info);
        Place updatedPlace = saveUtil(placeForUpdate);

        setLastChangeTimeToNow();

        log.debug(SERVICE_LOG_UPDATE_ENTITY, id, updatedPlace);
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
    public List<Long> findAllIds() {
        return findAll().stream().map(Place::getId).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        Place placeForDelete = findById(id);
        placeRepository.delete(placeForDelete);
        setLastChangeTimeToNow();

        log.debug(SERVICE_LOG_DELETE_ENTITY, id);
    }

    @Override
    public LocalDateTime getLastChangeTime() {
        log.debug(SERVICE_LOG_GET_CHANGE_TIME, lastChangeTime);
        return lastChangeTime;
    }

    private void setLastChangeTimeToNow() {
        lastChangeTime = LocalDateTime.now();
        log.debug(SERVICE_LOG_CHANGE, lastChangeTime);
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
