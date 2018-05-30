
public class DrawPhase extends Event{

  public DrawPhase(){}
  
  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    Player p = game.getPlayer(game.getTurn());
    Card c1 = game.drawCard(); // First card always come from main deck.
    int drawn = 0 ;
    if(c1 == null){
      game.endGame();
      addChoice(Player.DECK_INDEX);
    }else{
      p.add(c1);
      drawn++;
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
        drawn++;
      }
    }
    addChoice(drawn);
  }

  public String print(Game game) {
    resetReadPointer();
    int draw_select = readChoice();
    int drawn = readChoice();
    Player p = game.getPlayer(game.getTurn());
    int cards = p.getHand().size();
    String s  = p.getName() + " drew " + p.getHand().printRange(cards-drawn, drawn);
    if(game.game_over){
      s += " and ended the game on their draw phase" ;
    }
    s+= ".";
    return s ;
  }
  
  public Event copy() {
    return new DrawPhase();
  }

}
