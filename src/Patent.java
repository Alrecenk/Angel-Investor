
public class Patent extends Card {
  
  public static final String PATENT_NAME = "Patent";
  
  public Patent(){
    name = PATENT_NAME;
  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    //TODO cannot override under dog.
    int selected_row = e.chooseProjectforEffect(player, this, game);
    int winner = player.getPlayerNumber();
    game.beginSpecialCompletion(selected_row, winner, false);
  }
  
//Modifies the game state when this card is spent by the given player
  public void complete(Player winner, int row, Game game, Event e){
  //TODO cannot override under dog.
   int selected_row = e.chooseProjectforEffect(winner, this, game);
   while(selected_row == row){ // Can't pick the row patent is in.
     selected_row = e.chooseProjectforEffect(winner, this, game);
     //System.out.println("in complete loop patent");
   }
   game.beginSpecialCompletion(selected_row, winner.getPlayerNumber(), false);
 }
  
//Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int row = e.readChoice();
    return p.getName() + " spent Patent, completing row " + row +" with them as the winner.";
  }
  

//Prints a CompleteCard event for the this card having just been completed
//Note: card will now be on top of startup.discard, since game is state after the event.
public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
  int completed_row = e.readChoice();
  while(completed_row == which_row){
    completed_row = e.readChoice();
    //System.out.println("in print loop patent");
  }
  return winner.getName() + " completed Patent, completing row " + completed_row +" with them as the winner.";
}

  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Patent();
  }

}
