package com.example.company.service;

import com.example.company.model.search.SearchRequest;
import java.util.List;

public interface SearchService {

    <T> List<T> search(SearchRequest<T> searchRequest);
}
