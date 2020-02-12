package com.excursions.places.service;

import com.excursions.places.exception.ServiceException;
import com.excursions.places.model.Place;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PlaceService {

    String PLACE_CACHE_NAME = "placeCache";
    String PLACES_CACHE_NAME = "placesCache";

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(
            put= { @CachePut(value= PLACE_CACHE_NAME, key= "#result.id") },
            evict= { @CacheEvict(value= PLACES_CACHE_NAME, allEntries= true) }
    )
    Place create(String name, String address, String info);

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(
            put= { @CachePut(value= PLACE_CACHE_NAME, key= "#result.id") },
            evict= { @CacheEvict(value= PLACES_CACHE_NAME, allEntries= true) }
    )
    Place update(Long id, String name, String address, String info);

    @Cacheable(value = PLACE_CACHE_NAME, key = "#id")
    Place findById(Long id);

    @Cacheable(value= PLACES_CACHE_NAME, unless= "#result.size() == 0")
    List<Place> findAll();

    List<Long> findAllIds();

    @Caching(
            evict= {
                    @CacheEvict(value= PLACE_CACHE_NAME, key= "#id"),
                    @CacheEvict(value= PLACES_CACHE_NAME, allEntries= true)
            }
    )
    void deleteById(Long id);

    LocalDateTime getLastChangeTime();
}
