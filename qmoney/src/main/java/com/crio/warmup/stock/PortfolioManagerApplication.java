
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.crio.warmup.stock.portfolio.PortfolioManagerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
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
  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.




  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
     return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }
  
  public static String getToken() {
    String TOKEN = "22e405823b96a0065f7e3ecb5b60a44ad046f434";
    return TOKEN;
  }

  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    TiingoCandle[] response = restTemplate.getForObject(
              PortfolioManagerApplication.prepareUrl(trade, endDate,token), TiingoCandle[].class);

     return Arrays.stream(response).collect(Collectors.toList());
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> portfolioTradesList = readTradesFromJson(args[0]);
    List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>();
    LocalDate endDate = LocalDate.parse(args[1]);
    for (PortfolioTrade pt : portfolioTradesList) {
      List<Candle> tiingoCandlesList = fetchCandles(pt, endDate, getToken());
      Double openingPrice = getOpeningPriceOnStartDate(tiingoCandlesList);
      Double closingPrice = getClosingPriceOnEndDate(tiingoCandlesList);
      annualizedReturnsList
          .add(calculateAnnualizedReturns(endDate, pt, openingPrice, closingPrice));
    }
    System.out.println(annualizedReturnsList);
    return annualizedReturnsList.stream()
        .sorted((a1, a2) -> Double.compare(a2.getAnnualizedReturn(), a1.getAnnualizedReturn()))
        .collect(Collectors.toList());
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      Double totalReturn = (sellPrice - buyPrice) / buyPrice;
      Double noOfYears = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / 365.24;
      Double annualizedReturns = Math.pow((1.0 + totalReturn), (1.0 / noOfYears)) - 1;
      return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturn);
  }
  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  private static String readFileAsString(String file)
      throws UnsupportedEncodingException, IOException, URISyntaxException {
    return new String(Files.readAllBytes(resolveFileFromResources(file).toPath()), "UTF-8");
  }
  
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       RestTemplate restTemplate = new RestTemplate();
       PortfolioManager portfolioManager =
           PortfolioManagerFactory.getPortfolioManager(restTemplate);
       PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
      return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

