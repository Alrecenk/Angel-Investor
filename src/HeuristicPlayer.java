import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class HeuristicPlayer extends Player{

  
  Heuristic heuristic ;
  
  
  
  public int chooseFlip(Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int selectDrawLocation(Game game) {
    // TODO Auto-generated method stub
    return 0;
  }


  public int chooseProjectforEffect(Card c, Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int[] chooseProjectCardforEffect(Card c, Game game) {
    // TODO Auto-generated method stub
    return null;
  }

  public int chooseDeckforEffect(Card c, Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int choosePlayerforEffect(Card c, Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int trashCard(Card c, int location, Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int[] reorderOrDiscard(Deck c, int location, Game game) {
    // TODO Auto-generated method stub
    return null;
  }

  public String getStatus() {
    // TODO Auto-generated method stub
    return null;
  }

  public Player copy() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  public double expectiMiniMax(Game g, int depth){
    
 // Run the game until you get to a choice or unknown card.
    Event next_event = null;
    ArrayList<Choice> possible_choices = null;
    int unknown_card_location = Player.NOWHERE;
    if(!g.event_queue.isEmpty()){ 
      next_event = g.event_queue.peek();
      if(next_event != null){ 
        possible_choices = next_event.getPossibleChoices(g);
        unknown_card_location = next_event.getUnknownCardLocation(g);
      }
      
    }
     while((next_event == null || (possible_choices == null && unknown_card_location == Player.NOWHERE)) && !g.game_over){
      g.step();
      if(!g.event_queue.isEmpty()){ 
        next_event = g.event_queue.peek();
        if(next_event != null){ 
          possible_choices = next_event.getPossibleChoices(g);
          unknown_card_location = next_event.getUnknownCardLocation(g);
        }
      }
    }
    
    if(depth == 0 ){
      return heuristic.score(g, player_number);
    }
    
    if(unknown_card_location != Player.NOWHERE){
      
      Deck d;
      if(unknown_card_location == Player.MAIN_DECK){
        d = g.main_deck;
      }else if(unknown_card_location == Player.HAND){
        //TODO something smart for poach and scandal, until then:
        return heuristic.score(g, player_number);
      }else if(unknown_card_location >=0){
        d = g.start_ups.get(unknown_card_location).deck;
      }else{
        System.err.println("Unknown card in unknonw location:" + unknown_card_location);
        return heuristic.score(g, player_number);
      }
      // Draw the unknown card and make a table of possibilities and likelihoods
      UnknownCard u = (UnknownCard)d.draw();
      HashMap<String, Integer> possibilities = new HashMap<String, Integer> ();
      HashMap<String, Integer> location = new HashMap<String, Integer> ();
      for(int k = 0 ; k < u.possible_cards.size();k++){
        String name = u.possible_cards.getCard(k).name;
        if(possibilities.containsKey(name)){
          possibilities.put(name, possibilities.get(name)+1);
        }else{
          possibilities.put(name, 1);
          location.put(name,k);
        }
      }
      // Take the average expectiminimax value for each possible card tree.
      double total_score = 0 ;
      Iterator<String> i = possibilities.keySet().iterator();
      while(i.hasNext()){
        String card_name = i.next();
        int chances = possibilities.get(card_name);
        Game ng = new Game(g,0);
        Card nc = u.possible_cards.getCard(location.get(card_name)).copy();
        if(unknown_card_location == Player.MAIN_DECK){
          ng.main_deck.add(nc);
        }else{
          ng.start_ups.get(unknown_card_location).deck.add(nc);
        }
        total_score += chances * expectiMiniMax(ng, depth - 1);
      }
      return total_score/u.possible_cards.size();
    }
    boolean my_choice = next_event.getChoosingPlayer(g) == player_number;
    if(my_choice){
      double max_score = -999999;
      for(int k=0;k<possible_choices.size();k++){
        Game ng = new Game(g,0);
        ng.event_queue.peek().selection = possible_choices.get(k);
        double score = expectiMiniMax(ng, depth - 1);
        if(score > max_score){
          max_score = score ;
        }
      }
      return max_score;
    }else{
      double min_score = 999999;
      for(int k=0;k<possible_choices.size();k++){
        Game ng = new Game(g, 0);
        ng.event_queue.peek().selection = possible_choices.get(k);
        double score = expectiMiniMax(ng, depth - 1);
        if(score < min_score){
          min_score = score ;
        }
      }
      return min_score;
    }
  }

  public int[] choosePlay(Game game) {
    // TODO Auto-generated method stub
    return null;
  }

}
