import java.util.ArrayList;


public class CompleteCard extends Event{

  
  Card completed_card = null;

  public CompleteCard(int startup, int winner){
    addChoice(startup);
    addChoice(winner);
    addChoice(0); // Spinoff
  }
  
  public CompleteCard(int startup, int winner, boolean spinoff){
    addChoice(startup);
    addChoice(winner);
    addChoice(spinoff ? 1:0);
  }
    
  public void execute(Game game) {
    resetReadPointer(); 
    int which_row = readChoice();
    StartUp startup = game.start_ups.get(which_row);
    Player p = game.getPlayer(readChoice());
    boolean spinoff = (readChoice() == 1);
    completed_card = startup.project.removeFirst();
    if(p!=null){
      completed_card.complete(p, which_row, game, this);
    }
    if(spinoff){
      game.start_ups.get(game.start_ups.size()-1).discard.add(completed_card); // assumes start up for spin-off has bee created already.
    }else{
      startup.discard.add(completed_card);
    }
  }

  public String print(Game game) {
    resetReadPointer(); 
    int which_row = readChoice();
    Player p = game.getPlayer(readChoice());
    boolean was_spinoff = (readChoice() == 1);
    if(p !=null){
      return completed_card.completePrint(p, which_row, game, this);
    }else{
      return completed_card.name + " was discarded.";
    }
  }
  
  public Card getJustCompletedCard(){
    return completed_card ;
  }
  
  public Event copy() {
    int which_row = readChoice();
    int winner = readChoice();
    boolean spinoff = (readChoice() == 1);
    CompleteCard cc = new CompleteCard(which_row, winner, spinoff);
    cc.completed_card = completed_card;
    return cc;
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
