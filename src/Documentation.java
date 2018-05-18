
public class Documentation extends Card{

  public static final String DOCUMENTATION_NAME = "Documentation";
  
  public Documentation(){
    name = DOCUMENTATION_NAME;
  }
  

  // Modifies the game state when this card is spent by the given player.
  public void spend(Player player, Game game, Event e){
    game.skip_next_completion[player.getPlayerNumber()] = true;
  }

 //Modifies the game state when this card is completed in a project. winner is the winner of that project
 public void complete(Player winner, int row, Game game, Event e){
   game.flip_after_completion[row] += 3;
 }

 //Modifies the game state if this card were trashed.
 public void trash(Player trasher, Game game, Event e){
   game.skip_next_completion[trasher.getPlayerNumber()] = true;
 }
 

//Prints a SpendCard event for the this card having just been spent.
//Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player p, Game game, SpendCard e) {
return p.getName() + " spent Documentaion and will skip their next completion.";
}


//Prints a CompleteCard event for the this card having just been completed
//Note: card will now be on top of startup.discard, since game is state after the event.
public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
  if(game.phase == Game.COMPLETE){
    return "Documentation triggered, will attempt to draw 3 more cards after completion.";
  }else{
    return "Documentation completed by card effect, will not trigger.";
  }
}

//Prints a TrashCard Event for this card having just been trashed.
//Note: card will now be on top of trash.
//This is only if trashing the card triggered something. The trashing card will say what it trashed in its event print. 
public String trashPrint(Player trasher, Game game, TrashCard trashCard) {
  return trasher.getName() + " trashed a Documentation and will skip their next completion.";
}
  
  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Documentation();
  }

}
