package com.jozz.venus.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jozz.venus.domain.ESEntity;
import com.jozz.venus.domain.ESResult;
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
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ESUtils {
//    @Autowired
//    private OpenSearchClient openSearchClient;
    private final RestClient restClient;
    private final Integer SUCCESS = 200;
    private final Integer SUCCESS_UPDATE = 201;

    private final String UPDATE = "_update";

    /**
     * 创建索引
     *
     * @param indexName
     */
    public boolean createIndex(String indexName) {
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
    public boolean indexIsExist(String indexName) {
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
     * 添加/修改
     *
     * @param indexName，索引
     * @param id，主键
     * @param obj，数据
     */
    public boolean insertData(String indexName, String id, Object obj) {
        try {
            String endPoint = "/" + indexName + "/_doc/" + id;
            Request request = new Request("PUT", endPoint);
            //request.setJsonEntity(removeNull(obj));
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
    public <T> T findById(String indexName, String id,Class<T> tClass) {
        Response response = null;
        try {
            String endPoint = "/" + indexName + "/_doc/" + id;
            Request request = new Request("GET", endPoint);
            response = restClient.performRequest(request);
            JSONObject json = extracted(response);
            T source = json.getObject("_source",tClass);
            return source;
        } catch (ResponseException e) {
            if (e.getMessage().indexOf("404") != -1) {
                JSONObject json = extracted(e.getResponse());
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

    private JSONObject extracted(Response response){
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

    /**
     * 去重null值，提高效率
     */
    private String removeNull(Object obj) {
        if (!ObjectUtils.isEmpty(obj)) {
            JSONObject json = JSON.parseObject(JsonUtils.toJson(obj));
            List<String> keyList = new ArrayList();
            for (String key : json.keySet()) {
                Object value = json.get(key);
                if (!ObjectUtils.isEmpty(value)) {
                    String valueStr = value.toString();
                    if (isJsonObj(valueStr)) {
                        json.put(key, removeNull(JSONObject.parseObject(valueStr)));
                    } else if (isJsonArr(valueStr)) {
                        json.put(key, removeNull(JSONArray.parseArray(valueStr)));
                    }
                } else {
                    keyList.add(key);
                }
            }
            for (String key : keyList) {
                json.remove(key);
            }
            return json.toJSONString();
        }
        return null;
    }


    public String findData(String indexName, ESEntity domain) {
        if (ObjectUtils.isEmpty(domain)) {
            domain = ESEntity.of(1, 10);
        }
        int pageSize = domain.getPageSize();
        int page = domain.getPageNumber();
        int startLine = page == 1 ? 0 : (page - 1) * pageSize;
        String endPoint = "/" + indexName + "_search";
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
            XContentBuilder builder = this.getXContentBuilder(domain, pageSize, startLine);

//            request1.setOptions(builder);


            NStringEntity entity = new NStringEntity("source", ContentType.APPLICATION_JSON);
            Request request = new Request("POST", endPoint);
            request.setEntity(entity);
            Response response = restClient.performRequest(request);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = JSON.parseObject(result);
            JSONArray arr = new JSONArray();
            json.getJSONObject("hits").getJSONArray("hits").forEach(j -> {
                arr.add(((JSONObject) j).getJSONObject("_source"));
            });
            return JsonUtils.toJson(arr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ESResult findDataAndCount(String indexName, String indexType, ESEntity domain) {
        if (ObjectUtils.isEmpty(domain)) {
            domain = ESEntity.of(1, 10);
        }
        int pageSize = domain.getPageSize();
        int page = domain.getPageNumber();
        int startLine = page == 1 ? 0 : (page - 1) * pageSize;
        String endPoint = "/" + indexName + "/" + indexType + "/" + "_search";
        try {
            XContentBuilder builder = this.getXContentBuilder(domain, pageSize, startLine);

//            IndexRequest indexRequest = new IndexRequest();
//            indexRequest.source(builder);
//            String source = indexRequest.source().utf8ToString();
            NStringEntity entity = new NStringEntity("source", ContentType.APPLICATION_JSON);
            Request request = new Request("POST", endPoint);
            request.setEntity(entity);
            Response response = restClient.performRequest(request);

            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = JSON.parseObject(result);
            JSONArray arr = new JSONArray();
            json.getJSONObject("hits").getJSONArray("hits").forEach(j -> {
                arr.add(((JSONObject) j).getJSONObject("_source"));
            });

            String content = JsonUtils.toJson(arr);
            Integer total = json.getJSONObject("hits").getJSONObject("total").getInteger("value");

            // 打印请求体
            log.info("request={}");
            // 打印返回体
            log.info("response={}", response);

            return ESResult.of(total, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private XContentBuilder getXContentBuilder(ESEntity domain, int pageSize, int startLine) throws IOException {
        // 组装查询结构体
        XContentBuilder builder = JsonXContent.contentBuilder()
                .startObject()
                // 设置获取的数据范围
                .field("from", startLine)
                .field("size", pageSize);
        // 排序条件
        if (Objects.nonNull(domain.getSort())) {
            ESEntity.Sort sort = domain.getSort();
            if (sort != null && StringUtils.isNoneBlank(sort.getField(), sort.getType())) {
                builder.startObject("sort")
                        .startObject(sort.getField())
                        .field("order", sort.getType())
                        .endObject()
                        .endObject();
            }
        }
        // 查询条件时，query后面必须带上bool，否则会有语法报错
        builder.startObject("query")
                .startObject("bool")
                .startArray("must");
        //range范围查询
        for (ESEntity.Range range : ObjectUtils.defaultIfNull(domain.getRanges(), Collections.<ESEntity.Range>emptyList())) {
            if (StringUtils.isNotBlank(range.getField()) && (Objects.nonNull(range.getFrom()) || Objects.nonNull(range.getTo()))) {
                builder.startObject()
                        .startObject("range")
                        .startObject(range.getField())
                        .field("from", range.getFrom())
                        .field("to", range.getTo())
                        .field("include_lower", "true")
                        .field("include_upper", "true")
                        .endObject()
                        .endObject()
                        .endObject();
            }
        }
        // match条件查询
        for (ESEntity.Match match : ObjectUtils.defaultIfNull(domain.getMatches(), Collections.<ESEntity.Match>emptyList())) {
            if (StringUtils.isNotBlank(match.getField()) && Objects.nonNull(match.getValue())) {
                builder.startObject()
                        .startObject(match.getOperationName())
                        .field(match.getField(), match.getWildCardValue())
                        .endObject()
                        .endObject();
            }
        }
        // terms条件查询
        for (ESEntity.Terms terms : ObjectUtils.defaultIfNull(domain.getTerms(), Collections.<ESEntity.Terms>emptyList())) {
            if (StringUtils.isNotBlank(terms.getField()) && Objects.nonNull(terms.getValue())) {
                builder.startObject()
                        .startObject("terms")
                        .field(terms.getField(), terms.getValue())
                        .endObject()
                        .endObject();
            }
        }
        // nested查询
        for (ESEntity.Nested nested : ObjectUtils.defaultIfNull(domain.getNesteds(), Collections.<ESEntity.Nested>emptyList())) {
            List<ESEntity.Match> matches = nested.getMatches();
            if (CollectionUtils.isEmpty(matches)) {
                continue;
            }
            builder.startObject()
                    .startObject("nested")
                    .field("path", nested.getPath())
                    .startObject("query")
                    .startObject("bool")
                    .startArray("must");
            for (ESEntity.Match match : matches) {
                builder.startObject()
                        .startObject(match.getOperationName())
                        .field(match.getField(), match.getWildCardValue())
                        .endObject()
                        .endObject();
            }
            builder.endArray()
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        }
        // 结束结构体
        builder.endArray()
                .endObject()
                .endObject()
                .endObject();

        return builder;
    }


    private boolean isJsonObj(Object obj) {
        try {
            JSONObject.parseObject(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isJsonArr(Object obj) {
        try {
            JSONArray.parseArray(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    class UpdateDate{
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

    /**
     * ES数据更新 更新非空值
     * @param indexName
     * @param id
     * @param obj
     * @return
     */
    public boolean updateData(String indexName, String id, Object obj) {
        try {
            String endPoint = "/" + indexName + "/" + UPDATE + "/" + id;
            Request request = new Request("POST", endPoint);

            Map<String,Object> map = JsonUtils.jsonToPojo(JsonUtils.toJson(obj), Map.class);

            HashMap<String, Object> requestData = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if(value == null || StringUtils.isBlank(value.toString()) || "null".equals(value)){
                    continue;
                }
                requestData.put(entry.getKey(),value);

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


    public boolean deleteById(String indexName, Long id) {
        try {
            if(id == null){
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

    public boolean deleteTeamsById(String indexName, Long id) {
        try {
            if(StringUtils.isBlank(String.valueOf(id))){
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

}