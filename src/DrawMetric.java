import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// This metric tracks the amount of draws of each card for each player during a game.
public class DrawMetric extends Metric{

  public ArrayList<HashMap<String, Integer>> draws; // Index is which player then map of card to amount drawn.
  
  Game last_game ;
  
  public DrawMetric(){
    draws = new ArrayList<HashMap<String, Integer>>();
    for(int k=0;k<Money.MONEY_NAMES.length;k++){
      draws.add(new HashMap<String, Integer>());
    }
    last_game = null;
  }
  
  public void count(int player, Card c){
    HashMap<String, Integer> h = draws.get(player);
    String name = c.name;
    if(c instanceof Money){ // Log all money the same to make it easier to coalesce for players in different seats.
      name = Money.GENERAL_MONEY_NAME;
    }
    if(h.containsKey(name)){
      h.put(name, h.get(name)+1);
    }else{
      h.put(name, 1);
    }
  }

  public void measure(Event e, Game game) {
    e.resetReadPointer();
    if(game != last_game){// If a new game
      last_game = game;
      for(int k=0;k<game.numPlayers();k++){
        Deck h = game.getPlayer(k).getHand();
        for(int j = 0; j < h.size();j++){
          count(k,h.getCard(j)); // Tally the initial hands
        }
      }
    }
    if(e instanceof DrawPhase){ // Count the draw phase cards
      int draw_select = e.readChoice();
      int drawn = e.readChoice();
      Player p = game.getPlayer(game.getTurn());
      int cards = p.getHand().size();
      if(drawn >=1){
        count(p.getPlayerNumber(), p.getHand().getCard(cards-1));
      }
      if(drawn >=2){
        count(p.getPlayerNumber(), p.getHand().getCard(cards-2));
      }
    }else if(e instanceof CompleteCard){ // Count the draws from completing cards
      CompleteCard cc = ((CompleteCard)e);
      Card c = cc.completed_card;
      cc.resetReadPointer(); 
      int which_row = cc.readChoice();
      Player p = game.getPlayer(cc.readChoice());
      if(p!=null){
        int cards = p.getHand().size();
        
        if(!game.game_over && (c instanceof Capital || c instanceof Profit)){
          count(p.getPlayerNumber(), p.getHand().getCard(cards-1));
        }
      }
    }else if(e instanceof SpendCard){ // Count the draws from spending cards
      SpendCard sc = ((SpendCard)e);
      Card c = sc.spent_card;
      Player p = game.getPlayer(game.getTurn());
      int cards = p.getHand().size();
      if(!game.game_over && (c instanceof Capital || c instanceof Profit)){
        count(p.getPlayerNumber(), p.getHand().getCard(cards-1));
      }
    }
    
  }
  
  public String toString(){
    String s = "";
    for(int k=0;k<draws.size();k++){
      s+= "Player " + k + " + drew:\n";
      HashMap<String, Integer> h = draws.get(k);
      Iterator<String> i = h.keySet().iterator();
      while(i.hasNext()){
        String n = i.next();
        s+= "  " + n + " : " + h.get(n) + "\n";
      }
    }
    return s ;
  }
  
}
