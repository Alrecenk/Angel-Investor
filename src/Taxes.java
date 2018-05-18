
public class Taxes extends Card{
  
  public static final String TAXES_NAME = "Taxes";
  
  public Taxes(){
    name = TAXES_NAME ;
  }
  
  public void executeTaxes(Game game, Event e){
    for(int k=0;k<game.players.size();k++){
      for(int j=0;j<2;j++){
        Card m = game.players.get(k).drawMoney();
        if(m == null){
          game.endGame();
        } else {
          game.trash_pile.add(m);
        }
      }
    }
  }
  
//Modifies the game state when this card is spent by the given player
public void spend(Player player, Game game, Event e){
  executeTaxes(game, e);
}
 


//Modifies the game state if this card were trashed.
public void trash(Player trasher, Game game, Event e){
  executeTaxes(game, e);
}



//Prints a SpendCard event for the this card having just been spent.
//Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player p, Game game, SpendCard e) {
  if(game.game_over){
    return p.getName() +" spent Taxes and ended the game.";
  }else{
    return p.getName() + " spent Taxes and trashed 2 money from every player's reserve.";
  }
}

//Prints a TrashCard Event for this card having just been trashed.
//Note: card will now be on top of trash.
//This is only if trashing the card triggered something. The trashing card will say what it trashed in its event print. 
public String trashPrint(Player trasher, Game game, TrashCard trashCard) {
  if(game.game_over){
    return trasher.getName() +" trashed Taxes and ended the game.";
  }else{
    return trasher.getName() + " trashed Taxes which trashed 2 money from every player's reserve.";
  }
}
  

  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Taxes();
  }

}
