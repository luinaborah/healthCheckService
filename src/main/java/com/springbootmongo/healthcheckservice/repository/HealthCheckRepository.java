package com.springbootmongo.healthcheckservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.springbootmongo.healthcheckservice.model.ServiceRegistry;

@Repository
public interface HealthCheckRepository extends MongoRepository<ServiceRegistry, String>{

}
