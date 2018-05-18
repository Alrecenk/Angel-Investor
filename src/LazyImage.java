import java.awt.image.BufferedImage;

//An image that may or may not be loaded.
public class LazyImage implements Runnable{
  
  public static boolean logging_enabled = false;
  
  public final String source ;
  private BufferedImage source_image ;
  private BufferedImage result_image;
  private double scale = 1 ;
  private int source_width, source_height ;
  private int result_width, result_height ; 
  long last_access = System.currentTimeMillis();
  long bytes_used = 0;
  public boolean running = false;


  public LazyImage(String path){
    source = path ;
    source_width = -1;
    source_height = -1;
    result_width = 0 ;
    result_height = 0;
  }

  //If called before requesting then do not block.
  public void prepare(double scale){
    if(result_image == null || this.scale !=scale){ // Don't even start if we're already loaded.
      this.scale = scale;

      Thread t = new Thread(this);
      t.start();
    }
    last_access = System.currentTimeMillis();
  }

  public void prepare(){
    prepare(1);
  }

  public boolean loaded(){
    return source_image != null;
  }

  public long getLastAccess(){
    return last_access;
  }

  public long getBytesUsed(){
    return bytes_used ;
  }

  public void unload(){
    if(source_image!=null){
      source_image.flush();
      source_image = null;
    }
    if(result_image != null){
      result_image.flush();
      result_image = null;
    }
    bytes_used = 0 ;
    if(logging_enabled){
      System.out.println("Unloading " + source);
    }
  }

  public synchronized void run(){
    running = true;
    last_access = System.currentTimeMillis();
    String action = "";
    long time = System.currentTimeMillis();
    // Check if we have result first. As we may have unloaded source for memory.
    result_width = (int)(source_width*scale);
    result_height = (int)(source_height*scale);
    if(result_image == null || result_image.getWidth() != result_width){
      // If need to load image from disk.
      if(source_image == null){ 
        if(logging_enabled){
          System.out.println("Loading " + source);
        }
        source_image = ImageUtility.loadimage(source) ;
        source_width = source_image.getWidth();
        source_height = source_image.getHeight();
      }
      bytes_used = source_width*source_height*4 ;
      time = System.currentTimeMillis();
      // Scale
      result_image = null; // Wipe previous image, so it won't be shown while we're building the new one.
      if(scale > 1){ // Use bilinear if scaling up.
        result_image = ImageUtility.scaleTo(source_image, result_width, result_height);
        action +="bilinear upscale";
        bytes_used += result_width*result_height;
      } else if( scale < 1){ // Us pixel integration if scaling down.
        if(scale < .25){ // For very large photos
          action = "multi-sample downscale";
          // use a simpler downscale that antiaiases by multisampling.
          result_image =  ImageUtility.multiSampleScaleTo(source_image, result_width, result_height);
          source_image = null; // Unload the source as soon as we get the result to save memory.
          bytes_used -= source_width*source_height ;
        }else{
          action += "integration downscale";
          if(result_width >0){
            result_image = ImageUtility.makethumbnail(source_image, result_width, result_height);
          }
        }
        bytes_used += result_width*result_height;
      } else{ // If no scaling
        result_image = source_image;
        result_width = source_width;
        result_height = source_height;
      }
    }
    running = false;
    if(action.length() > 0 && logging_enabled){ // Only print time if we did something. This function is also used to synchronously wait.
      System.out.println(action + " in " + (System.currentTimeMillis() - time) +"ms ( " + source +")");
    }
  }

  public boolean ready(){
    return !running;
  }

  public BufferedImage getImage(double scale){
    last_access = System.currentTimeMillis();
    if(scale == 1){
      if(source_image == null){
        run();
      }
      return source_image;
    }else if(result_image == null || this.scale !=scale){
      this.scale = scale;
      run(); // If requesting image and it isn't there then block.
      return result_image ;
    }else{
      return result_image;
    }
  }

  public BufferedImage getImage(){
    return getImage(scale);
  }

  public int getSourceWidth(){
    // If you try to get dimensions on an unloaded image it will load and block.
    if(source_image == null){
      run();
    }
    return source_width;
  }
  public int getSourceHeight(){
    // If you try to get dimensions on an unloaded image it will load and block.
    if(source_image == null){
      run();
    }
    return source_height ;
  }

  public int getWidth(){
    // If you try to get dimensions on an unloaded image it will load and block.
    if(result_image == null){
      run();
    }
    return result_width;
  }

  public int getHeight(){
    // If you try to get dimensions on an unloaded image it will load and block.
    if(result_image == null){
      run();
    }
    return result_height;
  }

}