package com.example.company.service.impl;

import com.example.company.model.search.SearchRequest;
import com.example.company.service.SearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final EntityManager entityManager;

    @Override
    public <T> List<T> search(SearchRequest<T> searchRequest) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(searchRequest.getClazz());
        var root = criteriaQuery.from(searchRequest.getClazz());

        var predicates = generatePredicates(root, criteriaBuilder, searchRequest.getFilteringFields());
        predicates.forEach(criteriaQuery::where);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    <T> List<Predicate> generatePredicates(
            Root<T> root, CriteriaBuilder criteriaBuilder, List<SearchRequest.FilteringField> filteringFields) {
        return filteringFields.stream()
                .map(filteringField -> {
                    var path = root.get(filteringField.getFieldName());
                    return switch (filteringField.getOperator()) {
                        case EQUAL -> criteriaBuilder.equal(path, filteringField.getFieldValue());
                    };
                })
                .collect(Collectors.toList());
    }
}
