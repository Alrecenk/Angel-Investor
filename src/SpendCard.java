
public class SpendCard extends Event{

  Card spent_card ; //Just used to save the card for printing in case it moves (Infamy).
  
  public SpendCard(int which_card){
    addChoice(which_card);
  }
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    Player p = game.getPlayer(game.getTurn());
    int index = readChoice();
    if(index >= 0 && index < p.getHand().size()){
      spent_card = p.getHand().removeCard(index);
      game.trash_pile.add(spent_card); // TODO order was changed to trash spent card before calling. Likely broke some prints of card trashing cards.
      spent_card.spend(p, game, this);
    }
  }

  public String print(Game game) {
    Player p = game.getPlayer(game.getTurn());
    if(spent_card != null){
      return spent_card.spendPrint(p, game, this);
    }else{
      return null;
    }
  }
  
  public Event copy() {
    resetReadPointer();
    return new SpendCard(readChoice());
  }

}
