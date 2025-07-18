package com.api.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response model for User API calls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("per_page")
    private Integer perPage;
    
    @JsonProperty("total")
    private Integer total;
    
    @JsonProperty("total_pages")
    private Integer totalPages;
    
    @JsonProperty("data")
    private List<User> data;
    
    @JsonProperty("support")
    private Support support;
    
    // Single user response fields
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("job")
    private String job;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    // Default constructor
    public UserResponse() {}
    
    // Getters and Setters
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getPerPage() {
        return perPage;
    }
    
    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }
    
    public Integer getTotal() {
        return total;
    }
    
    public void setTotal(Integer total) {
        this.total = total;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    public List<User> getData() {
        return data;
    }
    
    public void setData(List<User> data) {
        this.data = data;
    }
    
    public Support getSupport() {
        return support;
    }
    
    public void setSupport(Support support) {
        this.support = support;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getJob() {
        return job;
    }
    
    public void setJob(String job) {
        this.job = job;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
    
    public int getDataSize() {
        return data != null ? data.size() : 0;
    }
    
    public User getFirstUser() {
        return hasData() ? data.get(0) : null;
    }
    
    public User getUserById(Integer userId) {
        if (data == null) return null;
        return data.stream()
                .filter(user -> userId.equals(user.getId()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return "UserResponse{" +
                "page=" + page +
                ", perPage=" + perPage +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", dataSize=" + getDataSize() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", job='" + job + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
    
    // Support class for nested support object
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Support {
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("text")
        private String text;
        
        public Support() {}
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return "Support{" +
                    "url='" + url + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
}
