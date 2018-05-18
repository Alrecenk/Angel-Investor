/*
 * A utility class for loading, saving, and converting image formats
 * Matt McDaniel
 * Spring 2012
 */


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/* A class with various image utility functions
 * such as conversion between various formats
 */
public class ImageUtility {

	//loads an image from a file into BufferedImage
	//yes, it is loading into a regular image and then drawing to a buffered image
	//yes, that is stupid, but there doesn't seem to exist a better way to do it since ImageIO.read doesn't work reliably
	public static BufferedImage loadimage(String imagename){
		try{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage(imagename);
			MediaTracker mediaTracker = new MediaTracker(new JFrame());
			mediaTracker.addImage(image, 0);
			mediaTracker.waitForID(0);
			BufferedImage buf = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D bufImageGraphics = buf.createGraphics();
			bufImageGraphics.drawImage(image, 0, 0, null);
			return buf ;
		}catch(Exception e){
			System.out.println("Image Load Failed: " + e ) ;
			e.printStackTrace() ;
		}
		return null ;
	}
	
	
	// Loads an imag selected by the user with a File Chooser dialog.
	public static BufferedImage selectImage(Component app){
		JFileChooser chooser = new JFileChooser("./");
		int returnVal = chooser.showOpenDialog(app);
		String filename="" ;
		if(returnVal == JFileChooser.APPROVE_OPTION){
			filename= chooser.getSelectedFile().getPath()  ;
			String s[] = filename.split("\\.");
			String ext = s[s.length-1].toLowerCase();
			if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif") ){//load an image if this has an image extensiom
				return loadimage(filename) ;
			}
		}
		return null ;
	}

	//returns a measure of the brightness of an rgb pixel
	//there are several candidates for this function, it's largely arbitrary
	//note that the range should match the expected integers in the gsolve function
	public static int luminance(int c[]){
		return (c[0] *2 + c[1] * 3 + c[2])/6  ;
	}

	public static int luminance(byte c[]){
		return ((c[0]&0xff) *2 + (c[1]&0xff) * 3 + (c[2]&0xff))/6  ;
	}

	public static double luminance(double c[]){
		return (c[0] *2 + c[1] * 3 + c[2])/6  ;
	}

	//converts a bufferedimage image to 3 integer arrays
	//first index is x, then y, then channel
	public static byte[][][] convertimage(BufferedImage img) {
		int w= img.getWidth(null), h = img.getHeight(null) ;
		int[] pixels = new int[ w*h ];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels!");
		}
		byte data[][][] = new byte[w][h][3];
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				int k = x+y*w;
				data[x][y][0] =  (byte)((pixels[k]>>16)&0xff) ;
				data[x][y][1] =  (byte)((pixels[k]>>8)&0xff) ;
				data[x][y][2] =  (byte)((pixels[k])&0xff) ;
			}
		}
		return data;
	}

	//converts a set of 3 integer arrays back ito a bufferedimage
	public static BufferedImage convertimage(int i[][][]){
		int w = i.length,h=i[0].length ;
		int p[] = new int[w*h] ;
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				p[x+y*w] = (i[x][y][0]&0xff)<<16 | (i[x][y][1]&0xff) << 8 | (i[x][y][2]&0xff) ;

			}
		}
		return convertpixelstoimage(p,w);
	}

	//converts a set of 3 byte arrays back into a bufferedimage
	public static BufferedImage convertimage(byte i[][][]){
		int w = i.length,h=i[0].length ;
		int p[] = new int[w*h] ;
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				p[x+y*w] = (i[x][y][0]&0xff)<<16 | (i[x][y][1]&0xff) << 8 | (i[x][y][2]&0xff) ;

			}
		}
		return convertpixelstoimage(p,w);
	}

	//takes an ARGB int array and converts to a buffered image
	public static BufferedImage convertpixelstoimage(int p[], int w){
		BufferedImage image = new BufferedImage(w, p.length/w, BufferedImage.TYPE_INT_RGB);
		DataBufferInt databuffer = (DataBufferInt) image.getRaster().getDataBuffer();
		int[] ipix = databuffer.getData();
		for(int k=0;k<p.length;k++){
			ipix[k] = p[k] ;
		}
		return image ;
	}
	
	
	public static float[][][] convertimagebytetofloat(byte i[][][]){
		float f[][][] = new float[i.length][i[0].length][3];
		for(int x=0;x<i.length;x++){
			for(int y=0;y<i[0].length;y++){
				for(int c=0;c<3;c++){
					f[x][y][c] = (i[x][y][c]&0xff)/255.0f;
				}
			}
		}
		return f ;
	}
	
	public static byte[][][] convertimagefloattobyte(float f[][][]){
		byte i[][][] = new byte[f.length][f[0].length][3];
		for(int x=0;x<i.length;x++){
			for(int y=0;y<i[0].length;y++){
				for(int c=0;c<3;c++){
					i[x][y][c] = (byte)(f[x][y][c]*255.0f);
				}
			}
		}
		return i ;
	}


	//returns a one dimensional gaussian kernel
	public static double[] gaussian(double theta){
		int radius = (int)(theta*3) ;
		double g[] = new double[radius*2+1] ;
		double oneovertwothetasquared = 1/(2*theta*theta);
		double a = 1/ (theta * Math.sqrt(2*Math.PI) );
		for(int x=-radius;x<=radius;x++){
			g[x+radius] = a*Math.exp(-oneovertwothetasquared*x*x) ;

		}
		return g ;
	}

	//applies a one dimensional kernel in both directions
	public static float[][][] applyseperablekernel(float[][][] i, double kernel[]){
		float[][][] i2 = new float[i.length][i[0].length][i[0][0].length];
		int radius = (kernel.length - 1 )/2 ;
		for(int x=0;x<i.length;x++){
			for(int y=0;y<i[0].length;y++){
				for(int c = 0 ; c < i[0][0].length; c++){
					double v = 0;
					for(int k=-radius;k<=radius;k++){
						//for(int j=-radius;j<=radius;j++){
						int nx = Math.min(Math.max(x+k,0),i.length-1) ;
						v+=i[nx][y][c]*kernel[k+radius];
						//}
					}
					i2[x][y][c] = (float)v ;
				}
			}

		}

		float[][][] i3 = new float[i.length][i[0].length][i[0][0].length];
		for(int x=0;x<i.length;x++){
			for(int y=0;y<i[0].length;y++){
				for(int c = 0 ; c < i[0][0].length; c++){
					double v = 0;
					//for(int k=-radius;k<=radius;k++){
					for(int j=-radius;j<=radius;j++){
						int ny = Math.min(Math.max(y+j,0),i[0].length-1) ;
						v+=i2[x][ny][c]*kernel[j+radius];
					}
					//}
					i3[x][y][c] = (float)v ;
				}
			}

		}

		return i3 ;
	}


	//applies a one dimensional kernel in both directions
	public static float[][] applyseperablekernel(float[][] i, double kernel[]){
		float[][] i2 = new float[i.length][i[0].length];
		int radius = (kernel.length - 1 )/2 ;
		for(int x=radius;x<i.length-radius;x++){
			for(int y=radius;y<i[0].length-radius;y++){
				double v = 0;
				for(int k=-radius;k<=radius;k++){
					//for(int j=-radius;j<=radius;j++){

					v+=i[x+k][y]*kernel[k+radius];
					//}
				}
				i2[x][y] = (float)v ;

			}

		}

		float[][] i3 = new float[i.length][i[0].length];
		for(int x=radius;x<i.length-radius;x++){
			for(int y=radius;y<i[0].length-radius;y++){
				double v = 0;
				//for(int k=-radius;k<=radius;k++){
				for(int j=-radius;j<=radius;j++){
					v+=i2[x][y+j]*kernel[j+radius];
				}
				//}
				i3[x][y] = (float)v ;
			}

		}
		
		return i3 ;
	}


	//returns the sum of the determinant of the hessian for each channel
	public static float[][] DeterminantofHessian(byte image[][][]){
		//convert to float to avoid conversion and bitwise operation in main loop
		int w = image.length, h = image[0].length ;
		float i[][][] = new float[w][h][3] ;
		for(int c=0;c<3;c++){
			for(int x=0;x<w;x++){
				for(int y=0;y<h;y++){
					i[x][y][c] = (image[x][y][c]&0xff)* (1.0f/255f) ;
				}
			}
		}
		//calculate determinant of hessian
		float DoH[][] = DeterminantofHessian(i) ;
		return DoH ;
	}
	
	
	//returns the sum of the determinant of the hessian for each channel
	public static float[][] DeterminantofHessian(float i[][][]){
		int w = i.length, h = i[0].length ;
		//calculate determinant of hessian
		float DoH[][] = new float[w][h] ;
		for(int c=0;c<3;c++){
			for(int x=1;x<w-1;x++){
				for(int y=1;y<h-1;y++){
					float dxx = i[x+1][y][c]+i[x-1][y][c]-2*i[x][y][c] ;
					float dyy = i[x][y+1][c]+i[x][y-1][c]-2*i[x][y][c] ;
					float dxdy = i[x+1][y+1][c]+i[x-1][y-1][c]-i[x-1][y+1][c]-i[x+1][y-1][c] ;
					DoH[x][y] += Math.abs(dxx*dyy - dxdy*dxdy) ;
				}
			}
		}
		return DoH ;
	}
	
	//applies a laplacian to an image
	public static float[][][] laplacian(float i[][][]){
		int w = i.length, h = i[0].length ;
		float laplace[][][] = new float[w][h][3] ;
		for(int c=0;c<3;c++){
			for(int x=1;x<w-1;x++){
				for(int y=1;y<h-1;y++){
					laplace[x][y][c] = i[x+1][y][c] + i[x-1][y][c] + i[x][y+1][c] + i[x][y-1][c] - 4*i[x][y][c] ;
				}
			}
		}
		return laplace ;
	}
	
	//Uses bilinear interpolation to scale an image to the given resolution.
	//good for scaling an image up, use makethumbnail to scale an image down with higher quality
	public static byte[][][] scaleTo(byte i[][][], int w, int h){
		byte i2[][][] = new byte[w][h][3] ;
		int iw = i.length, ih = i[0].length ;
		double xd,yd ;
		int x2,y2 ;
		for(int x =0; x < w; x++){
			for(int y = 0 ;y < h;y++){
				xd = (iw*x)/(double)w ;//map this point into the old image
				yd = (ih*y)/(double)h ;//map this point into the old image
				x2 = (int)xd ;
				y2 = (int)yd ;
				if(x2 + 1 < iw && y2+ 1 < ih){//if not on edge do subpixel scaling
					double t = xd - x2 ;
					double s = yd - y2 ;
					double a = (1-t)*(1-s), b = t*(1-s), c = (1-t)*s, d = t*s ;
					i2[x][y][0] = (byte)(a*(i[x2][y2][0]&0xff)+b*(i[x2+1][y2][0]&0xff) + c*(i[x2][y2+1][0]&0xff) + d*(i[x2+1][y2+1][0]&0xff)) ;
					i2[x][y][1] = (byte)(a*(i[x2][y2][1]&0xff)+b*(i[x2+1][y2][1]&0xff) + c*(i[x2][y2+1][1]&0xff) + d*(i[x2+1][y2+1][1]&0xff)) ;
					i2[x][y][2] = (byte)(a*(i[x2][y2][2]&0xff)+b*(i[x2+1][y2][2]&0xff) + c*(i[x2][y2+1][2]&0xff) + d*(i[x2+1][y2+1][2]&0xff)) ;
				}else if(x2 >= 0 && y2>=0){
					i2[x][y][0] = i[x2][y2][0] ;
					i2[x][y][1] = i[x2][y2][1] ;
					i2[x][y][2] = i[x2][y2][2] ;
				}
			}
		}
		return i2 ;

	}
	
	
	public static BufferedImage scaleTo(BufferedImage img, int desiredwidth, int desiredheight){
    //System.out.println("Original Image size: " + img.getWidth(null)+ " x "+img.getHeight(null));
    if(img ==null || img.getWidth(null) <1 || img.getHeight(null) <1){
      return null;
    }else{
      byte image[][][] = convertimage(img) ;
      byte[][][] newimage = ImageUtility.scaleTo(image,desiredwidth,desiredheight) ;
      BufferedImage img2 = convertimage(newimage) ;
      return img2 ;
    }
  }
	
	

	//Sample using bilinear interpolation
	public static byte[] sample(byte i[][][], double x, double y){
		int x2 = (int)x ;
		int y2 = (int)y ;
		double t = x - x2 ;
		double s = y - y2 ;
		double a = (1-t)*(1-s), b = t*(1-s), c = (1-t)*s, d = t*s ;
		return new byte[]{ (byte)(a*(i[x2][y2][0]&0xff)+b*(i[x2+1][y2][0]&0xff) + c*(i[x2][y2+1][0]&0xff) + d*(i[x2+1][y2+1][0]&0xff)) ,
				(byte)(a*(i[x2][y2][1]&0xff)+b*(i[x2+1][y2][1]&0xff) + c*(i[x2][y2+1][1]&0xff) + d*(i[x2+1][y2+1][1]&0xff)) ,
				(byte)(a*(i[x2][y2][2]&0xff)+b*(i[x2+1][y2][2]&0xff) + c*(i[x2][y2+1][2]&0xff) + d*(i[x2+1][y2+1][2]&0xff)) 
		} ;
	}


	//averages sets of four pixels to perfectly scale an image to half size.
	public static byte[][][] halfsize(byte image[][][]){
		byte i2[][][] = new byte[image.length/2][image[0].length/2][3] ;
		for(int x =0; x < image.length-1; x+=2){
			for(int y = 0 ;y < image[0].length-1;y+=2){
				for(int c = 0 ; c < 3 ; c++){
					i2[x/2][y/2][c] = (byte)(((image[x][y][c]&0xff) + (image[x+1][y][c]&0xff) +  (image[x][y+1][c]&0xff) +  (image[x+1][y+1][c]&0xff) ) *0.25) ;
				}
			}
		}
		return i2 ;
	}
	
	// Scales an image to the desired size by simple sampling.
	// Low quality but fast.
	public static byte[][][] fastScaleTo(byte i[][][], int w, int h){
	  byte i2[][][] = new byte[w][h][3] ;
    int iw = i.length, ih = i[0].length ;
    int x2,y2 ;
    int w2 = w/2, h2 = h/2;
    for(int x =0; x < w; x++){
      for(int y = 0 ;y < h;y++){
        x2 = (iw*x+w2)/w;
        y2 = (ih*y+h2)/h;
        i2[x][y][0] = i[x2][y2][0] ;
        i2[x][y][1] = i[x2][y2][1] ;
        i2[x][y][2] = i[x2][y2][2] ;
      }
    }
    return i2 ;
	}
	
	public static BufferedImage fastScaleTo(BufferedImage img, int desiredwidth, int desiredheight){
    //System.out.println("Original Image size: " + img.getWidth(null)+ " x "+img.getHeight(null));
    if(img ==null || img.getWidth(null) <1 || img.getHeight(null) <1){
      return null;
    }else{
      byte image[][][] = convertimage(img) ;
      byte[][][] newimage = ImageUtility.fastScaleTo(image,desiredwidth,desiredheight) ;
      BufferedImage img2 = convertimage(newimage) ;
      return img2 ;
    }
  }
	
	
