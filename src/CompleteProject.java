
public class CompleteProject extends Event{

  public CompleteProject(int which_startup){
    addChoice(which_startup);
  }
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    int which_startup = readChoice();
    game.beginCompletionPhase(which_startup);
  }

  public String print(Game game) {
    return null;
  }
  
  public Event copy() {
    resetReadPointer(); 
    int which_startup = readChoice();
    return new CompleteProject(which_startup);
  }

}
