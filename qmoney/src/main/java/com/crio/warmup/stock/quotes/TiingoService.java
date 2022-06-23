
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  RestTemplate restTemplate;
  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  // ./gradlew test --tests TiingoServiceTest


  // CHECKSTYLE:OFF
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws StockQuoteServiceException {
    TiingoCandle[] responseArr;
    String response = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
    try{
      responseArr = getObjectMapper().readValue(response, TiingoCandle[].class);
    } catch (Exception e) {
      throw new StockQuoteServiceException(e.getMessage());
    } 
    return Arrays.stream(responseArr)
      .sorted(Comparator.comparing(TiingoCandle::getDate))
      .collect(Collectors.toList());

  }
  
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call the Tiingo API.

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate="
        + startDate + "&endDate=" + endDate + "&token=" + getToken();
    return uriTemplate;
  }
  
  private static String getToken() {
    String TOKEN = "22e405823b96a0065f7e3ecb5b60a44ad046f434";
    return TOKEN;
  }
}