//Scales an image to the desired size by simple sampling.
 // Low quality but fast.
 public static byte[][][] multiSampleScaleTo(byte i[][][], int w, int h){
   byte i2[][][] = new byte[w][h][3] ;
   int iw = i.length, ih = i[0].length ;
   int x2,y2, x3,y3,x4,y4,x5,y5 ;
   int ox2 = iw/4, oy2 = ih/4;
   int ox3 = iw*3/4, oy3 = ih*3/4;
   int ox4 = iw/4, oy4 = ih*3/4;
   int ox5 = iw*3/4, oy5 = ih/4;
   for(int x =0; x < w; x++){
     for(int y = 0 ;y < h;y++){
       x2 = (iw*x+ox2)/w;
       y2 = (ih*y+oy2)/h;
       x3 = (iw*x+ox3)/w;
       y3 = (ih*y+oy3)/h;
       x4 = (iw*x+ox4)/w;
       y4 = (ih*y+oy4)/h;
       x5 = (iw*x+ox5)/w;
       y5 = (ih*y+oy5)/h;
       i2[x][y][0] = (byte)(((i[x2][y2][0]&0xff) + (i[x3][y3][0]&0xff)+(i[x4][y4][0]&0xff) + (i[x5][y5][0]&0xff))>>2) ;
       i2[x][y][1] = (byte)(((i[x2][y2][1]&0xff) + (i[x3][y3][1]&0xff)+(i[x4][y4][1]&0xff) + (i[x5][y5][1]&0xff))>>2) ;
       i2[x][y][2] = (byte)(((i[x2][y2][2]&0xff) + (i[x3][y3][2]&0xff)+(i[x4][y4][2]&0xff) + (i[x5][y5][2]&0xff))>>2) ;
     }
   }
   return i2 ;
 }
	
	public static BufferedImage multiSampleScaleTo(BufferedImage img, int desiredwidth, int desiredheight){
    //System.out.println("Original Image size: " + img.getWidth(null)+ " x "+img.getHeight(null));
    if(img ==null || img.getWidth(null) <1 || img.getHeight(null) <1){
      return null;
    }else{
      byte image[][][] = convertimage(img) ;
      byte[][][] newimage = ImageUtility.multiSampleScaleTo(image,desiredwidth,desiredheight) ;
      BufferedImage img2 = convertimage(newimage) ;
      return img2 ;
    }
  }

	//returns an image with the same color as the input images
	//but with the brightness of each pixel scaled to match the luminance given
	public static byte[][][] setluminance(byte[][][] image, float luminance[][]){
		int iw = image.length, ih = image[0].length ;
		byte i2[][][] = new byte[iw][ih][3] ;
		for(int x =0; x < iw; x++){
			for(int y = 0 ;y < ih;y++){
				double l = luminance(image[x][y]) ;
				if( l > 5 ){
					double scale = luminance[x][y]/l ;
					i2[x][y][0] = (byte)Math.min(Math.max((image[x][y][0]&0xff)*scale,0),255) ;
					i2[x][y][1] = (byte)Math.min(Math.max((image[x][y][1]&0xff)*scale,0),255) ;
					i2[x][y][2] = (byte)Math.min(Math.max((image[x][y][2]&0xff)*scale,0),255) ;
				}

			}
		}
		return i2 ;
	}

	//returns the color histograms for the given image
	//first index is which channel, second index is what value
	public static int[][] histogram(BufferedImage img){
		int h[][] = new int[3][256] ;
		byte image[][][] = convertimage(img) ;
		for(int x=0;x<image.length;x++){
			for(int y=0;y<image[0].length;y++){
				h[0][image[x][y][0]&0xff]++;
				h[1][image[x][y][1]&0xff]++;
				h[2][image[x][y][2]&0xff]++;
			}
		}
		return h ;
	}

	//scales an image, maintaining aspect ratio, to fit within the desired width and height
	//averages color over squares to get good results when scaling down
	//use scaleto when scaling up
	public static BufferedImage makethumbnail(BufferedImage img, double desiredwidth, double desiredheight){
		//System.out.println("Original Image size: " + img.getWidth(null)+ " x "+img.getHeight(null));
		if(img ==null || img.getWidth(null) <1 || img.getHeight(null) <1){
			return null;
		}else{
			byte image[][][] = convertimage(img) ;
			byte[][][] newimage = makethumbnail(image,desiredwidth,desiredheight) ;
			BufferedImage img2 = convertimage(newimage) ;
			return img2 ;
		}
	}

	//scales an image, maintaining aspect ratio, to fit within the desired width and height
	//averages color over squares to get good results when scaling down
	//use scaleto when scaling up
	public static byte[][][] makethumbnail(byte[][][] image, double desiredwidth, double desiredheight){

		int width = image.length ;
		int height = image[0].length ;
		//System.out.println(width +", " + height + " --> " + desiredwidth +", " + desiredheight) ;
		double scale = Math.min(desiredwidth/(double)width, desiredheight/(double)height) ;
		//System.out.println(desiredwidth + ", " + desiredheight + " --> " + scale);
		int nw = (int)(width*scale+.1), nh = (int)(height*scale+.1) ;
		//System.out.println(nw +", " + nh ) ;
		byte newimage[][][] = new byte[nw][nh][3];
		double pixel[] = new double[4];
		double xstep = width/(double)nw, ystep=height/(double)nh;
		for(int x = 0 ; x < nw;x++){
			for(int y = 0 ; y < nh;y++){
				pixel[0] =  x*xstep ;
				pixel[1] = y*ystep ;
				pixel[2] = pixel[0]+xstep;
				pixel[3] = pixel[1]+ystep;
				double c[] = colorSampledGammaCorrected(pixel, image);
				newimage[x][y][0] = (byte)(c[0]+.5);
				newimage[x][y][1] = (byte)(c[1]+.5);
				newimage[x][y][2] = (byte)(c[2]+.5);
			}
		}
		return newimage ;
	}

	//returns the average value of an axis aligned bounding box on this texture
	//uses no interpolation (see video modeling 2 / thumbnailmaker.texture for additional related methods and details)
	//AABB is an axis aligned bounding box of the form{ minx,miny,maxx,maxy}
	public static double sample(double AABB[], float i[][]){
		int width = i.length ;
		int height = i[0].length ;
		int minx = (int)(AABB[0]) ;
		int maxx = (int)(AABB[2]+1) ;
		int miny = (int)(AABB[1]) ;
		int maxy = (int)(AABB[3]+1) ;
		//make sure AABB doesn't try to read outside of image
		if(minx<0)minx = 0 ;
		if(miny<0)miny = 0;
		if(maxx>width)maxx = width ;
		if(maxy>height)maxy = height ;

		//area*value
		double valuearea = 0 ;
		double area =0;
		for(int x=minx;x<maxx;x++){
			for(int y=miny;y<maxy;y++){
				double[] texel = new double[]{x,y,x+1,y+1};//the AABB of this pixel
				double intersect[] = AABBintersect(texel,AABB) ;
				if(intersect[2] > intersect[0] && intersect[3] > intersect[1]){//if the AABBs intersect
					double intersectarea = (intersect[2]-intersect[0])*(intersect[3]-intersect[1]);
					if(intersectarea>0.000001){
						area += intersectarea ;
						valuearea+= i[x][y]*intersectarea ;//sum up values weighted by intersection area
					}
				}
			}
		}
		return valuearea/area;
	}


	//returns the average color of an axis aligned bounding box on this texture
	//using no interpolation 
	//AABB is an axis aligned bounding box of the form{ minx,miny,maxx,maxy}
	public static double[] colorsampled(double AABB[], byte image[][][]){
		int width = image.length;
		int height = image[1].length ;

		int minx = (int)(AABB[0]) ;
		int maxx = (int)(AABB[2]+1) ;
		int miny = (int)(AABB[1]) ;
		int maxy = (int)(AABB[3]+1) ;
		//make sure AABB doesn't try to read outside of texture
		if(minx<0)minx = 0 ;
		if(miny<0)miny = 0;
		if(maxx>width)maxx = width ;
		if(maxy>height)maxy = height ;

		//area*each color
		double rarea = 0 ;
		double barea = 0 ;
		double garea = 0 ;
		double area =0,a;
		for(int x=minx;x<maxx;x++){
			for(int y=miny;y<maxy;y++){
				double[] texel = new double[]{x,y,x+1,y+1};
				double intersect[] = AABBintersect(texel,AABB) ;
				if(AABB[2] > AABB[0] && AABB[3] > AABB[1]){//if the AABBs intersect
					double intersectarea = (AABB[2]-AABB[0])*(AABB[3]-AABB[1]);
					if(intersectarea>0.000001){
						area += intersectarea ;
						rarea+= (image[x][y][0]&0xff)*intersectarea ;
						garea+= (image[x][y][1]&0xff)*intersectarea ;
						barea+= (image[x][y][2]&0xff)*intersectarea ;
					}
				}
			}
		}
		return new double[]{rarea/area,garea/area,barea/area};
	}
	
