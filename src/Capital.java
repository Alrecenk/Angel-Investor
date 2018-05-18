
public class Capital extends Card{
  
  public static final String CAPITAL_NAME = "Capital";
  
  public Capital(){
    this.name = CAPITAL_NAME;
  }

  public void spend(Player player, Game game, Event e) {
    Card c = game.drawCard();
    if(c == null){
      game.endGame();
    } else {
      player.add(c);
    }
  }


  public void complete(Player winner, int row, Game game, Event e) {
    Card c = game.drawCard();
    if(c == null){
      game.endGame();
    } else {
      winner.add(c);
    }
  }


  public boolean canBeStartingCard() {
    return true;
  }

  public Card copy() {
    return new Capital();
  }

  public double endGamePoints() {
    return 0;
  }
  
  
//Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is "after" the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    String s = p.getName() +" spent a Capital";
    if(game.game_over){
      s+= ", ending the game.";
    }else{
      s+= " and drew a " + p.getHand().getCard(p.getHand().size()-1).name + ".";
    }
    return s;
  }
  
  //Prints a CompleteCard event for the this card having just been completed
  //Note: card will now be on top of startup.discard, since game is state after the event.
  public String completePrint(Player winner, int startup, Game game, CompleteCard e) {
    String s = winner.getName() +" completed a Capital";
    if(game.game_over && game.main_deck.size() == 0){
      s+= ", ending the game.";
    }else{
      s+= " and drew a " + winner.getHand().getCard(winner.getHand().size()-1).name + ".";
    }
    return s;
  }

}
