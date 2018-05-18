
public class CompleteCard extends Event{


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
    Card c = startup.project.removeFirst();
    if(p!=null){
      c.complete(p, which_row, game, this);
    }
    if(spinoff){
      game.start_ups.get(game.start_ups.size()-1).discard.add(c); // assumes start up for spin-off has bee created already.
    }else{
      startup.discard.add(c);
    }
  }

  public String print(Game game) {
    resetReadPointer(); 
    int which_row = readChoice();
    Player p = game.getPlayer(readChoice());
    boolean was_spinoff = (readChoice() == 1);
    Card c ;
    if(was_spinoff){
      int spin_off_row = game.start_ups.size()-1;
      StartUp spinoff = game.start_ups.get(spin_off_row);
      c = spinoff.discard.getCard(spinoff.discard.size()-1);
    }else{
      StartUp startup = game.start_ups.get(which_row);
      c = startup.discard.getCard(startup.discard.size()-1);
    }
    if(p !=null){
      return c.completePrint(p, which_row, game, this);
    }else{
      return c.name + " was discarded.";
    }
  }
  
  public Event copy() {
    resetReadPointer(); // Necessary when iterating over logs.
    int which_row = readChoice();
    int winner = readChoice();
    boolean spinoff = (readChoice() == 1);
    return new CompleteCard(which_row, winner, spinoff);
  }

}
