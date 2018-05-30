import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class FlipMetric extends Metric{

public ArrayList<HashMap<String, Integer>> flips; // Index is which player then map of card to amount flipped
  
public static final String YOUR_MONEY =  "Your Money";
public static final String THEIR_MONEY = "Their Money";
  
  public FlipMetric(){
    flips = new ArrayList<HashMap<String, Integer>>();
    for(int k=0;k<Money.MONEY_NAMES.length;k++){
      flips.add(new HashMap<String, Integer>());
    }
  }
  
  public void count(int player, Card c){
    HashMap<String, Integer> h = flips.get(player);
    String name = c.name;
    if(c instanceof Money){ // Log all money as yours or theirs to make it easier to coalesce for players in different seats.
      if(((Money)c).player == player){
        name = YOUR_MONEY;
      }else{
        name = THEIR_MONEY;
      }
    }
    if(h.containsKey(name)){
      h.put(name, h.get(name)+1);
    }else{
      h.put(name, 1);
    }
  }
  
  public void measure(Event e, Game game) {
    if(e instanceof FlipCard){
      FlipCard f = (FlipCard)e;
      count(game.getTurn(), f.card);
    }
  }
  
  public String toString(){
    String s = "";
    for(int k=0;k<flips.size();k++){
      s+= "Player " + k + " + flipped:\n";
      HashMap<String, Integer> h = flips.get(k);
      Iterator<String> i = h.keySet().iterator();
      while(i.hasNext()){
        String n = i.next();
        s+= "  " + n + " : " + h.get(n) + "\n";
      }
    }
    return s ;
  }

}
