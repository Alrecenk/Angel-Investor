
public class Breakthrough extends Card{

  
  
  
public static final String BREAKTHROUGH_NAME = "Breakthrough";

public Breakthrough(){
  name = BREAKTHROUGH_NAME;
}

//TODO consider bloat rule

//Modifies the game state when this card is spent by the given player
public void spend(Player player, Game game, Event e){
  
  int which_row = e.chooseProjectforEffect(player, this, game);
  StartUp s = game.start_ups.get(which_row);
  e.addChoice(s.flip(2, game));
}
 
//Modifies the game state when this card is invested by the given player.
public void invest(Player player, int row, Game game, Event e){
  StartUp s = game.start_ups.get(row);
  e.addChoice(s.flip(1, game));
  
}

//Modifies the game state when this card is flipped on the given player's turn.
public void flip(Player player, int row, Game game, Event e){
  StartUp s = game.start_ups.get(row);
  e.addChoice(s.flip(3, game));
  
}
  


//Prints a SpendCard event for the this card having just been spent.
//Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player player, Game game, SpendCard e) {
  int row = e.readChoice();
  int flipped = e.readChoice();
  //TODO use deck print range.
  Deck p = game.start_ups.get(row).project;
  String s = player.getName() + " spent Breakthrough on row " + row +" and flipped ";
  if(flipped ==0){
    s+="nothing.";
  }else if(flipped == 1){
    s+= p.getCard(p.size()-1).name+".";
  }else if( flipped == 2){
    s+= p.getCard(p.size()-1).name + " and " + p.getCard(p.size()-2).name + ".";
  }else{
    s+= "an illegal number of cards.";
  }
  return s;
}

//Prints an InvestCard event for this card having just been invested and executed.
//Note: card will now be at end of project.
public String investPrint(Player player, int which_row, Game game, InvestCard e) {
  int flipped = e.readChoice();
  Deck p = game.start_ups.get(which_row).project;
  String s = player.getName() + " invested Breakthrough on row " + which_row +" and flipped ";
  if(flipped ==0){
    s+="nothing.";
  }else if(flipped == 1){
    s+= p.getCard(p.size()-1).name+".";
  }
  return s;
}

//Prints a FlipCard Event for this card having just executed its flip command.
public String flipPrint(Player current, int which_row, Game game, FlipCard e){
  int flipped = e.readChoice();
  Deck p = game.start_ups.get(which_row).project;
  String s = "Breakthrough flipped on row " + which_row +" and flipped ";
  if(flipped ==0){
    s+="nothing else.";
  }else if(flipped == 1){
    s+= p.getCard(p.size()-1).name+".";
  }else if( flipped == 2){
    s+= p.getCard(p.size()-1).name + " and " + p.getCard(p.size()-2).name + ".";
  }else if(flipped == 3){
    s+= p.getCard(p.size()-1).name + ", "+ p.getCard(p.size()-2).name + ", and " + p.getCard(p.size()-3).name + ".";
  }else{
    s+= "an illegal number of cards.";
  }
  return s;
}
  
  
  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Breakthrough();
  }

}
