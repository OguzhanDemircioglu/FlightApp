package com.casestudy.repository;

import com.casestudy.entity.ServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceLogRepository extends JpaRepository<ServiceLog, Long> {
}
