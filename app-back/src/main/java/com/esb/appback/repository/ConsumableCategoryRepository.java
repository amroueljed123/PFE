package com.esb.appback.repository;

import com.esb.appback.model.ConsumableCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsumableCategoryRepository extends JpaRepository<ConsumableCategory, Long> {
    ConsumableCategory findByName(String name);
    List<ConsumableCategory> findByParentId(Long parentId);
}
