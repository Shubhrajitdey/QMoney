package com.crio.warmup.stock.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//  Implement the Candle interface in such a way that it matches the parameters returned
//  inside Json response from Alphavantage service.

//  Reference - https:www.baeldung.com/jackson-ignore-properties-on-serialization
// Reference - https:www.baeldung.com/jackson-name-of-property

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageCandle implements Candle {
  @JsonProperty("1. open")
  private Double open;
  @JsonProperty("2. high")
  private Double high;
  @JsonProperty("3. low")
  private Double low;
  @JsonProperty("4. close")
  private Double close;
  
  private LocalDate date;
  public void setOpen(Double open) {
    this.open = open;
  }

  public void setClose(Double close) {
    this.close = close;
  }

  public void setHigh(Double high) {
    this.high = high;
  }

  public void setLow(Double low) {
    this.low = low;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
  @Override
  public Double getOpen() {
    return open;
  }
  
  @Override
  public Double getClose() {
    return close;
  }
  
  @Override
  public Double getHigh() {
    return high;
  }
  
  @Override
  public Double getLow() {
    return low;
  }
  
  @Override
  public LocalDate getDate() {
    return date;
  }
}

