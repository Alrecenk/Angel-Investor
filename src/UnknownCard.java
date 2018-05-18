/* This card represents a stand-in for cards unknown to players when making choices. 
 * The player knows that the card is one of a given set of cards.
 * These cards never appear in the real game, but may exist within theoretical games being managed by AI agents.
 * When any of their functions are called they will forward that call to the player.
 * They do not provide printing functions.
 */

public class UnknownCard extends Card{
  
  public static String UNKNOWN_CARD_NAME = "Unknown";
  
  int place, index;
  Deck possible_cards;
  Player call_back;
  
  
  public UnknownCard(Deck possible_cards, int place, int index, Player call_back){
    this.possible_cards = possible_cards;
    this.place = place;
    this.index = index;
    this.call_back = call_back;
    this.name = UNKNOWN_CARD_NAME;
  }
  
//TODO implement callback
  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new UnknownCard(possible_cards, place, index, call_back);
  }

}
