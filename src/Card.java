// A card is a single instance of a specific card at a location in the game.
public abstract class Card {
  
  public String name; // A String that refers to the name of this card.
  
 // Modifies the game state when this card is spent by the given player
 public void spend(Player player, Game game, Event e){}
  
//Modifies the game state when this card is invested by the given player.
 public void invest(Player player, int row, Game game, Event e){}
 
//Modifies the game state when this card is flipped on the given player's turn.
public void flip(Player player, int row, Game game, Event e){}

//Modifies the game state when this card is completed in a project. winner is the winner of that project
public void complete(Player winner, int row, Game game, Event e){}

//Modifies the game state if this card were trashed.
public void trash(Player trasher, Game game, Event e){}

//Modifies the game state if this card were pushed onto special phase stack with pushSpecialCardPhase.
// This function will be called from a CardEvent when returning from a special phase or after the event queue has been cleared.
public void special(Player activater, Game game, Event e){
  System.err.println("Special phase called on card that does not have a special phase.");
}

// returns whether this card is valid for a beginning game start-up project (i.e. it has a flip or complete action on it).
public abstract boolean canBeStartingCard();

// How many points this card is worth if it's in a player's hand at the end of the game.
public abstract double endGamePoints();

// Returns a non-shallow copy of this card.
public abstract Card copy();

// Prints a SpendCard event for the this card having just been spent.
// Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player p, Game game, SpendCard e) {
  return p.getName() + " spent " + this.name +", which did nothing.";
}

//Prints an InvestCard event for this card having just been invested and executed.
//Note: card will now be at end of project.
public String investPrint(Player p, int which_row, Game game, InvestCard e) {
return p.getName() + " invested " + this.name +" in project " + which_row + ".";
}

//Prints a CompleteCard event for the this card having just been completed
//Note: card will now be on top of startup.discard, since game is state after the event.
public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
  return name + " was discarded.";
}

// Prints a FlipCard Event for this card having just executed its flip command.
public String flipPrint(Player current, int which_row, Game game, FlipCard e){
  return null ;//name +" flip action triggered with no effect."; 
}

//Prints a TrashCard Event for this card having just been trashed.
//Note: card will now be on top of trash.
//This is only if trashing the card triggered something. The trashing card will say what it trashed in its event print. 
public String trashPrint(Player trasher, Game game, TrashCard e) {
  return null ; 
}

public String specialPrint(Player p, Game game, CardEvent e) {
  return null;
}
  

}


