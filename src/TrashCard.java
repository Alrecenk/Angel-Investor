
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
}
