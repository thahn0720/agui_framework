package thahn.java.agui.res;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.google.common.collect.Lists;

import thahn.java.agui.AguiConstants;
import thahn.java.agui.Global;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.graphics.Color;
import thahn.java.agui.utils.DimensionUtils;
import thahn.java.agui.utils.Log;
import thahn.java.agui.utils.MyUtils;
import thahn.java.agui.utils.Pair;

/**
 * 
 * @author thAhn
 *
 */
public abstract class RBase {
	
	public static final String 												TAG 					= "RBase";
	
	public static final int													INDEX_GAP				= 10000;
	
	public static final String												GEN_FILE_NAME			= "R";
	
	public static final String												RES_PREFIX				= "@";
	public static final String												RES_NS_SEPARATOR		= ":";
	public static final String												RES_SEPARATOR			= "/";
	
	public static final String												TAG_RESOURCES			= "resources";
	public static final String												TAG_STRING				= "string";
	public static final String												TAG_INTEGER				= "integer";
	public static final String												TAG_BOOL				= "bool";
	public static final String												TAG_DIMEN  				= "dimen";
	public static final String												TAG_COLOR  				= "color";
	public static final String												TAG_STYLE			    = "style";
	public static final String												TAG_ITEM			    = "item";
	public static final String												TAG_DECLARE_STYLEABLE   = "declare-styleable";
	public static final String												TAG_ATTR   				= "attr";
	
	public static final String												FORMAT_DIMENSION		= "dimension";
	
	/*package*/ ResourcesContainer											mResources;
	/*package*/ String														mPackageName;
	/*package*/ BufferedOutputStream										mROS;
	/*package*/ String														mAbsoluteResBasePath;
	/*package*/ String														mAbsoluteGenBasePath;
	/*package*/ int															mStartIndex;
	/*package*/ EnumResources 												mEnumRes;
	/*package*/ boolean														isWritable;
	/*package*/ Class<?>													mClassBuildConfig;
	/*package*/ List<String>												mPathList = new ArrayList<String>();
	
