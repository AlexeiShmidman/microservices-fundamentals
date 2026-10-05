package com.example.resourceservice.repository;

import com.example.resourceservice.entity.Resource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

  List<Resource> findAllByIdIn(List<Long> ids);
}

