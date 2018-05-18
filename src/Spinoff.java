
public class Spinoff extends Card{

  public static final String SPINOFF_NAME = "Spin-off";

  public Spinoff(){
    name = SPINOFF_NAME;
  }

  //Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int source_row = e.chooseProjectforEffect(player, this, game);
    int winner = game.getProjectWinner(source_row);
    e.addChoice(winner);
    game.start_ups.add(new StartUp());
    game.beginSpecialCompletion(source_row, winner, true);
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int source_row = e.readChoice();
    int winner = e.readChoice();
    String s = p.getName() + " spent Spinoff, completing row " + source_row +" with ";
    if(winner == Game.NOONE){
      s += "no" ;
    }else{
      s += game.getPlayer(winner).getName() + " as the";
    }
    s+= " winner. Cards will be discarded to row " + (game.start_ups.size()-1) +".";
    return s ;
  }


  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Spinoff();
  }

}
