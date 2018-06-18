
public class Infamy extends Card {

  public static final String INFAMY_NAME = "Infamy";
  
  public Infamy(){
    name = INFAMY_NAME;
  }
  
  public void moveCard(Player player, Game game, Event e){
    int least_fame = Integer.MAX_VALUE;
    int least_player = Game.NOONE;
    for(int k=0;k<game.players.size();k++){
      int fame = game.getPlayer(k).getFame();
      if(fame < least_fame){
        least_fame = fame;
        least_player = k ;
      }else if(fame == least_fame){
        least_player = Game.NOONE;
      }
    }
    if(least_player == player.getPlayerNumber() && e instanceof SpendCard){
      least_player = Game.NOONE;
    }
    e.addChoice(least_player);
    if(least_player != Game.NOONE){
      game.trash_pile.removeCard(this);
      game.players.get(least_player).add(this);
    }
  }
  
  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    player.giveFame();
    moveCard(player, game, e);
  }
   
 //Modifies the game state when this card is invested by the given player.
  public void invest(Player player, int row, Game game, Event e){
    //player.giveFame();
  }

 //Modifies the game state when this card is completed in a project. winner is the winner of that project
 public void complete(Player winner, int row, Game game, Event e){
   winner.giveFame();
 }

 //Modifies the game state if this card were trashed.
 public void trash(Player trasher, Game game, Event e){
   moveCard(trasher, game, e);
 }
 
 

//Prints a SpendCard event for the this card having just been spent.
//Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player p, Game game, SpendCard e) {
  int receiver = e.readChoice();
  if(receiver == Game.NOONE){
    return p.getName() + " spent Infamy and got a fame (now has " + p.getFame() +"). Infamy went to the trash.";
  }else{
    return p.getName() + " spent Infamy and got a fame (now has " + p.getFame() +"). Infamy went to the hand of " + game.getPlayer(receiver).getName() + ".";
  }
}

//Prints an InvestCard event for this card having just been invested and executed.
//Note: card will now be at end of project.
public String investPrint(Player p, int which_row, Game game, InvestCard e) {
return p.getName() + " invested Infamy in project " + which_row + ", and got a fame (now has " + p.getFame() +").";
}

//Prints a CompleteCard event for the this card having just been completed
//Note: card will now be on top of startup.discard, since game is state after the event.
public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
  return winner.getName() + " completed Infamy in project " + which_row + ", and got a fame (now has " + winner.getFame() +").";
}

//Prints a TrashCard Event for this card having just been trashed.
//This is only if trashing the card triggered something. The trashing card will say what it trashed in its event print. 
public String trashPrint(Player trasher, Game game, TrashCard e) {
  int receiver = e.readChoice();
  if(receiver == Game.NOONE){
    return trasher.getName() + " trashed Infamy. Noone received it.";
  }else{
    return trasher.getName() + " trashed Infamy, but it went to the hand of " + game.getPlayer(receiver).getName() + " instead.";
  }
}

  
  
  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 1;
  }

  public Card copy() {
    return new Infamy();
  }

}
