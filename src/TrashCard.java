import java.util.ArrayList;


public class TrashCard extends Event{

  Card card;
  int trasher;
  public TrashCard(Card c, int p){
    card = c;
    trasher = p;
  }
  
    
  public void execute(Game game) {
    card.trash(game.getPlayer(trasher), game, this);
    
  }

  public String print(Game game) {
    // TODO Auto-generated method stub
    return card.trashPrint(game.getPlayer(trasher), game, this);
  }

  public Event copy() {
    return new TrashCard(card.copy(),trasher);
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
