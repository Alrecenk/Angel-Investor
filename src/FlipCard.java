// An event that occurs because of an F trigger on a card.
public class FlipCard extends Event{

  Card card;
  int row;
  
  public FlipCard(Card c, int start_up){
    card = c ;
    row = start_up;
  }
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    if(game.start_ups.get(row).project.contains(card)){ // Make sure it hasn't been destroyed
      addChoice(1); // Card did get to execute.
      Player p = game.getPlayer(game.getTurn());
      card.flip(p, row, game, this);
    } else {
      addChoice(0); // Card did not get to execute.
    }
  }

  public String print(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    int did_play = readChoice();
    if(did_play == 1){
      return card.flipPrint(game.getPlayer(game.getTurn()), row, game, this);
    }else{
      return card.name + " flip action was skipped because it is no longer in play.";
    }
  }

  
  public Event copy() {
    return new FlipCard(card.copy(), row);
  }
}
