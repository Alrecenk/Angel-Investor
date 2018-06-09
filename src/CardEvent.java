import java.util.ArrayList;

//An event that is created by a card effect. These can be used to allow the game event queue to run during a card effect such as Boom or Open Source.
// Use pushSpecialCardPhase to create a CardEvent with a card that will be called after any other pushed special phases and once the event queue is cleared.


public class CardEvent extends Event {

  int caller ;
  Card queued_card;
  
  public CardEvent(Card c, int p){
    queued_card = c;
    caller = p ;
  }
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    queued_card.special(game.getPlayer(caller), game, this);
  }

  public String print(Game game) {
    return queued_card.specialPrint(game.getPlayer(caller), game, this);
  }

  public Event copy() {
    return new CardEvent(queued_card.copy(), caller);
  }

  public ArrayList<Choice> getPossibleChoices(Game game) {
    // TODO Auto-generated method stub
    System.err.println("Unimplemented choice enumeration.");
    return null;
  }

  public int getUnknownCardLocation(Game game) {
    // TODO Auto-generated method stub
    System.err.println("Unimplemented choice enumeration.");
    return 0;
  }

  public int getChoosingPlayer(Game game) {
    // TODO Auto-generated method stub
    System.err.println("Unimplemented choice enumeration.");
    return 0;
  }

}
