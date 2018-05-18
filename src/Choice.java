import java.util.ArrayList;

// Holds a set of ordered player choices for a given event.
public class Choice {
  ArrayList<int[]> selection; // Player choices required for an event
  int pointer ; // current location in the choice list.
  
  public Choice(){
    selection = new ArrayList<int[]>();
    pointer = 0 ;
  }
  
  public void resetReadPointer(){
    pointer = 0 ;
  }
  
//next next choice in array or null if past end of choices.
  public int[] readChoiceArray(){
    if(pointer >= selection.size() ){
      return null;
    }else{
      int c[] = selection.get(pointer);
      pointer++;
      return c;
    }
  }
  
  // next single int choice or min_value if past end of choices.
  public int readChoice(){
    if(pointer >= selection.size() ){
      return Integer.MIN_VALUE;
    }else{
      int c[] = selection.get(pointer);
      pointer++;
      return c[0];
    }
  }
  
  public void addChoiceArray(int c[]){
    selection.add(c);
  }
  
  public void addChoice(int c){
    selection.add(new int[]{c});
  }
  
  public String toString(){
    String s  = "";
    for(int k=0;k<selection.size();k++){
      s+="[";
      int c[] = selection.get(k);
      for(int j=0;j<c.length;j++){
        s+=c[j] +",";
      }
      s+="]";
    }
    return s ;
  }
}
