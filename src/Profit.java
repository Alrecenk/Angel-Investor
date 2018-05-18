
public class Profit extends Card {

  public static final String PROFIT_NAME = "Profit";

  public Profit(){
    this.name = PROFIT_NAME;
  }

  public void spend(Player player, Game game, Event e) {
    Card m = player.drawMoney();
    if(m == null){
      game.endGame();
    } else {
      player.add(m);
    }
  }

  public void complete(Player winner, int row, Game game, Event e) {
    Card m = winner.drawMoney();
    if(m == null){
      game.endGame();
    } else {
      winner.add(m);
    }
  }

  public boolean canBeStartingCard() {
    return true;
  }

  public Card copy() {
    return new Profit();
  }

  public double endGamePoints() {
    return 0;
  }


  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is "after" the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    String s = p.getName() +" spent a Profit";
    if(game.game_over){
      s+= ", ending the game.";
    }else{
      s+= " and drew a money from their reserve(" + p.reserve +" remaining).";
    }
    return s;
  }
  
//Prints a CompleteCard event for the this card having just been completed
//Note: card will now be on top of startup.discard, since game is state after the event.
public String completePrint(Player winner, int startup, Game game, CompleteCard e) {
  String s = winner.getName() +" completed a Profit";
  if(game.game_over && winner.reserve == 0){
    s+= ", ending the game.";
  }else{
    s+= " and drew a money from their reserve(" + winner.reserve +" remaining).";
  }
  return s;
}
}