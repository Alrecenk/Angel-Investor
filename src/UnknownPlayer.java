/* This card represents a stand-in for players unknown to other players when making choices. 
 * Other players will be replaced by these when passed to AI for choices.
 * The cards in hand will be replaced by UnknownCards, except for money which will be as normal as it's countable in game.
 * This also hides the opponents AI from any entity using a simulated game for choice making.
 * Instead of the opponent AI acting any choices given to an opponent will call back to the real player making the choice.
 */


public class UnknownPlayer extends Player {

  Player call_back ;
  
  public UnknownPlayer(Player source, Deck all_unknown_cards, Player call_back){
    hand = new Deck();
    for(int k=0;k<source.hand.size();k++){
      Card c = source.hand.getCard(k);
      if(c instanceof Money){
        hand.add(c.copy());
      }else{
        hand.add(new UnknownCard(all_unknown_cards, Player.HAND, source.getPlayerNumber(), call_back));
      }
    }
    
    reserve = source.reserve; // Amount of money in player's reserve.
    fame = source.getFame() ; // Current score of player.
    player_number  = source.getPlayerNumber(); // Player's position on the table and money type.
    this.call_back = call_back ;
  }

  //TODO implement callbacks
  public int chooseFlip(Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int selectDrawLocation(Game game) {
    // TODO Auto-generated method stub
    return 0;
  }

  public Event makePlay(Game game) {
    // TODO Auto-generated method stub
    return null;
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
    return "Wishing to be a real goat.";
  }


  public Player copy() {
    return this;
  }

}
