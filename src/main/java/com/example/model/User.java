package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.io.Serializable;
import java.util.Date;

@DynamoDbBean
public class User implements Serializable {

  @JsonProperty("nick")
  private String nickName = null;

  @JsonProperty("userId")
  private String email = null;

  @JsonProperty("logo")
  private String LogoOriginFileName = null;

  @JsonProperty("logokey")
  private String LogoFileKey = null;

  @JsonProperty("accesstime")
  private Date accessTime = null;

  @JsonProperty("rank")
  private Integer rank = null;

  public User() {
  }

  public User(String nickName, String email, String logoOriginFileName, String logoFileKey, Date accessTime, Integer rank) {
    this.nickName = nickName;
    this.email = email;
    LogoOriginFileName = logoOriginFileName;
    LogoFileKey = logoFileKey;
    this.accessTime = accessTime;
    this.rank = rank;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  @DynamoDbPartitionKey
  public String getEmail() {
    return email;
  }

  @DynamoDbPartitionKey
  public void setEmail(String email) {
    this.email = email;
  }

  public String getLogoOriginFileName() {
    return LogoOriginFileName;
  }

  public void setLogoOriginFileName(String logoOriginFileName) {
    LogoOriginFileName = logoOriginFileName;
  }

  public String getLogoFileKey() {
    return LogoFileKey;
  }

  public void setLogoFileKey(String logoFileKey) {
    LogoFileKey = logoFileKey;
  }

  public Date getAccessTime() {
    return accessTime;
  }

  public void setAccessTime(Date accessTime) {
    this.accessTime = accessTime;
  }

  public Integer getRank() {
    return rank;
  }

  public void setRank(Integer rank) {
    this.rank = rank;
  }
}
