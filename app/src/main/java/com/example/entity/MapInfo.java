package com.example.entity;

import java.math.BigDecimal;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/1 16:50
 * @Decription : 地图信息的实体类，方便向后端传输地理位置，经纬度等信息
 */

public class MapInfo {

    private int id;
    private String placeName; // 地名 - 地点的名称
    private BigDecimal latitude; // 纬度 - 指定南北位置的地理坐标
    private BigDecimal longitude; // 经度 - 指定东西位置的地理坐标
    private int shareInfoId; // Share Info Id - 与图文与视频分享信息表关联的ID

    public MapInfo() {}
    public MapInfo(int id, String placeName, BigDecimal latitude,
                   BigDecimal longitude, int shareInfoId) {
        this.id = id;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.shareInfoId = shareInfoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public int getShareInfoId() {
        return shareInfoId;
    }

    public void setShareInfoId(int shareInfoId) {
        this.shareInfoId = shareInfoId;
    }

    @Override
    public String toString() {
        return "MapInfo{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", shareInfoId=" + shareInfoId +
                '}';
    }
}
