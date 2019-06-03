package com.fileserver.app.works.settings;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanInterface {
    public PlanSchema add(PlanSchema planSchema);

    public PlanSchema findOne(String field, String value);

    public List<PlanSchema> findAll();
}
