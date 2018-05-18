import java.util.Iterator;

// A player is any actor that makes choices in the game whether human or artificial intelligence.
public abstract class Player {

  protected Deck hand = new Deck(); // Player's hand.
  public int reserve; // Amount of money in player's reserve.
  protected int fame = 0 ; // Current score of player.
  protected int player_number; // Player's position on the table and money type.
  
  // Special locations in the game, >= 0 are the projects
  public static int MAIN_DECK= -1, CASH_RESERVE = -2, TRASH = -3, KEEP_CARD = -4, HAND = -5, CHOICE = -6, NOWHERE = -7;
  // special indices
  public static int DECK_INDEX = -1, DISCARD_INDEX = -2;
  
  
  public void setPlayerNumber(int pn){
      player_number = pn;
  }
  
  public int getPlayerNumber(){
    return player_number;
  }
  
  // Get a money from the player's reserve. Returns null if the reserve is empty.
  public Card drawMoney(){
    if(reserve > 0){
      reserve--;
      return new Money(player_number);
    }else{
      return null;
    }
  }

  // Put a card in a player's hand.
  public void add(Card c){
    hand.add(c);
  }
  
  // Give the player a point.
  public void giveFame(){
    fame++;
  }
  
  public double finalScore(){
    double final_score = fame;
    Iterator<Card> hand_iter = hand.iterator();
    while(hand_iter.hasNext()){
      final_score+=hand_iter.next().endGamePoints();
    }
    final_score+= reserve * .01;
    return final_score;
  }
  
  public String getName(){
    return "Player " + player_number ;
  }
  
  public Deck getHand(){
    return hand;
  }
  
  //Select which start-up to flip next during the flip phase.
  public abstract int chooseFlip(Game game); 
  
  // Select second draw. Should return either MAIN_DECK or CASH_RESERVE
  public abstract int selectDrawLocation(Game game);
  
  // Returns a play event to (probably) be executed immediately. Should be SpendCard, InvestCard, or CompleteProject. 
  public abstract Event makePlay(Game game);
  
  // Returns the index of a project to select for a given card's effect that targets a project(such as Epic Fail or Patent).
  public abstract int chooseProjectforEffect(Card c, Game game);
  
  // Returns the {project index, card index} for a given card's effect that targets a card in a project (such as sabotage or delay).
  public abstract int[] chooseProjectCardforEffect(Card c, Game game);
  
  // returns the project index or MAIN_DECK for a given card's effect that target a deck (such as pivot or planning).
  public abstract int chooseDeckforEffect(Card c, Game game);
  
  //returns the player index for a given card's effect that targets a player (such as poach or scandal).
  public abstract int choosePlayerforEffect(Card c, Game game);
  
  // Returns whether to trash a card ( TRASH_CARD or KEEP_CARD) when given the option (such as in pivot or publicity stunt).
  public abstract int trashCard(Card c, int location, Game game);
  
  // Returns the indexes into the given cards for the ordering from bottom to top of cards that are being put back in Planning. Cards left out will be discarded.
  public abstract int[] reorderOrDiscard(Deck c, int location, Game game);

  public int getFame() {
    return fame;
  }
  
  // Returns a string of the agents current status, typically called off thread to update the GUI.
  public abstract String getStatus();
  
  // Returns a non-shallow copy of the player.
  public abstract Player copy();
  
  // Sets the raw game data of this player to be a copy of the given player
  public void copyGameStateFrom(Player p){
    fame = p.fame;
    hand = p.hand.copy();
    reserve = p.reserve; 
    player_number = p.player_number; 
  }
  
  public String toString(){
    return "Player " + player_number + "- Fame:" + fame +", Reserve:" + reserve +", Hand:" + hand.toString();
  }
    
}
