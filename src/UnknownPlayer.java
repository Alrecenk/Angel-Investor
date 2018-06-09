/* This Player represents a stand-in for players unknown to other players when making choices. 
 * All players will be replaced by these when passed to AI for choices.
 * If a deck is given, the cards in hand will be replaced by UnknownCards, except for money which will be as normal as it's countable in game.
 * This also hides the opponents AI from any entity using a simulated game for choice making.
 * Choices should never be requested of unknown players. Instead simulated games should be run from logs.
 */


public class UnknownPlayer extends Player {

  
  // Sanitizes hand and uses the call_black player when asked for choices.
  public UnknownPlayer(Player source, Deck all_unknown_cards){
    hand = new Deck();
    for(int k=0;k<source.hand.size();k++){
      Card c = source.hand.getCard(k);
      if(c instanceof Money){
        hand.add(c.copy());
      }else{
        hand.add(new UnknownCard(all_unknown_cards, Player.HAND, source.getPlayerNumber()));
      }
    }
    
    reserve = source.reserve; // Amount of money in player's reserve.
    fame = source.getFame() ; // Current score of player.
    player_number  = source.getPlayerNumber(); // Player's position on the table and money type.
  }
  
  public int chooseFlip(Game game) {
    System.err.println("Unknown Player requested choice.");
    return 0;
  }

  public int selectDrawLocation(Game game) {
    System.err.println("Unknown Player requested choice.");
    return 0;
  }

  public int chooseProjectforEffect(Card c, Game game) {
    System.err.println("Unknown Player requested choice.");
    return 0;
  }

  public int[] chooseProjectCardforEffect(Card c, Game game) {
    System.err.println("Unknown Player requested choice.");
    return null;
  }

  public int chooseDeckforEffect(Card c, Game game) {
    System.err.println("Unknown Player requested choice.");
    return 0;
  }

  public int choosePlayerforEffect(Card c, Game game) {
    System.err.println("Unknown Player requested choice.");
    return 0;
  }

  public int trashCard(Card c, int location, Game game) {
    System.err.println("Unknown Player requested choice.");
    return 0;
  }

  public int[] reorderOrDiscard(Deck c, int location, Game game) {
    System.err.println("Unknown Player requested choice.");
    return null;
  }

  public String getStatus() {
    return "Wishing to be a real goat.";
  }


  public Player copy() {
    return this;
  }

  public int[] choosePlay(Game game) {
    System.err.println("Unknown Player requested choice.");
    return null;
  }

}
