
package com.example.ottproject;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "video_id",
        "ad_id",
        "ad_start_time",
        "ad_end_time",
        "ad_image",
        "on_click"
})

public class Advertisement {

    @JsonProperty("video_id")
    private String videoId;
    @JsonProperty("ad_id")
    private String adId;
    @JsonProperty("ad_start_time")
    private Integer adStartTime;
    @JsonProperty("ad_end_time")
    private Integer adEndTime;
    @JsonProperty("ad_image")
    private String adImage;
    @JsonProperty("on_click")
    private String onClick;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("video_id")
    public String getVideoId() {
        return videoId;
    }

    @JsonProperty("video_id")
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @JsonProperty("ad_id")
    public String getAdId() {
        return adId;
    }

    @JsonProperty("ad_id")
    public void setAdId(String adId) {
        this.adId = adId;
    }

    @JsonProperty("ad_start_time")
    public Integer getAdStartTime() {
        return adStartTime;
    }

    @JsonProperty("ad_start_time")
    public void setAdStartTime(Integer adStartTime) {
        this.adStartTime = adStartTime;
    }

    @JsonProperty("ad_end_time")
    public Integer getAdEndTime() {
        return adEndTime;
    }

    @JsonProperty("ad_end_time")
    public void setAdEndTime(Integer adEndTime) {
        this.adEndTime = adEndTime;
    }

    @JsonProperty("ad_image")
    public String getAdImage() {
        return adImage;
    }

    @JsonProperty("ad_image")
    public void setAdImage(String adImage) {
        this.adImage = adImage;
    }

    @JsonProperty("on_click")
    public String getOnClick() {
        return onClick;
    }

    @JsonProperty("on_click")
    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}