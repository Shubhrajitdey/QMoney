
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {
  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    String TOKEN = "8f01a5a63850b10acdd7c2af98189529a3bcd575";
    List<String> sortedSymbol = new ArrayList<>();
    List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
    List<PortfolioTrade> portfolioTradesList = PortfolioManagerApplication.readTradesFromJson(args[0]);
    LocalDate localDate = LocalDate.parse(args[1]);
    RestTemplate restTemplate = new RestTemplate();
    for (PortfolioTrade pt : portfolioTradesList) {
      TiingoCandle[] response = restTemplate.getForObject(PortfolioManagerApplication.prepareUrl(pt,localDate,TOKEN),TiingoCandle[].class);
      totalReturnsDtos.add(new TotalReturnsDto(pt.getSymbol(), response[response.length-1].getClose()));
    }
    Collections.sort(totalReturnsDtos,
        (a, b) -> Double.compare(a.getClosingPrice(), b.getClosingPrice()));
    for (TotalReturnsDto tdto : totalReturnsDtos) {
      sortedSymbol.add(tdto.getSymbol());
    }
    return sortedSymbol;

  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
     List<PortfolioTrade> portfolioTradesList = PortfolioManagerApplication.getObjectMapper().readValue(
                                           PortfolioManagerApplication.resolveFileFromResources(filename),
                                           new TypeReference<List<PortfolioTrade>>() {});
     
     return portfolioTradesList;
  }

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    List<String> symbolList = new ArrayList<String>();
    List<PortfolioTrade> portfolioTradesList = PortfolioManagerApplication.getObjectMapper().readValue(
                                            PortfolioManagerApplication.resolveFileFromResources(args[0]),
                                            new TypeReference<List<PortfolioTrade>>() {});
    for (PortfolioTrade pt : portfolioTradesList) {
      symbolList.add(pt.getSymbol());
    }
    //System.out.println(symbolList);                    
     return symbolList;
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    //String url = "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices/"+"?&endDate="+endDate+"&sort=-date&token="+token;
    String url = "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices"+"?startDate="+trade.getPurchaseDate()+"&endDate="+endDate+"&token="+token;
     return url;
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
   return Paths.get(
       Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
 }

  private static ObjectMapper getObjectMapper() {
   ObjectMapper objectMapper = new ObjectMapper();
   objectMapper.registerModule(new JavaTimeModule());
   return objectMapper;
 }

 private static void printJsonObject(Object object) throws IOException {
   Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
   logger.info(PortfolioManagerApplication.getObjectMapper().writeValueAsString(object));
 }

 public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/shubhrajitdey98-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@67c27493";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "24.1";
    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
      toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
      lineNumberFromTestFileInStackTrace});
    }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainReadQuotes(args));
  }
}

