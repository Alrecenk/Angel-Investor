
public class Delay extends Card{

  public static final String DELAY_NAME = "Delay";
  
  public Delay(){
    name = DELAY_NAME;
  }
  
  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int total_project_cards = 0 ;
    for(int k=0;k<game.start_ups.size();k++){
      total_project_cards+= game.start_ups.get(k).project.size();
    }
    if(total_project_cards > 0){ // Don't make player choose if it's impossible.
      int project_card[] = e.chooseProjectCardforEffect(player, this, game);
      int project = project_card[0], card = project_card[1];
      if(project < game.start_ups.size()){
        Deck s = game.start_ups.get(project).project;
        if(card < s.size()){
          Card c = s.removeCard(card);
          game.start_ups.get(project).discard.add(c);
        }
      }
    }
  }
  
//Prints a SpendCard event for the this card having just been spent.
//Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player p, Game game, SpendCard e) {
  int project_card[] = e.readChoiceArray();
  if(project_card == null){
    return p.getName() + " spent Delay but there were no cards to discard.";
  }else{
    int project = project_card[0];
    Deck discard = game.start_ups.get(project).discard;
    // Sabotage is on top after spending, so the card is trashed it right under that.
    return p.getName() + " spent Delay and discarded " + discard.getCard(discard.size()-1).name + " in row " + project +".";
  }
}
  
  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Delay();
  }

}
