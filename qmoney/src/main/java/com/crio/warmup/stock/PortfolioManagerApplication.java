
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {


  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static RestTemplate restTemplate=new RestTemplate();
  public static PortfolioManager portfolioManager=PortfolioManagerFactory.getPortfolioManager(restTemplate);
  
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(readTradesFromJson1(file)), endDate);
  }

  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
  
    RestTemplate restTemplate = new RestTemplate();
   // List<TiingoCandle[]> company = new ArrayList<>();
    TiingoCandle[] response = restTemplate.getForObject(
    prepareUrl(trade,endDate,"169f93269e3e6e617deb899feb399b3f430ebbca"),
    TiingoCandle[].class);

    List<Candle> obj=new ArrayList<>();
    for(TiingoCandle x:response){
       obj.add(x);
    }
    return obj;
   
}

  private static String readFileAsString(String file) {
    return null;
  }
  public static List<String> debugOutputs() {
 
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/tushar-khandave22-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@7e6f74c";
    String functionNameFromTestFileInStackTrace = "mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "154:1";
  
  
   return Arrays.asList(new String[]{
       valueOfArgument0, resultOfResolveFilePathArgs0,
       toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
       lineNumberFromTestFileInStackTrace});
   }
   public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File input=resolveFileFromResources(filename);
    List<PortfolioTrade> list=new ArrayList<>();
    ObjectMapper obj=getObjectMapper();
    PortfolioTrade[] trade=obj.readValue(input,PortfolioTrade[].class);
    for(PortfolioTrade x:trade){
      list.add(x);
    }
    return list;
  }
  
  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    //tingocandle.java will store the decerialized data
    List<PortfolioTrade> symbol=readTradesFromJson(args[0]);
    RestTemplate restTemplate = new RestTemplate();
    List<TiingoCandle[]> company = new ArrayList<>();
    // List<String> answer=new ArrayList<>();
    // HashMap<PortfolioTrade,TiingoCandle> map=new HashMap<>();
    List<TotalReturnsDto> ansObj=new ArrayList<>();
    for(PortfolioTrade x:symbol)
     {
      TiingoCandle[] response = restTemplate.getForObject(
      prepareUrl(x,LocalDate.parse(args[1]),"169f93269e3e6e617deb899feb399b3f430ebbca"),
      TiingoCandle[].class);
      // company.add(response);
      // TiingoCandle abc=response[response.length-1];
      // map.put(x,abc);
      if(response!=null){
        ansObj.add(new TotalReturnsDto(x.getSymbol(),response[response.length-1].getClose()));
      }

      
      }
      Collections.sort(ansObj,new closing());
      List<String>finalList=new ArrayList<>();
      for(TotalReturnsDto x:ansObj){
           finalList.add(x.getSymbol());
      }
      return finalList;
    //  List<Employee> employees = response.getEmployees();
  }
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String url="https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate()+"&endDate="+endDate+"&token="+token;
     return url;
  }
  public static PortfolioTrade[] readTradesFromJson1(String filename) throws IOException, URISyntaxException {
    File input=resolveFileFromResources(filename);
    List<PortfolioTrade> list=new ArrayList<>();
    ObjectMapper obj=getObjectMapper();
    PortfolioTrade[] trade=obj.readValue(input,PortfolioTrade[].class);
    // for(PortfolioTrade x:trade){
    //   list.add(x);
    // }
    // return list;
    return trade;
  }

  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
   
    return candles.get(0).getOpen();
 }
 public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }
 public static String getToken(){
   return "169f93269e3e6e617deb899feb399b3f430ebbca";
 }

  private static File resolveFileFromResources(String filename) throws URISyntaxException
  {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }



public static List<String> mainReadFile(String[]args)throws IOException, URISyntaxException{
  File input=resolveFileFromResources(args[0]);
  List<String> list=new ArrayList<>();
  ObjectMapper obj=getObjectMapper();
  PortfolioTrade[] trade=obj.readValue(input,PortfolioTrade[].class);
  for(PortfolioTrade x:trade){
    list.add(x.getSymbol());
  }
  return list;
 // return Collections.emptyList();

}

 

  private static ObjectMapper getObjectMapper() 
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  private static void printJsonObject(Object object) throws IOException 
  {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }
  
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
      List<AnnualizedReturn> obj=new ArrayList<>();
      List<PortfolioTrade> trad=readTradesFromJson(args[0]);

      for(PortfolioTrade x:trad){
        
        List<Candle> candle=fetchCandles(x, LocalDate.parse(args[1]), getToken());
        obj.add(calculateAnnualizedReturns(LocalDate.parse(args[1]),x,getOpeningPriceOnStartDate(candle),getClosingPriceOnEndDate(candle)));
      }
      Collections.sort(obj,new asending());
     
     return obj;
  }


  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      //how to resolve the symbol and purchase date from the object of the profolioTrade app
      double totalReturn=(sellPrice-buyPrice)/buyPrice;
      //I have to count the number of days between two dates there may be funciton for this
      //i have to convert that number of days into years
      LocalDate p=trade.getPurchaseDate();
      

      double total_num_years = p.until(endDate, ChronoUnit.DAYS)/365.24;
      // long daysBetween = Duration.between(trade.getPurchaseDate(), endDate).toDays();
      // //divide it by the 365 
      // double total_num_years=daysBetween/365;
      double annualized_returns=Math.pow(1+totalReturn, (1/total_num_years))-1;
      //how to return the symbol with this two parameters in java
      

      return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  }
  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    


    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

class closing implements Comparator<TotalReturnsDto>
{

  @Override
  public int compare(TotalReturnsDto arg0, TotalReturnsDto arg1) {
    if(arg0.getClosingPrice()>arg1.getClosingPrice()) return 1;
    else if (arg0.getClosingPrice()<arg1.getClosingPrice()) return -1;
    else return 0;
   
  }

}

class asending implements Comparator<AnnualizedReturn>
{

  @Override
  public int compare(AnnualizedReturn arg0, AnnualizedReturn arg1) {
    if(arg0.getTotalReturns()<arg1.getTotalReturns()) return 1;
    else if (arg0.getTotalReturns()>arg1.getTotalReturns()) return -1;
    else return 0;
  }
  
}