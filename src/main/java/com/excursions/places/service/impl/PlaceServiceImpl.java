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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.*;
import java.util.stream.Collectors;

import static com.excursions.places.exception.message.PlaceServiceExceptionMessages.*;
import static com.excursions.places.log.message.PlaceServiceLogMessages.*;

@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private static final String SERVICE_NAME = "PlaceServiceImpl";

    private static final String PLACE_CACHE_NAME = "placeCache";
    private static final String PLACES_CACHE_NAME = "placesCache";

    private PlaceRepository placeRepository;
    private EntityManager entityManager;
    private PlaceServiceImpl self;

    @Lazy
    @Autowired
    protected PlaceServiceImpl(PlaceRepository placeRepository, EntityManager entityManager, PlaceServiceImpl self) {
        this.placeRepository = placeRepository;
        this.entityManager = entityManager;
        this.self = self;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(
            put= { @CachePut(value= PLACE_CACHE_NAME, key= "#result.id") },
            evict= { @CacheEvict(value= PLACES_CACHE_NAME, allEntries= true) }
    )
    @Override
    public Place create(String name, String address, String info) {
        Place savedPlace = saveUtil(null, name, address, info);
        log.debug(PLACE_SERVICE_LOG_NEW_PLACE, savedPlace);
        return savedPlace;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(
            put= { @CachePut(value= PLACE_CACHE_NAME, key= "#result.id") },
            evict= { @CacheEvict(value= PLACES_CACHE_NAME, allEntries= true) }
    )
    @Override
    public Place update(Long id, String name, String address, String info) {
        Place placeForUpdate = self.findById(id);
        Place updatedPlace = saveUtil(id, name, address, info);

        log.debug(PLACE_SERVICE_LOG_UPDATE_PLACE, placeForUpdate, updatedPlace);
        return updatedPlace;
    }

    @Cacheable(value = PLACE_CACHE_NAME, key = "#id")
    @Override
    public Place findById(Long id) {
        Optional<Place> optionalPlace = placeRepository.findById(id);
        if(!optionalPlace.isPresent()) {
            throw new ServiceException(SERVICE_NAME, String.format(PLACE_SERVICE_EXCEPTION_NOT_EXIST_PLACE, id));
        }

        Place findByIdPlace = optionalPlace.get();
        log.debug(PLACE_SERVICE_LOG_GET_PLACE, findByIdPlace);
        return findByIdPlace;
    }

    @Cacheable(value= PLACES_CACHE_NAME, unless= "#result.size() == 0")
    @Override
    public List<Place> findAll() {
        List<Place> places = new ArrayList<>();
        placeRepository.findAll().forEach(places::add);

        log.debug(PLACE_SERVICE_LOG_GET_ALL_PLACES);
        return places;
    }

    @Caching(
            evict= {
                    @CacheEvict(value= PLACE_CACHE_NAME, key= "#id"),
                    @CacheEvict(value= PLACES_CACHE_NAME, allEntries= true)
            }
    )
    @Override
    public void deleteById(Long id) {
        Place placeForDelete = self.findById(id);
        placeRepository.delete(placeForDelete);

        log.debug(PLACE_SERVICE_LOG_DELETE_PLACE, placeForDelete);
    }

    @Override
    public List<Long> findAllIds() {
        log.debug(PLACE_SERVICE_LOG_GET_ALL_PLACES_IDS);
        return self.findAll().stream().map(Place::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck) {

        if(placesIdsForCheck == null) {
            throw new ServiceException(SERVICE_NAME, PLACE_SERVICE_EXCEPTION_NULL_OR_EMPTY_PLACES_IDS_LIST_FOR_CHECK);
        } else if(placesIdsForCheck.size() < 1) {
            throw new ServiceException(SERVICE_NAME, PLACE_SERVICE_EXCEPTION_NULL_OR_EMPTY_PLACES_IDS_LIST_FOR_CHECK);
        }

        List<Long> existPlacesIds = findAllIds();
        HashSet<Long> notExistPlacesIds = new HashSet<>();

        for(Long placeIdForCheck: placesIdsForCheck) {
            if(!existPlacesIds.contains(placeIdForCheck)) {
                notExistPlacesIds.add(placeIdForCheck);
            }
        }

        log.debug(PLACE_SERVICE_LOG_GET_NOT_EXIST_PLACES_IDS);
        return new ArrayList<>(notExistPlacesIds);
    }

    private Place saveUtil(Long id, String name, String address, String info) {
        Place placeForSave = new Place(name, address, info);
        if(id != null) {
            placeForSave.setId(id);
        }

        Place savedPlace;
        try {
            savedPlace = placeRepository.save(placeForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        } catch (PersistenceException e) {
            throw new ServiceException(SERVICE_NAME, PLACE_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE);
        }

        return savedPlace;
    }
}
