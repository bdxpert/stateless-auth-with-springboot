package com.stateless.service.services;

import com.stateless.service.entities.SearchData;

import java.util.List;

public interface SearchService {

	List<SearchData> find(String queryStr) throws Exception;

}
