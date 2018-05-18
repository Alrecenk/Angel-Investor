
public class PressRelease extends Card{

  public static final String PRESS_RELEASE_NAME = "Press Release";
  
  public PressRelease(){
    this.name = PRESS_RELEASE_NAME;
  }
  
  public void complete(Player winner, int row, Game game, Event e) {
    winner.giveFame();
  }
  
//Prints a CompleteCard event for the this card having just been completed
//Note: card will now be on top of startup.discard, since game is state after the event.
public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
  return winner.getName() + " completed a Press Release and received a fame (now has " + winner.getFame() +").";
}

  public boolean canBeStartingCard() {
    return true;
  }

  public Card copy() {
    return new PressRelease();
  }

  public double endGamePoints() {
    return 1;
  }

}
