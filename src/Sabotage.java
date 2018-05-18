
public class Sabotage extends Card{

  public static final String SABOTAGE_NAME = "Sabotage";

  public Sabotage(){
    name = SABOTAGE_NAME;
  }

  public void trashProjectCard(Player p, Game game, Event e){

    int total_project_cards = 0 ;
    for(int k=0;k<game.start_ups.size();k++){
      total_project_cards+= game.start_ups.get(k).project.size();
    }
    if(total_project_cards > 0){ // Don't make player choose if it's impossible.
      int project_card[] = e.chooseProjectCardforEffect(p, this, game);
      int which_project = project_card[0], which_card = project_card[1];
      if(which_project < game.start_ups.size()){
        Deck project = game.start_ups.get(which_project).project;
        if(which_card < project.size()){
          // Check for Lawyer interrupt.
          Deck lawyers = Lawyers.getLawyersProtecting(project);
          e.addChoice(lawyers.size()); // Log number of lawyers
          if(lawyers.size() > 0){ // There's a lawyer interrupt.
            for(int k=0;k<lawyers.size();k++){
              Card c = lawyers.getCard(k);
              project.removeCard(c);
              game.queueEvent(new TrashCard(c, p.getPlayerNumber()));
              game.trash_pile.add(c);
            }
          }else{ // No lawyers
            Card c = project.removeCard(which_card);
            game.queueEvent(new TrashCard(c, p.getPlayerNumber()));
            game.trash_pile.add(c);

          }
          //TODO consider bankruptcy rule
        }else{
          System.err.println(p.getName() + " attemped to trash card past end of project.");
        }
      }else{
        System.err.println(p.getName() + " attemped to trash card in a nonexistant project");
      }
    }
  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    trashProjectCard(player,game,e);

  }


  //Modifies the game state when this card is completed in a project. winner is the winner of that project
  public void complete(Player winner, int row, Game game, Event e){
    trashProjectCard(winner, game, e);
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int project_card[] = e.readChoiceArray();
    int lawyers = e.readChoice();
    if(project_card == null){
      return p.getName() + " spent Sabotage but there were no cards to trash.";
    }else if(lawyers > 0){
      return p.getName() + " spent Sabotage and trashed " + game.trash_pile.printRange(game.trash_pile.size()-1-lawyers, lawyers) + " in row " + project_card[0] +".";
    }else{
      // Sabotage is on top after spending, so the card it trashed it right under that.
      return p.getName() + " spent Sabotage and trashed " + game.trash_pile.getCard(game.trash_pile.size()-2).name + " in row " + project_card[0] +".";
    }
  }


  //Prints a CompleteCard event for the this card having just been completed
  //Note: card will now be on top of startup.discard, since game is state after the event.
  public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
    int project_card[] = e.readChoiceArray();
    int lawyers = e.readChoice();
    //TODO Fix bug: can print the wrong card was sabotaged if completed during a special complete triggered by a spent card.
    if(project_card == null){
      return winner.getName() + " completed Sabotage but there were no cards to trash.";
    }else if(lawyers > 0){
      return winner.getName() + " completed Sabotage and trashed " + lawyers + " Lawyers in row " + project_card[0] +".";
    }else{
      // Trashed card is on top of trash.
      return winner.getName() + " completed Sabotage and trashed " + game.trash_pile.getCard(game.trash_pile.size()-1).name + " in row " + project_card[0];
    }
  }

  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Sabotage();
  }

}
