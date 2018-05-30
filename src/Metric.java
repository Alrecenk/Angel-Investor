// Whenever a metric is attached to a game it's measure function will be called after each Event (much the same way as print).
// One metric may be used across many games to accumulate a statistic. Many metrics may be attached to single game to analyze many things.


public abstract class Metric {

  public abstract void measure(Event e, Game game);
  
}
