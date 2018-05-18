// An event that occurs when a player is deciding which start-up to flip next.
public class SelectedFlip extends Event{
  
  boolean valid;
  public SelectedFlip(){}
  
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    int chosen_flip = chooseFlip(game.getPlayer(game.getTurn()),game);
    valid = !game.flipped[chosen_flip];
    game.flipPhaseFlip(chosen_flip);
    
  }

  public String print(Game game) {
    resetReadPointer();
    int chosen_flip = readChoice();
    Player p =  game.getPlayer(game.getTurn());
    if(!valid){
      return null ;//p.getName() + " attempted to flip " + chosen_flip +", but it's already been flipped.";
    }else{
      Deck project = game.start_ups.get(chosen_flip).project;
      return p.getName() + " flipped row " + chosen_flip +" and got " + project.getCard(project.size()-1).name +".";
    }
  }

  public Event copy() {
    return new SelectedFlip();
  }

}
