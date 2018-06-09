import java.util.ArrayList;


public class InvestCard extends Event{

  Card card;

  // Card location in hand.
  public InvestCard(int which_card, int which_row){
    addChoice(which_card);
    addChoice(which_row);
  }

  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    Player p = game.getPlayer(game.getTurn());
    int which_card = readChoice();
    if(which_card >= 0 && which_card < p.getHand().size()){
      card = p.getHand().removeCard(which_card);
      int which_row = readChoice();
      StartUp row = game.start_ups.get(which_row);
      row.project.add(card);
      card.invest(p, which_row, game, this);
    }
  }

  public String print(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    Player p = game.getPlayer(game.getTurn());
    int which_card = readChoice(); // Not useful here but need to read it to get the next value.
    int which_row = readChoice();
    if(card !=null){
      return card.investPrint(p, which_row, game, this);
    }else{
      return null;
    }
  }
  
  public Event copy() {
    resetReadPointer(); // Necessary when iterating over logs.
    int which_card = readChoice(); // Not useful here but need to read it to get the next value.
    int which_row = readChoice();
    return new InvestCard(which_card, which_row);
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
