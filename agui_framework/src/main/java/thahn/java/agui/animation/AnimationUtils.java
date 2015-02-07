package thahn.java.agui.animation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import thahn.java.agui.app.Context;
import thahn.java.agui.res.ResourcesContainer.NotFoundException;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.view.AttributeSet;


/**
 * Defines common utilities for working with animations.
 *
 */
public class AnimationUtils {

    /**
     * These flags are used when parsing AnimatorSet objects
     */
    private static final int TOGETHER = 0;
    private static final int SEQUENTIALLY = 1;


    /**
     * Returns the current animation time in milliseconds. This time should be used when invoking
     * {@link Animation#setStartTime(long)}. Refer to {@link android.os.SystemClock} for more
     * information about the different available clocks. The clock used by this method is
     * <em>not</em> the "wall" clock (it is not {@link System#currentTimeMillis}).
     *
     * @return the current animation time in milliseconds
     *
     * @see android.os.SystemClock
     */
    public static long currentAnimationTimeMillis() {
    	return System.currentTimeMillis();
//        return SystemClock.uptimeMillis();
    }

    /**
     * Loads an {@link Animation} object from a resource
     *
     * @param context Application context used to access resources
     * @param id The resource id of the animation to load
     * @return The animation object reference by the specified id
     * @throws NotFoundException when the animation cannot be loaded
     */
    public static Animation loadAnimation(Context context, int id) {
    	try {
	    	Animation anim = null;
	    	String path = context.getResources().getAnimation(id);
	    	if(path != null) {
	    		InputStream is = MyUtils.getResourceInputStream(path);
	    		if(is != null) {
		    		// FIXME : 매번 이렇게 animation 실행마다 파일을 읽어와서 하는 것은 performance를 많이 떨어트린다.
	    			BufferedInputStream bi = new BufferedInputStream(is);//in);
	    			Element root = new SAXBuilder().build(bi).getRootElement();
	    			String type = root.getName();
	    			
	    			AttributeSet attrs = new AttributeSet(context, root);
	    			anim = createAnimationFromXml(context, type, null, attrs, root);
	    			
	    			bi.close();
	    		}
	    	}
	    	return anim;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		throw new NotFoundException();
    }

//    private static Animation createAnimationFromXml(Context c, String path) {
//        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
//    }

    private static Animation createAnimationFromXml(Context c, String type, AnimationSet parent, AttributeSet attrs, Element root) {
        Animation anim = null;
        
        if (type.equals("set")) {
        	anim = new AnimationSet(c, attrs);
        	
        	List<Element> children = (List<Element>) root.getChildren();
        	int size = children.size();
        	for (int i = 0; i < size; i++) {
        		Element e = children.get(i);
        		AttributeSet childAttrs = new AttributeSet(c, e);
        		createAnimationFromXml(c, e.getName(), (AnimationSet) anim, childAttrs, e);
        	}
        } else if (type.equals("alpha")) {
            anim = new AlphaAnimation(c, attrs);
        } else if (type.equals("scale")) {
            anim = new ScaleAnimation(c, attrs);
        }  else if (type.equals("rotate")) {
            anim = new RotateAnimation(c, attrs);
        }  else if (type.equals("translate")) {
            anim = new TranslateAnimation(c, attrs);
        } else {
            throw new RuntimeException("Unknown animation name: " + type);
        }
        
        if (parent != null) {
            parent.addAnimation(anim);
        }
        
        return anim;

    }
//
//    public static LayoutAnimationController loadLayoutAnimation(Context context, int id)
//            throws NotFoundException {
//        
//        XmlResourceParser parser = null;
//        try {
//            parser = context.getResources().getAnimation(id);
//            return createLayoutAnimationFromXml(context, parser);
//        } catch (XmlPullParserException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } catch (IOException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } finally {
//            if (parser != null) parser.close();
//        }
//    }
//
//    private static LayoutAnimationController createLayoutAnimationFromXml(Context c,
//            XmlPullParser parser) throws XmlPullParserException, IOException {
//
//        return createLayoutAnimationFromXml(c, parser, Xml.asAttributeSet(parser));
//    }
//
//    private static LayoutAnimationController createLayoutAnimationFromXml(Context c,
//            XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
//
//        LayoutAnimationController controller = null;
//
//        int type;
//        int depth = parser.getDepth();
//
//        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
//                && type != XmlPullParser.END_DOCUMENT) {
//
//            if (type != XmlPullParser.START_TAG) {
//                continue;
//            }
//
//            String name = parser.getName();
//
//            if ("layoutAnimation".equals(name)) {
//                controller = new LayoutAnimationController(c, attrs);
//            } else if ("gridLayoutAnimation".equals(name)) {
//                controller = new GridLayoutAnimationController(c, attrs);
//            } else {
//                throw new RuntimeException("Unknown layout animation name: " + name);
//            }
//        }
//
//        return controller;
//    }
//
//    /**
//     * Make an animation for objects becoming visible. Uses a slide and fade
//     * effect.
//     * 
//     * @param c Context for loading resources
//     * @param fromLeft is the object to be animated coming from the left
//     * @return The new animation
//     */
//    public static Animation makeInAnimation(Context c, boolean fromLeft) {
//        Animation a;
//        if (fromLeft) {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_in_left);
//        } else {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_in_right);
//        }
//
//        a.setInterpolator(new DecelerateInterpolator());
//        a.setStartTime(currentAnimationTimeMillis());
//        return a;
//    }
//    
//    /**
//     * Make an animation for objects becoming invisible. Uses a slide and fade
//     * effect.
//     * 
//     * @param c Context for loading resources
//     * @param toRight is the object to be animated exiting to the right
//     * @return The new animation
//     */
//    public static Animation makeOutAnimation(Context c, boolean toRight) {
//        Animation a;
//        if (toRight) {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_out_right);
//        } else {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_out_left);
//        }
//        
//        a.setInterpolator(new AccelerateInterpolator());
//        a.setStartTime(currentAnimationTimeMillis());
//        return a;
//    }
//
//    
//    /**
//     * Make an animation for objects becoming visible. Uses a slide up and fade
//     * effect.
//     * 
//     * @param c Context for loading resources
//     * @return The new animation
//     */
//    public static Animation makeInChildBottomAnimation(Context c) {
//        Animation a;
//        a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_in_child_bottom);
//        a.setInterpolator(new AccelerateInterpolator());
//        a.setStartTime(currentAnimationTimeMillis());
//        return a;
//    }
//    
    /**
     * Loads an {@link Interpolator} object from a resource
     * 
     * @param context Application context used to access resources
     * @param id The resource id of the animation to load
     * @return The animation object reference by the specified id
     * @throws NotFoundException
     */
    public static Interpolator loadInterpolator(Context context, int id) {
    	Interpolator interpolator = null;
    	String name = context.getResources().getAnimation(id);
    	if(name != null) {
	    	try {
	    		InputStream is = MyUtils.getResourceInputStream(name);
				BufferedInputStream bi = new BufferedInputStream(is);
				SAXBuilder builder = new SAXBuilder();
				Element root = builder.build(bi).getRootElement();
				AttributeSet attrs = new AttributeSet(context, root);
				interpolator = createInterpolatorFromXml(context, name, attrs);
				bi.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return interpolator;
    }

    private static Interpolator createInterpolatorFromXml(Context c, String name, AttributeSet attrs) {
        Interpolator interpolator = null;
        String firstName = name.substring(name.lastIndexOf("/")+1, name.lastIndexOf("."));
        
        if (firstName.equals("linear_interpolator")) {
            interpolator = new LinearInterpolator(c, attrs);
        } else if (firstName.equals("accelerate_interpolator")) {
            interpolator = new AccelerateInterpolator(c, attrs);
        } else if (firstName.equals("decelerate_interpolator")) {
            interpolator = new DecelerateInterpolator(c, attrs);
        }  else if (firstName.equals("accelerate_decelerate_interpolator")) {
            interpolator = new AccelerateDecelerateInterpolator(c, attrs);
        } else if (firstName.equals("cycle_interpolator")) {
            interpolator = new CycleInterpolator(c, attrs);
        } else if (firstName.equals("anticipate_interpolator")) {
            interpolator = new AnticipateInterpolator(c, attrs);
        } else if (firstName.equals("overshoot_interpolator")) {
            interpolator = new OvershootInterpolator(c, attrs);
        } else if (firstName.equals("anticipate_overshoot_interpolator")) {
            interpolator = new AnticipateOvershootInterpolator(c, attrs);
        } else if (firstName.equals("bounce_interpolator")) {
            interpolator = new BounceInterpolator(c, attrs);
        } else {
            throw new RuntimeException("Unknown interpolator name: " + firstName);
        }

        return interpolator;
    }
}