package com.fileserver.app.works.network;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface RequestInterface {
    RequestSchema add(RequestSchema requestSchema);

    List<RequestSchema> getAllById(ArrayList<String> ids);

    RequestSchema findOne(String field, String value);
}
