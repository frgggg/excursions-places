package com.excursions.places.log.messages;

public class ServiceLogMessages {

    public static final String SERVICE_LOG_NEW_ENTITY = "New entity: {}";
    public static final String SERVICE_LOG_UPDATE_ENTITY = "Entity with id {} is updated. New = {}";
    public static final String SERVICE_LOG_DELETE_ENTITY = "Delete entity with id={}";

    public static final String SERVICE_LOG_GET_ENTITY = "Get entity with id={}";
    public static final String SERVICE_LOG_GET_ALL_ENTITIES = "Get all entities";
    public static final String SERVICE_LOG_GET_ALL_ENTITIES_IDS = "Get all entities ids";
    public static final String SERVICE_LOG_GET_NOT_EXIST_ENTITIES_IDS = "Get not exist entities ids from input list";

    public static final String SERVICE_LOG_SET_LAST_MODIFICATION_TIME = "set last modification time";
    public static final String SERVICE_LOG_GET_LAST_MODIFICATION_TIME = "get last modification time";

    public static final String SERVICE_LOG_EXCEPTION = "Exception in service {}. Info: {}";
}
