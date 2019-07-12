package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RestrictedEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the RestrictedEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestrictedEntityRepository extends JpaRepository<RestrictedEntity, Long> {

}
