
public class DrawPhase extends Event{

  public DrawPhase(){}
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    Player p = game.getPlayer(game.getTurn());
    Card c1 = game.drawCard(); // First card always come from main deck.
    if(c1 == null){
      game.endGame();
    }else{
      p.add(c1);
      int draw_select = selectDrawLocation(p, game); // Player picks second card draw.
      Card c2 = null;
      if(draw_select == Player.CASH_RESERVE){
        c2 = p.drawMoney();
      }else if(draw_select == Player.MAIN_DECK){
        c2 = game.drawCard();
      }else{
        System.err.println("Player selected to draw from an invalid location.");
      }
      if(c2 == null){
        game.endGame(); // Game ends if player attempts to draw card that isn't there.
      }else{
        p.add(c2);
      }
    }

  }

  public String print(Game game) {
    Player p = game.getPlayer(game.getTurn());
    int cards = p.getHand().size();
    return p.getName() + " drew " + p.getHand().getCard(cards-2).name +" and " + p.getHand().getCard(cards-1).name +".";
  }
  
  public Event copy() {
    return new DrawPhase();
  }

}
