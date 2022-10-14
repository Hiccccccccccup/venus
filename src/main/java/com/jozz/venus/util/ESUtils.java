package com.jozz.venus.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jozz.venus.domain.ESEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.opensearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ESUtils {
    //    @Autowired
//    private OpenSearchClient openSearchClient;
    private static RestClient restClient;
    private final static Integer SUCCESS = 200;
    private final static Integer SUCCESS_UPDATE = 201;

    private final static String UPDATE = "_update";

    @Autowired
    public ESUtils(RestClient restClient) {
        ESUtils.restClient = restClient;
    }

    /**
     * 创建索引
     *
     * @param indexName
     */
    public static boolean createIndex(String indexName) {
        try {
            Request request = new Request("PUT", indexName);
            Response response = restClient.performRequest(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == SUCCESS) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }


    /**
     * 判断索引是否存在
     *
     * @param indexName
     */
    public static boolean indexIsExist(String indexName) {
        try {
            Request request = new Request("HEAD", indexName);
            Response response = restClient.performRequest(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == SUCCESS) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 添加
     *
     * @param indexName，索引
     * @param id，主键
     * @param obj，数据
     */
    public static boolean insert(String indexName, Serializable id, Object obj) {
        try {
            String surfix = id == null ? "" : "/" + id;
            String endPoint = "/" + indexName + "/_doc" + surfix;
            Request request = new Request("PUT", endPoint);
            String result = JsonUtils.toJson(obj);
            request.setJsonEntity(result);
            Response response = restClient.performRequest(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == SUCCESS || status == SUCCESS_UPDATE) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * ES数据更新 更新非空值
     *
     * @param indexName
     * @param id
     * @param obj
     * @return
     */
    public static boolean updateData(String indexName, String id, Object obj) {
        try {
            String endPoint = "/" + indexName + "/" + UPDATE + "/" + id;
            Request request = new Request("POST", endPoint);

            Map<String, Object> map = JsonUtils.jsonToPojo(JsonUtils.toJson(obj), Map.class);

            HashMap<String, Object> requestData = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value == null || StringUtils.isBlank(value.toString()) || "null".equals(value)) {
                    continue;
                }
                requestData.put(entry.getKey(), value);
            }
            UpdateDate updateDate = new UpdateDate(requestData);
            String result = JSON.toJSON(updateDate).toString();
            request.setJsonEntity(result);
            Response response = restClient.performRequest(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == SUCCESS || status == SUCCESS_UPDATE) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * 根据ID删除
     *
     * @param indexName
     * @param id
     * @return
     */
    public static boolean deleteById(String indexName, Serializable id) {
        try {
            if (id == null) {
                return false;
            }
            String endPoint = "/" + indexName + "/_doc/" + id;
            Request request = new Request("DELETE", endPoint);
            Response response = restClient.performRequest(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == SUCCESS || status == SUCCESS_UPDATE) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 根据ID查询
     *
     * @param indexName
     * @param id
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T findById(String indexName, Serializable id, Class<T> tClass) {
        Response response = null;
        try {
            String endPoint = "/" + indexName + "/_doc/" + id;
            Request request = new Request("GET", endPoint);
            response = restClient.performRequest(request);
            JSONObject json = convert(response);
            T source = json.getObject("_source", tClass);
            return source;
        } catch (ResponseException e) {
            if (e.getMessage().indexOf("404") != -1) {
                JSONObject json = convert(e.getResponse());
                if (json == null) {
                    throw new RuntimeException(e);
                }
                if (json.getBoolean("found") != null && !json.getBoolean("found")) {
                    return null;
                }
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Response解析
     *
     * @param response
     * @return
     */
    private static JSONObject convert(Response response) {
        if (response != null && response.getEntity() != null) {
            String result = null;
            try {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return JSON.parseObject(result);
        }
        return null;
    }

    public static String findData(String indexName, ESEntity domain) {
        if (ObjectUtils.isEmpty(domain)) {
            domain = ESEntity.of(1, 10);
        }
        int pageSize = domain.getPageSize();
        int page = domain.getPageNumber();
        int startLine = page == 1 ? 0 : (page - 1) * pageSize;
        String endPoint = "/" + indexName + "/_search";
        try {
//            SearchResponse<Map> searchResponse = openSearchClient.search(
//                    s -> s.index(indexName)
//                            .from(0)
//                            .size(10),
//                    Map.class);
//            for (Hit<Map> hit: searchResponse.hits().hits()) {
//                Map po = hit.source();
//                System.out.println(po);
//            }


            HashMap<String, Object> map = build(pageSize, startLine, domain);
            String source = JsonUtils.toJson(map);
            System.out.println(source);
            NStringEntity entity = new NStringEntity(source, ContentType.APPLICATION_JSON);
            Request request = new Request("POST", endPoint);
            request.setEntity(entity);
            Response response = restClient.performRequest(request);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = JSON.parseObject(result);
            JSONArray arr = new JSONArray();
            JSONObject hits = json.getJSONObject("hits");
            JSONArray hits1 = hits.getJSONArray("hits");
            hits1.forEach(j -> {
                arr.add(((JSONObject) j).getJSONObject("_source"));
            });
            Integer total = json.getJSONObject("hits").getJSONObject("total").getInteger("value");
            return JsonUtils.toJson(arr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建请求体payload
     *
     * @param pageSize
     * @param startLine
     * @param esEntity
     * @return
     */
    public static HashMap<String, Object> build(Integer pageSize, Integer startLine, ESEntity esEntity) {
        HashMap<String, Object> root = new HashMap<>();
        if (pageSize != null && startLine != null) {
            root.put("from", startLine);
            root.put("size", pageSize);
        }
        // 排序条件
        if (!CollectionUtils.isEmpty(esEntity.getSort())) {
            List<Map<String, Object>> sortList = new ArrayList<>();
            for (ESEntity.Sort sort : esEntity.getSort()) {
                Map<String, Object> sortItem = sort.toMap4Search();
                sortList.add(sortItem);
            }
            root.put("sort", sortList);
        }
        List<Map<String, Object>> mustList = new ArrayList<>();
        //range范围查询
        if (!CollectionUtils.isEmpty(esEntity.getRanges())) {
            for (ESEntity.Range range : esEntity.getRanges()) {
                Map<String, Object> rangeItem = range.toMap4Search();
                mustList.add(rangeItem);
            }
        }
        //match条件查询
        if (!CollectionUtils.isEmpty(esEntity.getMatches())) {
            for (ESEntity.Match match : esEntity.getMatches()) {
                Map<String, Object> matchItem = match.toMap4Search();
                mustList.add(matchItem);
            }
        }
        //match_phrase条件查询
        if (!CollectionUtils.isEmpty(esEntity.getMatchePhrases())) {
            for (ESEntity.MatchPhrase matchPhrase : esEntity.getMatchePhrases()) {
                Map<String, Object> matchPhraseItem = matchPhrase.toMap4Search();
                mustList.add(matchPhraseItem);
            }
        }
        // terms条件查询
        if (!CollectionUtils.isEmpty(esEntity.getTerms())) {
            for (ESEntity.Terms terms : esEntity.getTerms()) {
                Map<String, Object> termsItem = terms.toMap4Search();
                mustList.add(termsItem);
            }
        }
        HashMap<String, Object> boolVaue = new HashMap<>();
        boolVaue.put("must", mustList);
        HashMap<String, Object> queryValue = new HashMap<>();
        queryValue.put("bool", boolVaue);
        root.put("query", queryValue);
        return root;
    }


    static class UpdateDate {
        private Object doc;

        public Object getDoc() {
            return doc;
        }

        public void setDoc(Object doc) {
            this.doc = doc;
        }

        public UpdateDate(Object doc) {
            this.doc = doc;

        }
    }

}