//returns the average color of an axis aligned bounding box on this texture
  //using no interpolation 
  //AABB is an axis aligned bounding box of the form{ minx,miny,maxx,maxy}
  public static double[] colorSampledGammaCorrected(double AABB[], byte image[][][]){
    int width = image.length;
    int height = image[1].length ;

    int minx = (int)(AABB[0]) ;
    int maxx = (int)(AABB[2]+1) ;
    int miny = (int)(AABB[1]) ;
    int maxy = (int)(AABB[3]+1) ;
    //make sure AABB doesn't try to read outside of texture
    if(minx<0)minx = 0 ;
    if(miny<0)miny = 0;
    if(maxx>width)maxx = width ;
    if(maxy>height)maxy = height ;

    //area*each color
    double rarea = 0 ;
    double barea = 0 ;
    double garea = 0 ;
    double area =0,a;
    for(int x=minx;x<maxx;x++){
      for(int y=miny;y<maxy;y++){
        double[] texel = new double[]{x,y,x+1,y+1};
        double intersect[] = AABBintersect(texel,AABB) ;
        if(AABB[2] > AABB[0] && AABB[3] > AABB[1]){//if the AABBs intersect
          double intersectarea = (AABB[2]-AABB[0])*(AABB[3]-AABB[1]);
          if(intersectarea>0.000001){
            area += intersectarea ;
            rarea+= toGamma(image[x][y][0]&0xff)*intersectarea ;
            garea+= toGamma(image[x][y][1]&0xff)*intersectarea ;
            barea+= toGamma(image[x][y][2]&0xff)*intersectarea ;
          }
        }
      }
    }
    return new double[]{fromGamma(rarea/area),fromGamma(garea/area),fromGamma(barea/area)};
  }
  
  public static double toGamma(double c){
    //return Math.pow(c/255.0, 2.2);
    return c*c*0.0000153787;
  }
  public static double fromGamma(double g){
    //return Math.pow(g, 1.0/2.2)*255;
    return Math.sqrt(g)*255.0;
  }
  

	//returns an AABB representing the intersection of two AABBs 
	//where AABBs given as xmin,ymin,xmax,ymax
	public static double[] AABBintersect(double[] a, double[] b){
		return new double[]{Math.max(a[0], b[0]),Math.max(a[1], b[1]), Math.min(a[2], b[2]),Math.min(a[3], b[3])};
	}

	//Saves a buffered image to disk as jpeg with the given percentage quality
	public static void saveImage(BufferedImage bi, File file, int quality){
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try{
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
			quality = Math.max(0, Math.min(quality, 100));
			param.setQuality((float) quality / 100.0f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bi);
		}catch (FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		catch (IOException ioe){
			ioe.printStackTrace();
		}finally{
			if (bos != null){
				try{
					bos.close();
				}
				catch (IOException ioe){
					ioe.printStackTrace();
				}
			}
			if (fos != null){
				try{
					fos.close();
				}catch (IOException ioe){
					ioe.printStackTrace();
				}
			}
		}
	}

	// Converts a float image to a list of vectors, where each vector contains a patch either of one color or all colors.
	public static float[][] convertToVectors(float image[][][], int patchsize, boolean combine_colors){
		int pw = (int) Math.ceil(image.length / (double)patchsize), ph = (int) Math.ceil(image[0].length / (double)patchsize), colors = image[0][0].length;
		float[][] vector;
		if(combine_colors){
			vector = new float[pw*ph][patchsize*patchsize*colors];
		}else{
			vector = new float[pw*ph*colors][patchsize*patchsize];
		}
		for(int c=0;c<colors;c++){
			for(int y=0;y<image[0].length;y++){
				for(int x=0;x<image.length;x++){
					if(combine_colors){
						vector[(x/patchsize) + pw * (y/patchsize)][(x%patchsize) + patchsize * (y%patchsize) + patchsize*patchsize*c] = image[x][y][c];
					} else{
						vector[(x/patchsize) + pw * (y/patchsize) + pw*ph*c][(x%patchsize) + patchsize * (y%patchsize)] = image[x][y][c];
					}
				}
			}
		}
		return vector;
	}
	
	// Converts a set of float vectors created with convertToVectors back to an image.
		public static float[][][] convertFromVectors(float[][] vector, int width, int height, int colors, int patchsize){
			float image[][][] = new float[width][height][colors];
			int pw = (int) Math.ceil(image.length / (double)patchsize), ph = (int) Math.ceil(image[0].length / (double)patchsize);
			boolean combine_colors = (vector.length == pw*ph);
			for(int c=0;c<colors;c++){
				for(int y=0;y<image[0].length;y++){
					for(int x=0;x<image.length;x++){
						if(combine_colors){
							image[x][y][c] = vector[(x/patchsize) + pw * (y/patchsize)][(x%patchsize) + patchsize * (y%patchsize) + patchsize*patchsize*c];
						} else{
								image[x][y][c] = vector[(x/patchsize) + pw * (y/patchsize) + pw*ph*c][(x%patchsize) + patchsize * (y%patchsize)];
						}
					}
				}
			}
			return image;
		}
	
}
