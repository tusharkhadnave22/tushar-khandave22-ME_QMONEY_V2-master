
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  public static RestTemplate restTemplate=new RestTemplate();



  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        // List<TiingoCandle[]> company = new ArrayList<>();
         TiingoCandle[] response = restTemplate.getForObject(
          buildUri(symbol,from,to),
         TiingoCandle[].class);
   
         List<Candle> obj=new ArrayList<>();
         for(TiingoCandle x:response){
            obj.add(x);
         }
         return obj;
  
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
      //       + "startDate"+"=&"+endDate+"=$ENDDATE&token=";
      //       return uriTemplate;
      String url="https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate+"&endDate="+endDate+"&token="+"169f93269e3e6e617deb899feb399b3f430ebbca";
      return url;

  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {

        // LocalDate endDate,
        // PortfolioTrade trade,
        // for this we have to call getStockquote
        // Double buyPrice 
        // Double sellPrice; 
         List<AnnualizedReturn> ansObj=new ArrayList<>();
         Map<String,List<Candle>> obj=new HashMap<>();
         for(PortfolioTrade x:portfolioTrades)
         {
           try {
            obj.put(x.getSymbol(),getStockQuote(x.getSymbol(),x.getPurchaseDate(),endDate));
          } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
         }
        for(PortfolioTrade x:portfolioTrades){
        double  buyPrice=obj.get(x.getSymbol()).get(0).getOpen();
        double  sellPrice=obj.get(x.getSymbol()).get(obj.get(x.getSymbol()).size()-1).getClose();
        double totalReturn=(sellPrice-buyPrice)/buyPrice;
      
        LocalDate p=x.getPurchaseDate();
        
  
        double total_num_years = p.until(endDate, ChronoUnit.DAYS)/365.24;
       
        double annualized_returns=Math.pow(1+totalReturn, (1/total_num_years))-1;
        ansObj.add(new AnnualizedReturn(x.getSymbol(), annualized_returns, totalReturn));
      }
      Collections.sort(ansObj,new sorter());
     
        return ansObj;
    
  }
}
class sorter implements Comparator<AnnualizedReturn>{


  @Override
  public int compare(AnnualizedReturn arg0, AnnualizedReturn arg1) {
    if(arg0.getAnnualizedReturn()<arg1.getAnnualizedReturn()) return 1;
    else if (arg0.getAnnualizedReturn()>arg1.getAnnualizedReturn()) return -1;
    else return 0;
  }
  
}
