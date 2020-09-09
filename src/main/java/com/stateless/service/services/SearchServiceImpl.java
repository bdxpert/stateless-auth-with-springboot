package com.stateless.service.services;

import com.stateless.service.entities.SearchData;
import com.stateless.service.extra.CustomAggregationOperation;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MongoTemplate mongoTemplate;


	@Override
	public List<SearchData> find(String queryStr) throws Exception {
		log.info(" full text search with: {}", queryStr);
		//String queryAggregation = "{$searchBeta: {\"compound\": {\"must\": {\"search\": {\"query\": '"+queryStr+"',\"path\": \"content\"}}}}},";
		String queryAggregation = "{$search: {\"text\": {\"path\": \"contentData\", \"query\": '"+ URLEncoder.encode(queryStr, "UTF-8")+"' }}}";

		Aggregation aggregation;
			aggregation = Aggregation.newAggregation(
					new CustomAggregationOperation(queryAggregation),
					Aggregation.project("id").and(aggregationOperationContext -> new Document("$meta", "searchScore")).as("score"),
					Aggregation.limit(10)
			);

		//queryAggregation = "{$searchBeta: {\"text\": {\"path\": 'contentData', \"query\": '"+queryStr+"' }}}, {$limit: 10},{$project:{\"_id\": 1,score: { $meta: \"searchScore\" }}}}}";
		AggregationResults<SearchData> results =
				mongoTemplate.aggregate(aggregation, "fileContent", SearchData.class);

		return results.getMappedResults();
	}


}