	public void parse() {
		try {
			String packageName = mPackageName.replace(".", "/");
			File dirs = null;
			
			if (isJar()) {
//				jar:file:\E:\Dropbox\Workspace\Java\AGUI\AGUI_SDK\agui_sdk.jar!\thahn\java\agui\R.java 
				isWritable = false;

				ZipInputStream in = new ZipInputStream(new FileInputStream(mAbsoluteResBasePath));
			    ZipEntry entry = null;
			    while ((entry = in.getNextEntry()) != null) {
			    	String path = entry.getName();
			    	if (!path.endsWith("/") && (path.endsWith(".xml") || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".bmp"))) {
			    		// FIXME : seperate container variable by folder like mValuesPathList , mMenuPathList and so on.
			    		mPathList.add("/"+path);
			    	}
				}
			    in.close();
			} else {
				isWritable = true;
				File genDir = new File(mAbsoluteGenBasePath+"/gen/"+packageName);
				if (!genDir.exists()) genDir.mkdirs();
				dirs = new File(mAbsoluteGenBasePath+"/gen/"+packageName+"/R.java");
			}
			if (isWritable) {
				mROS = new BufferedOutputStream(new FileOutputStream(dirs));
			}
			write(makePackage(mPackageName));
			write(startClass());

			if (mAbsoluteGenBasePath.equals(Global.corePath)) {
				mClassBuildConfig = Class.forName(mPackageName+".BuildConfig");
			} else {
				mClassBuildConfig = MyUtils.getProjectClass(mPackageName+".BuildConfig");
			}
			
//			if (true) {
				parseAllValuesByDOM();
//			} else {
//				parseAttrByDOM();
//				parseStyleableByDOM();			
//				parseValuesByDOM();
//			}
			
			mStartIndex += INDEX_GAP;
			parseIdByDOM();
			mStartIndex += INDEX_GAP;
			parseLayoutByDOM();
			mStartIndex += INDEX_GAP;
			parseDrawableImgByDOM();
			mStartIndex += INDEX_GAP;
			parseAnimByDOM();
			mStartIndex += INDEX_GAP;
			parseMenuByDOM();
			mStartIndex += INDEX_GAP;
			parseRawByDOM();
			
			write(endClass());
			if (isWritable) {
				mROS.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
// <resource>	
//	<style name="View">
//    <item name="android:textSize">13dip</item>
	private void parseStyleableByDOM() throws JDOMException, IOException {
		String[] paths = new String[]{ "/res/values/styles.xml"
									 , "/res/values/themes.xml" };
		
		write(makePublicStaticFinalClass("style"));
		
		for (String path : paths) {//mClassR.getProtectionDomain().getCodeSource().getLocation().getPath() -5
			InputStream in = getInputStream(path);
			if (in == null) continue;
			
			BufferedInputStream bi = new BufferedInputStream(in);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(bi);
			Element root = doc.getRootElement();
			if (!"resources".equals(root.getName())) logWrongFormat();
			String packageName = mPackageName;//"dksxogudsla.java.agui";//R.class.getPackage().getName();
			String simpleName = "R";//R.class.getSimpleName();
			List<Element> elements = (List<Element>) root.getChildren();
			for (Element element : elements) {
				TreeMap<String, String> styleMap = new TreeMap<>();
				String eName = element.getName();
				if (!"style".equals(eName)) logWrongFormat();
				String styleName = element.getAttributeValue("name");
				String parentName = element.getAttributeValue("parent");
				// process parent by attr
				if (parentName != null) {
					for (;;) {
						Element eParent = getElement(elements, "name", parentName);
						if (eParent == null) {
							Log.t("xml error : above all, parent's attr should be positioned on top of a citing child");
						}
						for (Element item : (List<Element>) eParent.getChildren()) {
							addStyleabItem(styleMap, item, packageName, simpleName);
						}
						parentName = eParent.getAttributeValue("parent");
						if (parentName == null) break;
					}
				}
				// process parent bt "."
				if (styleName.contains(".")) {
					String[] parents = styleName.replace(".", "_").split("_");
					for (int i=0;i<parents.length-1;++i) {
						String parentTemp = parents[i];
						Element eParent = getElement(elements, "name", parentTemp);
						if (eParent == null) {
							// TODO : implement and modify when custom
							Log.t("xml error : above all, parent's attr should be positioned on top of a citing child");
						}
						for (Element item : (List<Element>) eParent.getChildren()) {
							addStyleabItem(styleMap, item, packageName, simpleName);
						}
					}
					styleName = styleName.replace(".", "_");
				}
				//
				String declare = new StringBuilder("\t\tpublic static final int[][] ").append(styleName).append(" = new int[][]{").toString();
				write(declare.getBytes());
				
				for (Element item : (List<Element>) element.getChildren()) {
	//				if (!"item".equals(item.getName())) logWrongFormat();
					addStyleabItem(styleMap, item, packageName, simpleName);
				}
				
				for (Entry<String, String> entry : styleMap.entrySet()) {
					String arrayItem = new StringBuilder("\r\n\t\t\t{").append(entry.getKey()).append(", ").append(entry.getValue()).append("},").toString();
					write(arrayItem.getBytes());
				}
				write("\r\n\t\t};\r\n".getBytes());
			}
			
			bi.close();
			in.close();
		}
//		
		write(endClass());
	}
	
	private void addStyleabItem(TreeMap<String, String> styleMap, Element item, String packageName, String simpleName) {
		String[] names = item.getAttributeValue("name").split(":");
		String itemName = null;
		if (names.length >= 2) {
			itemName = names[1];
		} else {
			itemName = names[0];
		}
		String value = item.getValue();
		String attrName = //itemName;
						itemName.split("_")[1];
		String temp = mEnumRes.get(attrName.hashCode(), value.hashCode());
		if (temp != null) {
			value = temp;
		}
		String name = new StringBuilder(packageName).append(".").append(simpleName).append(".attr.").append(itemName).toString();
		styleMap.put(name, value);
	}
	
	private Element getElement(List<Element> list, String attrName, String name) {
		for (Element e : list) {
			if (name.equals(e.getAttributeValue(attrName))) return e;
		}
		return null;
	}
	
	//<resources>
//	<declare-styleable name="View">
//    <attr format="string" name="id" />
	private void parseAttrByDOM() throws JDOMException, IOException {
		String path = "/res/values/attrs.xml";
		InputStream in = getInputStream(path);
		if (in == null) return ;
//		
		write(makePublicStaticFinalClass("attr"));
//		
//		FileInputStream in = new FileInputStream(xmlFile);
		BufferedInputStream bi = new BufferedInputStream(in);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(bi);
		Element root = doc.getRootElement();
		if (!"resources".equals(root.getName())) logWrongFormat();
		List<Element> elements = (List<Element>) root.getChildren();
		for (Element element : elements) {
			if (!"declare-styleable".equals(element.getName())) logWrongFormat();
			String declareName = element.getAttributeValue("name");
			write(makePublicStaticInt(declareName, element.getChildren().size()));
			for (Element attr : (List<Element>) element.getChildren()) {
				if (!"attr".equals(attr.getName())) logWrongFormat();
				String attrName = attr.getAttributeValue("name");
				String attrFormat = attr.getAttributeValue("format");
				if (attrFormat == null) {
					List<Element> children = (List<Element>) attr.getChildren();
					if (children.size() > 0) {
						HashMap<Integer,String> enums = new HashMap<>();
						for (Element en : children) {
							enums.put(en.getAttributeValue("name").hashCode(), en.getAttributeValue("value"));
						}
						mEnumRes.put(attrName.hashCode(), enums);
					}
				} 
//				pairList.add(new Pair<String, String>(declareName, attrName));
				String name = new StringBuilder(declareName).append("_").append(attrName).toString();
				write(makePublicStaticInt(name, attrName.hashCode()));
//				++mStartIndex;
			}
		}
		
		bi.close();
		in.close();
//		
		write(endClass());
	}
	
	private void parseAnimByDOM() throws Exception {
		write(makePublicStaticFinalClass("anim"));
		//
		final String animPath = mAbsoluteResBasePath+"/res/anim/";
		mCheckFolder.loop(animPath, new OnDiscoverListener() {
			@Override
			public void processFile(String path, String pathWithoutEx) throws IOException {
				write(makePublicStaticInt(pathWithoutEx, mStartIndex));
	    		mResources.addAnimValue(mStartIndex, animPath+path);
	    		++mStartIndex;
			}
		});
		write(endClass());
	}
	
	private void parseMenuByDOM() throws Exception {
		write(makePublicStaticFinalClass("menu"));
		
		final String menuPath = mAbsoluteResBasePath+"/res/menu/";
		mCheckFolder.loop(menuPath, new OnDiscoverListener() {
			@Override
			public void processFile(String path, String pathWithoutEx) throws IOException {
				write(makePublicStaticInt(pathWithoutEx, mStartIndex));
	    		mResources.addMenuValue(mStartIndex, menuPath+path);
	    		++mStartIndex;
			}
		});
		
		write(endClass());
	}
	
	@SuppressWarnings("unchecked")
	private void parseDrawableImgByDOM() throws Exception {
		write(makePublicStaticFinalClass("drawable"));
		
		final String layoutPath = mAbsoluteResBasePath+"/res/drawable-hdpi/";
		mCheckFolder.loop(layoutPath, new OnDiscoverListener() {
			@Override
			public void processFile(String path, String pathWithoutEx) throws IOException {
				if (pathWithoutEx.endsWith(".9")) {
					pathWithoutEx = pathWithoutEx.substring(0, pathWithoutEx.length()-2);
				}
				write(makePublicStaticInt(pathWithoutEx, mStartIndex));
				mResources.addDrawableValue(mStartIndex, layoutPath+path);
				++mStartIndex;
			}
		});
		parseDrawableByDOM();
		
		write(endClass());
	}
	
	@SuppressWarnings("unchecked")
	private void parseDrawableByDOM() throws Exception {
		final String layoutPath = mAbsoluteResBasePath+"/res/drawable/";
		mCheckFolder.loop(layoutPath, new OnDiscoverListener() {
			@Override
			public void processFile(String path, String pathWithoutEx) throws IOException {
				write(makePublicStaticInt(pathWithoutEx, mStartIndex));
				mResources.addDrawableValue(mStartIndex, layoutPath+path);
				++mStartIndex;
			}
		});
	}
	
	private void parseLayoutByDOM() throws Exception {
		write(makePublicStaticFinalClass("layout"));
		
		final String layoutPath = mAbsoluteResBasePath+"/res/layout/";
		mCheckFolder.loop(layoutPath, new OnDiscoverListener() {
			@Override
			public void processFile(String path, String pathWithoutEx) throws IOException {
				write(makePublicStaticInt(pathWithoutEx, mStartIndex));
				mResources.addLayoutValue(mStartIndex, layoutPath+pathWithoutEx+".xml");
				++mStartIndex;
			}
		});
		
		write(endClass());
	}
	
	private void parseIdByDOM() throws JDOMException, IOException, URISyntaxException, Exception {
		write(makePublicStaticFinalClass("id"));
		String[] paths = null;
		
		if (isJar()) {
			paths = new String[]{ "/res/layout/", "/res/menu/" };
		} else {
			paths = new String[]{ mAbsoluteResBasePath + "/res/layout/", mAbsoluteResBasePath + "/res/menu/" };
		}
		
		for (String dirPath : paths) {
			if (isJar()) {
			    for (String resName : mPathList) {
			    	if (!resName.contains(dirPath)) continue;
			    	InputStream inputStream = mClassBuildConfig.getResourceAsStream(resName);
					BufferedInputStream bi = new BufferedInputStream(inputStream);
					SAXBuilder builder = new SAXBuilder();
					Document doc = builder.build(bi);
					Element root = doc.getRootElement();
					//			
					for (Attribute attr : (List<Attribute>)root.getAttributes()) {
						if (attr.getName().equals("id")) {
							String idValue = attr.getValue();
							if (idValue.startsWith("@+") || idValue.startsWith("@agui:")) {
								String idName = idValue.substring(idValue.indexOf("/")+1);
								if (mResources.containsId(idName)) continue;
								write(makePublicStaticInt(idName, mStartIndex));
								mResources.addIdValue(mStartIndex, idName);
								++mStartIndex;
							}
							break;
						}
					}
					//			
					checkId(root);
					//				
					bi.close();
					inputStream.close();
			    }
			} else {
				File file = new File(dirPath);
				String[] fileList = file.list();
				if (fileList != null) {
					for (String path : fileList) {
						String fullPath = dirPath + path;
						File layout = new File(fullPath);
						FileInputStream in = new FileInputStream(layout);
						BufferedInputStream bi = new BufferedInputStream(in);
						try {
							SAXBuilder builder = new SAXBuilder();
							Document doc = builder.build(bi);
							Element root = doc.getRootElement();

							for (Attribute attr : (List<Attribute>)root.getAttributes()) {
								if (attr.getName().equals("id")) {
									String idValue = attr.getValue();
									if (idValue.startsWith("@+") || idValue.startsWith("@agui:")) {
										String idName = idValue.substring(idValue.indexOf("/")+1);
										if (mResources.containsId(idName)) continue;
										write(makePublicStaticInt(idName, mStartIndex));
										mResources.addIdValue(mStartIndex, idName);
										++mStartIndex;
									}
									break;
								}
							}
										
							checkId(root);
						} catch (Exception e) {
							Log.e(TAG, "file : " + fullPath + " is wrong format.");
							// e.printStackTrace();
						}
						
						bi.close();
						in.close();
					}
				}
		    }
		}
		write(endClass());
	}
	
	private void parseValuesByDOM() throws Exception {
		
		String[][] container = new String[][]{{mAbsoluteResBasePath+"/res/values/values.xml", "string"}
										, {mAbsoluteResBasePath+"/res/values/dimens.xml", "dimen"}
										};

		for (String[] names : container) {
			if (isJar()) {
				names[0] = names[0].substring(mAbsoluteResBasePath.length()+1);
				write(makePublicStaticFinalClass(names[1]));//"string"));
			    InputStream in = mClassBuildConfig.getResourceAsStream("/"+names[0]);
		    	if (in != null) {
		    		processValues(in);
					in.close();
			    }
			    write(endClass());
			} else {
				File xmlFile = new File(names[0]);
				if (!xmlFile.exists()) continue;
				write(makePublicStaticFinalClass(names[1]));//"string"));
				FileInputStream in = new FileInputStream(xmlFile);
				processValues(in);
				in.close();
				write(endClass());
			}
		}
	}
	
	private void processValues(InputStream in) throws JDOMException, IOException {
		BufferedInputStream bi = new BufferedInputStream(in);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(bi);
		Element root = doc.getRootElement();
		if (!"resources".equals(root.getName())) logWrongFormat();
		List<Element> elements = (List<Element>) root.getChildren();
		for (Element element : elements) {
			String eName = element.getName();
			String name = element.getAttributeValue("name");
			String value = element.getValue();
			if ("string".equals(eName)) { 
				mResources.addStringValue(mStartIndex, value);
			} else if ("integer".equals(eName)) {
				mResources.addIntegerValue(mStartIndex, Integer.parseInt(value));
			} else if ("bool".equals(eName)) {
				mResources.addBooleanValue(mStartIndex, Boolean.parseBoolean(value));
			} else if ("string-array".equals(eName)) {
				List<Element> children = root.getChildren();
				String[] values = new String[children.size()];
				for (int i = 0; i < values.length; i++) {
					Element child  = children.get(i);
					values[i] = child.getAttributeValue("item");
				}
				mResources.addStringArrayValue(mStartIndex, values);
			} else if ("dimen".equals(eName)) {
				if (value.contains("px")) {
					mResources.addDimensionValue(mStartIndex, Integer.parseInt(value.replace("px", "")));
				} else if (value.contains("dip")) {
					mResources.addDimensionValue(mStartIndex, Integer.parseInt(value.replace("dip", "")));
				} else {
					mResources.addDimensionValue(mStartIndex, Integer.parseInt(value));
				}
			} 
			write(makePublicStaticInt(name, mStartIndex));
			++mStartIndex;
		}
		
		bi.close();
	}
	
	private void parseRawByDOM() throws Exception {
		write(makePublicStaticFinalClass("raw"));
		
		final String layoutPath = mAbsoluteResBasePath+"/res/raw/";
		mCheckFolder.loop(layoutPath, new OnDiscoverListener() {
			@Override
			public void processFile(String path, String pathWithoutEx) throws IOException {
				String temp = path.substring(0, path.lastIndexOf("."));
				write(makePublicStaticInt(pathWithoutEx, mStartIndex));
				mResources.addRawValue(mStartIndex, layoutPath+path);
				++mStartIndex;
			}
		});
		
		write(endClass());
	}
	
	private void checkId(Element parent) throws IOException {
		for (Element element : (List<Element>) parent.getChildren()) {
			String idValue = null;
			for (Attribute attr : (List<Attribute>)element.getAttributes()) {
				if (attr.getName().equals("id")) {
					idValue = attr.getValue();
					if (idValue.startsWith("@+") || idValue.startsWith("@agui:")) {
						String idName = idValue.substring(idValue.indexOf("/")+1);
						if (mResources.containsId(idName)) continue;
						write(makePublicStaticInt(idName, mStartIndex));
						mResources.addIdValue(mStartIndex, idName);
						++mStartIndex;
					}
					break;
				}
			}
			List<Element> children = element.getChildren();
			if (children.size() > 0) checkId(element);
		}
	}
	
	private InputStream getInputStream(String path) {
		InputStream in = null;
		
		if (isJar() && mClassBuildConfig != null){
			in = (InputStream) mClassBuildConfig.getResourceAsStream(path);
		}
		
		if (in == null && mClassBuildConfig != null) {
			String pathWithoutBin = mClassBuildConfig.getProtectionDomain().getCodeSource().getLocation().getPath();
			if (pathWithoutBin.endsWith("/bin/")) {
				pathWithoutBin = pathWithoutBin.substring(0, pathWithoutBin.length()-5);
			} else if (pathWithoutBin.endsWith("/")) {
				pathWithoutBin = pathWithoutBin.substring(0, pathWithoutBin.length()-1);
			}
			try { in = new FileInputStream(pathWithoutBin + path); } catch (FileNotFoundException e) { }
		} 
		
		return in;
	}
	
	private boolean isJar() {
		return mAbsoluteResBasePath.endsWith(AguiConstants.JAR_KEYWORD);
	}
	
	private void write(byte[] content) throws IOException {
		if (isWritable) {
			mROS.write(content);
		}
	}
	
	private void logWrongFormat() {
		Log.e("Values Format is not correct");
		throw new WrongFormatException();
	}
	
	private byte[] makePackage(String packageName) {
		return new StringBuilder("package ").append(packageName.replace("/", ".")).append(";\r\n\n").toString().getBytes();
	}
	
	private byte[] startClass() {
		return new StringBuilder("public class R {\r\n").toString().getBytes();
	}
	
	private byte[] makePublicStaticFinalClass(String name) {
		return new StringBuilder("\tpublic static final class " + name + " {\r\n").toString().getBytes();
	}
	
	private byte[] endClass() {
		return new StringBuilder("}\r\n").toString().getBytes();
	}
	
	private byte[] makePublicStaticInt(String varName, int value) {
		return new StringBuilder("\t\tpublic static final int\t").append(varName).append("\t= ").append(value).append(";\r\n").toString().getBytes();
	}
	
	private byte[] makePublicStaticIntDoubleArray(String title) {
		return new StringBuilder("\t\tpublic static final int[][] " + title + " = new int[][] {\t\r\n").toString().getBytes();
	}
	
	private byte[] makePublicStaticIntArrayItem(String... items) {
		StringBuilder builder = new StringBuilder("\t\t\t{");
		for (String item : items) {
			builder.append(item).append(", ");
		}
		builder.append("},\r\n");
		return builder.toString().getBytes();
	}
	
	private byte[] endArray() {
		return new StringBuilder("\t\t};\r\n").toString().getBytes();
	}
	
	CheckFolder mCheckFolder = new CheckFolder();
	class CheckFolder {
		
		public void loop(String input, OnDiscoverListener listener) throws Exception {
			String folderPath = input;
			if (isJar()) {
				folderPath = input.substring(mAbsoluteResBasePath.length());
			    for (String path : mPathList) {
			    	if (path.contains(folderPath)) {
			    		path = path.substring(path.lastIndexOf("/")+1);
			    		String pathWithoutEx = path.substring(0, path.lastIndexOf("."));
			    		listener.processFile(path, pathWithoutEx);
			    	}
				}
			} else {
				File file = new File(folderPath);
				String[] fileList = file.list();
				if (fileList != null) {
					for (String path : fileList) {
						String pathWithoutEx = path.substring(0, path.lastIndexOf("."));
						listener.processFile(path, pathWithoutEx);
					}
				}
			}
		}
	}
	
	interface OnDiscoverListener {
		void processFile(String path, String pathWithoutEx) throws IOException;
	}
	
	//****************************************************************************************
	// new ver
	//****************************************************************************************
	private List<ResourceInfo> mStringRes = new ArrayList<ResourceInfo>();
	private List<ResourceInfo> mIntegerRes = new ArrayList<ResourceInfo>();
	private List<ResourceInfo> mBoolRes = new ArrayList<ResourceInfo>();
	private List<ResourceInfo> mColorRes = new ArrayList<ResourceInfo>();
	private List<ResourceInfo> mDimenRes = new ArrayList<ResourceInfo>();
	private List<StyleResource> mAttrRes = new ArrayList<StyleResource>();
	private List<StyleResource> mDeclareStyleableRes = new ArrayList<StyleResource>();
	private List<StyleResource> mStyleRes = new ArrayList<StyleResource>();
	
	private static HashMap<String, AttrInfo> mAttrNameMap = new HashMap<String, AttrInfo>();
	
	/**
	 * parse all xml values and set resource container
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void parseAllValuesByDOM() throws Exception {
		String valuesPath = "/res/values/";
		InputStream in = null;
		List<String> valuesPathList = null;
		
		if (isJar()) {
			valuesPathList = Lists.newArrayList();
		    for (String path : mPathList) {
		    	if (path.contains(valuesPath)) {
		    		valuesPathList.add(path);
		    	}
			}
		} else {
			File values = new File(mAbsoluteResBasePath + valuesPath);
			valuesPathList = Arrays.asList(values.list());
		}
		
		for (String path : valuesPathList) {
			if (isJar()) {
			    in = mClassBuildConfig.getResourceAsStream(path);
			} else {
				if (!path.endsWith(".xml")) {
					continue;
				} else {
					in = new BufferedInputStream(new FileInputStream(mAbsoluteResBasePath + valuesPath + path));
				}
			}
			
			if (in != null) {
				BufferedInputStream bi = new BufferedInputStream(in);
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(bi);
				Element root = doc.getRootElement();
				if (!TAG_RESOURCES.equals(root.getName())) {
					logWrongFormat();
					continue;
				}
				List<Element> elements = (List<Element>) root.getChildren();
				for (Element element : elements) {
					String eName = element.getName();
					String name = element.getAttributeValue("name");
					String value = element.getValue();
					//
					ResourceInfo resInfo = new ResourceInfo();
					resInfo.name = name;
					resInfo.index = mStartIndex;
					resInfo.value = value;
					resInfo.type = eName;
					//
					if (TAG_STRING.equals(eName)) { 
						mStringRes.add(resInfo);
//						mResources.addStringValue(mStartIndex, value);
					} else if (TAG_INTEGER.equals(eName)) {
						mIntegerRes.add(resInfo);
//						mResources.addIntegerValue(mStartIndex, Integer.parseInt(value));
					} else if (TAG_BOOL.equals(eName)) {
						mBoolRes.add(resInfo);
//						mResources.addBooleanValue(mStartIndex, Boolean.parseBoolean(value));
					} else if (TAG_COLOR.equals(eName)) {
						mColorRes.add(resInfo);
//						mResources.addColorValue(mStartIndex, Color.parseColor(value));
					} else if (TAG_DIMEN.equals(eName)) {
						resInfo.value = DimensionUtils.toPixel(value);
						mDimenRes.add(resInfo);
					} else if (TAG_DECLARE_STYLEABLE.equals(eName)) {
						resInfo = null;
						mDeclareStyleableRes.add(processDeclareStyleable(element));
					} else if (TAG_STYLE.equals(eName)) {
						resInfo = null;
						mStyleRes.add(processStyle(element));
					}
					
					++mStartIndex;
				}
				bi.close();
				in.close();
		    }
		}
		// process style parent 
		for (StyleResource styleRes : mStyleRes) {
			if (styleRes.parentName != null) {
				for (StyleResource res : mStyleRes) {
					if (res.name.equals(styleRes.parentName)) {
						for (Pair<String, String> pair : res.items.values()) {
							Pair<String, String> attrInfo = styleRes.items.get(pair.first);
							if (attrInfo == null) {
								styleRes.items.put(pair.first, pair);
							}
						}
					}
				}
			}
		}
		// writeRClass
		makeR();
	}
	
	private StyleResource processStyle(Element element) {
		StyleResource styleRes = new StyleResource(); 
		String styleName = element.getAttributeValue("name");
		String parentName = element.getAttributeValue("parent");
		
		if (styleName.contains(".")) {
			styleName = styleName.replace(".", "_");
		}
		
		styleRes.name = styleName;
		styleRes.parentName = parentName;
		 
		for (Element item : (List<Element>) element.getChildren()) {
			String itemName = null;
			String packageName = mPackageName;
			String[] names = item.getAttributeValue("name").split(":");
			if (names.length >= 2) {
				packageName = Global.corePackageName;
				itemName = names[1];
			} else {
				packageName = mPackageName;
				itemName = names[0];
			}
			String attrName = new StringBuilder(packageName).append(".R.attr.").append(itemName).toString();
			styleRes.items.put(attrName, new Pair<String, String>(attrName, item.getValue()));
		}
		
		return styleRes;
	}
	
	/**
	 * attrs
	 */
	private StyleResource processDeclareStyleable(Element element) {
		StyleResource attrRes = new StyleResource();
		List<Element> children = (List<Element>) element.getChildren();
		String declareName = element.getAttributeValue("name");
		String value = String.valueOf(children.size());
		attrRes.name = declareName;
		attrRes.value = value;
		
		for (Element child : children) {
			if (!TAG_ATTR.equals(child.getName())) logWrongFormat();
			String attrName = child.getAttributeValue("name");
			String attrFormat = child.getAttributeValue("format");
			if (attrFormat == null) {
				List<Element> enumList = (List<Element>) child.getChildren();
				if (enumList.size() > 0) {
					HashMap<Integer,String> enums = new HashMap<>();
					for (Element en : enumList) {
						enums.put(en.getAttributeValue("name").hashCode(), en.getAttributeValue("value"));
					}
					mEnumRes.put(attrName.hashCode(), enums);
				}
			} 
			String name = new StringBuilder(declareName).append("_").append(attrName).toString();
			attrRes.items.put(name, new Pair<String, String>(name, String.valueOf(attrName.hashCode())));
			AttrInfo attrInfo = new AttrInfo();
			attrInfo.fullName = name;
			attrInfo.attrName = attrName;
			attrInfo.format = attrFormat;
			mAttrNameMap.put(name, attrInfo);
		}
		
		return attrRes;
	}
	
	/**
	 * In previous, we put the resource value in resource container object. 
	 * Now, make R.java through resource value info 
	 * @throws IOException
	 */
	private void makeR() throws IOException {
//		mStringRes
		makeType1(mStringRes, "string");
//		mIntegerRes
		makeType1(mIntegerRes, "integer");
//		mDimenRes
		makeType1(mDimenRes, "dimen");
//		mBoolRes
		makeType1(mBoolRes, "bool");
//		mColorRes
		makeType1(mColorRes, "color");
//		mDeclareStyleableRes
		makeStyleableResource(mStyleRes, "style");
//		mAttrRes
		makeAttrResource(mDeclareStyleableRes, "attr");
	}
	
	private void makeType1(List<ResourceInfo> resInfoList, String which) throws IOException {
		write(makePublicStaticFinalClass(which));
		//
		for (ResourceInfo resInfo : resInfoList) {
			write(makePublicStaticInt(resInfo.name, resInfo.index));
			//
			if (resInfo.value.startsWith("@")) {
				String valueName = resInfo.value.split("/")[1];
				for (ResourceInfo info : resInfoList) {
					if (info.name.equals(valueName)) {
						resInfo.value = info.value;
						break;
					}
				}
			} 
			// TODO : improve this if statement
			if (TAG_STRING.equals(which)) { 
				mResources.addStringValue(resInfo.index, resInfo.value);
			} else if (TAG_INTEGER.equals(which)) {
				mResources.addIntegerValue(resInfo.index, Integer.parseInt(resInfo.value));
			} else if (TAG_BOOL.equals(which)) {
				mResources.addBooleanValue(resInfo.index, Boolean.parseBoolean(resInfo.value));
			} else if (TAG_COLOR.equals(which)) {
				mResources.addColorValue(resInfo.index, Color.parseColor(resInfo.value));
			} else if (TAG_DIMEN.equals(which)) {
				mResources.addDimensionValue(resInfo.index, Integer.parseInt(resInfo.value));
			} else if (TAG_DECLARE_STYLEABLE.equals(which)) {
//						mDeclareStyleableRes.add(processDeclareStyleable(element));
			} else if (TAG_STYLE.equals(which)) {
//						mStyleRes.add(processStyle(element));
			}
		}
		//
		write(endClass());
	}

	private void makeStyleableResource(List<StyleResource> resInfoList, String which) throws IOException {
		write(makePublicStaticFinalClass(which));

		for (StyleResource resInfo : resInfoList) {
			write(makePublicStaticIntDoubleArray(resInfo.name));
			for (Pair<String, String> item : resInfo.items.values()) {
				String value = null;
				
				if (item.second.startsWith("@")) {
					String[] temps = item.second.substring(1).split("/");
					String name = null;
					StringBuilder prefix = new StringBuilder();
					
					if (temps[0].contains(":")) { 
						name = temps[0].substring(temps[0].indexOf(":")+1);
						prefix.append(Global.corePackageName).append(".");
					} else {
						name = temps[0];
					}
					
					prefix.append("R.").append(name).append(".").append(temps[1]);
					value = prefix.toString();
				} else if (item.first.contains("_")) {
					String attrName = item.first.split("_")[1];
					value = mEnumRes.get(attrName.hashCode(), item.second.hashCode());
					
					if (value == null) {
						String attrNameWithBar = item.first.substring(item.first.lastIndexOf(".")+1);
						AttrInfo attrInfo = mAttrNameMap.get(attrNameWithBar);
						if (attrInfo != null && FORMAT_DIMENSION.equals(attrInfo.format)) {
							value = DimensionUtils.toPixel(item.second);
						}
					}
				}
				
				if (value == null) {
					value = item.second;
				}
				write(makePublicStaticIntArrayItem(item.first, value));
			}
			write(endArray());
		}
		
		write(endClass());
	}
	
	private void makeAttrResource(List<StyleResource> resInfoList, String which) throws IOException {
		write(makePublicStaticFinalClass(which));
		
		for (StyleResource resInfo : resInfoList) {
			write(makePublicStaticInt(resInfo.name, Integer.parseInt(resInfo.value)));
			for (Pair<String, String> item : resInfo.items.values()) {
				write(makePublicStaticInt(item.first, Integer.parseInt(item.second)));
			}
		}
		
		write(endClass());
	}
	
	/**
	 * 
	 * @param title @type/name
	 */
	private int getReferenceId(String title) {
		int id = -1;
		if (title.startsWith("@")) {
			String[] temp = title.substring(1).split("/");
			String type = temp[0];
			String name = temp[1];
			
			id = mResources.getIdentifier(name, type, mPackageName);
		}
		
		return id;
	}
	
	private String getReferenceValue(String type, int id) {
		String value = null;
		
		if (TAG_STRING.equals(type)) { 
			value = mResources.getString(id);
		} else if (TAG_INTEGER.equals(type)) {
			value = String.valueOf(mResources.getInteger(id));
		} else if (TAG_BOOL.equals(type)) {
			value = String.valueOf(mResources.getBoolean(id));
		} else if (TAG_COLOR.equals(type)) {
			value = String.valueOf(mResources.getColor(id));
		} else if (TAG_DIMEN.equals(type)) {
			value = String.valueOf(mResources.getDimension(id));
		} else if (TAG_DECLARE_STYLEABLE.equals(type)) {
//			mDeclareStyleableRes.add(processDeclareStyleable(element));
		} else if (TAG_STYLE.equals(type)) {
//			mStyleRes.add(processStyle(element));
		}
		
		return value;
	}
	
	public static void recycle() {
		mAttrNameMap = null;
	}
	
	private class ResourceInfo {
		public String name;
		/** startIndex */
		public int index = -1;
		public String value;
		public String type;
	}
	
	private class AttrInfo {
		public String fullName;
		public String attrName;
		public String format;
	}
	
	private class StyleResource extends ResourceInfo {
		public String parentName;
		public HashMap<String, Pair<String, String>> items = new HashMap<String, Pair<String, String>>();
	}
}
