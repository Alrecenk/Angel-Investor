
public class Rush extends Card{

  public static final String RUSH_NAME = "Rush";

  
  public Rush(){
    name = RUSH_NAME;
  }

  //Modifies the game state when this card is spent by the given player
  public void executeRush(Player player, int row, Game game, Event e){
    int winner = game.getProjectWinner(row);
    e.addChoice(winner);
    game.beginSpecialCompletion(row, winner, false);
  }
  
  
  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int selected_row = e.chooseProjectforEffect(player, this, game);
    executeRush(player, selected_row, game, e);
  }
   
 //Modifies the game state when this card is invested by the given player.
  public void invest(Player player, int row, Game game, Event e){
    e.addChoice(row);
    executeRush(player, row, game, e);
  }
  
 //Modifies the game state when this card is flipped on the given player's turn.
 public void flip(Player player, int row, Game game, Event e){
   e.addChoice(row);
   executeRush(player, row, game, e);
 }
  

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int row = e.readChoice();
    int winner = e.readChoice();
    String s = p.getName() + " spent Rush, completing row " + row +" with ";
    if(winner == Game.NOONE){
      s += "no" ;
    }else{
      s += game.getPlayer(winner).getName() + " as the";
    }
    s+= " winner.";
    return s ;
  }
  
  
//Prints an InvestCard event for this card having just been invested and executed.
//Note: card will now be at end of project.
public String investPrint(Player p, int which_row, Game game, InvestCard e) {
  
  int row = e.readChoice();
  int winner = e.readChoice();
  String s = p.getName() + " invested Rush, completing row " + row +" with ";
  if(winner == Game.NOONE){
    s += "no" ;
  }else{
    s += game.getPlayer(winner).getName() + " as the";
  }
  s+= " winner.";
  return s ;
}


// Prints a FlipCard Event for this card having just executed its flip command.
public String flipPrint(Player current, int which_row, Game game, FlipCard e){
  int row = e.readChoice();
  int winner = e.readChoice();
  String s = "Rush flipped, completing row " + row +" with ";
  if(winner == Game.NOONE){
    s += "no" ;
  }else{
    s += game.getPlayer(winner).getName() + " as the";
  }
  s+= " winner.";
  return s ;
}



  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Rush();
  }

}
