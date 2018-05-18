
public class Lawyers extends Card{

  public static final String LAWYERS_NAME = "Lawyers";

  public Lawyers(){
    name = LAWYERS_NAME;
  }

  public static Deck getLawyersProtecting(Deck project){
    Deck d = new Deck();
    for(int k=0;k<project.size();k++){
      Card c = project.getCard(k);
      if(c instanceof Lawyers){
        d.add(c);
      }
    }
    return d ;
  }
  
  public void executeLawyers(Player p, Game game, Event e){

    Card m = p.drawMoney();
    if(m == null){
      game.endGame();
    } else {
      game.trash_pile.add(m);
    }
  }

  //Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    executeLawyers(player, game, e);
  }



  //Modifies the game state if this card were trashed.
  public void trash(Player trasher, Game game, Event e){
    executeLawyers(trasher, game, e);
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    if(game.game_over){
      return p.getName() +" spent Lawyers and ended the game.";
    }else{
      return p.getName() + " spent Lawyers and trashed a money from their reserve.";
    }
  }

  //Prints a TrashCard Event for this card having just been trashed.
  //Note: card will now be on top of trash.
  //This is only if trashing the card triggered something. The trashing card will say what it trashed in its event print. 
  public String trashPrint(Player trasher, Game game, TrashCard trashCard) {
    if(game.game_over){
      return trasher.getName() +" trashed Lawyers and ended the game.";
    }else{
      return trasher.getName() + " trashed Lawyers which trashed a money from their reserve.";
    }
  }

  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    // TODO Auto-generated method stub
    return new Lawyers();
  }

}
