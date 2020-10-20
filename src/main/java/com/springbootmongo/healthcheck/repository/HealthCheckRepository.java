package com.springbootmongo.healthcheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.springbootmongo.healthcheck.model.ServiceRegistry;

@Repository
public interface HealthCheckRepository extends MongoRepository<ServiceRegistry, String>{

}
