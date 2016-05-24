/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.util;

import be.ac.vub.wise.cmtserver.blocks.FactType;
import be.ac.vub.wise.cmtserver.core.CMTDelegator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 *
 * @author Sandra
 */
public class HelperClass {
    
    public static synchronized void compile(String source, String className1, String var){
        System.out.println("7>>>>> compile(" + source + ", " + className1 + ", " + var);
        String className = toUppercaseFirstLetter(className1);
        File root = new File(Constants.JAVAFILEPATH + var);
	
        File sourceFile = new File(root, className+".java");
	sourceFile.getParentFile().mkdirs();
	try {
            new FileWriter(sourceFile).append(source).close();
            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator+ Constants.KIEJARPATH);
            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + Constants.COMMONSPATH);
            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + Constants.CLASSPATH);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            System.out.println("9>>>>>>>>>>> compiler sourceFile path: " + sourceFile.getPath());
            System.out.println("10>>>>>>>>>> current class path: " + System.getProperty("java.class.path"));
            int err = compiler.run(null, null, null, sourceFile.getPath());
            if(err == 0){
		File roots = new File(Constants.JAVAFILEPATH +var);			
                File sFile = new File(roots, className+".class");
		InputStream inputStr = new FileInputStream(sFile);
                File rootoutputClass = new File(Constants.CLASSPATH);
		File targetFileClass = new File(rootoutputClass, var+"/"+className+".class"); 
		targetFileClass.getParentFile().mkdirs();
		OutputStream outputClass = new FileOutputStream(targetFileClass);
		byte[] buf = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStr.read(buf)) > 0) {
                    outputClass.write(buf, 0, bytesRead);
		}
		outputClass.close();
		inputStr.close();			
		//Files.delete(sourceFile.toPath());
		Files.delete(sFile.toPath());
                
                String clname = "";
                String war = "WEB-INF/classes/";
                String inputDic ="/";
                switch(var){
                    case Constants.PACKAGEFACTSSLASH:
                        clname = Constants.PACKAGEFACTS + "."+className;
                        war += Constants.PACKAGEFACTSSLASH ;
                        inputDic += Constants.PACKAGEFACTSSLASH;
                        break;
                    case Constants.PACKAGEEVENTSSLASH:
                        clname = Constants.PACKAGEEVENTS + "."+className;
                        war += Constants.PACKAGEEVENTSSLASH ;
                        inputDic += Constants.PACKAGEEVENTSSLASH;
                        break;
                    case Constants.PACKAGEFUNCTIONSSLASH:
                        clname = Constants.PACKAGEFUNCTIONS + "."+className;
                        war += Constants.PACKAGEFUNCTIONSSLASH ;
                        inputDic += Constants.PACKAGEFUNCTIONSSLASH;
                        break;
                    case Constants.PACKAGEACTIONSSLASH:
                        clname = Constants.PACKAGEACTIONS + "."+className;
                        war += Constants.PACKAGEACTIONSSLASH ;
                        inputDic += Constants.PACKAGEACTIONSSLASH;
                        break;
                }
                // copy cl file to war folder dic then change war
                
                File rootsWAR = new File(Constants.CLASSPATH + inputDic );			
                File sFileWAR = new File(rootsWAR, "/"+className+".class");
                
                System.out.println("5>>>>>> \n" + 
                        "rootsWAR: " + rootsWAR.getAbsolutePath() +
                        "sFileWAR: " + sFileWAR.getAbsolutePath());
                
		InputStream inputStrWAR = new FileInputStream(sFileWAR);
                File rootoutputClassWAR = new File(Constants.CLASSPATHDicFolder + "/" +war);
		File targetFileClassWAR = new File(rootoutputClassWAR, "/"+className+".class"); 
		targetFileClassWAR.getParentFile().mkdirs();
		OutputStream outputClassWAR = new FileOutputStream(targetFileClassWAR);
		byte[] bufWAR = new byte[1024];
		int bytesReadWAR;
		while ((bytesReadWAR = inputStrWAR.read(bufWAR)) > 0) {
                    outputClassWAR.write(bufWAR, 0, bytesReadWAR);
		}
		outputClassWAR.close();
		inputStrWAR.close();			
               
                Runtime.getRuntime().exec("jar -uvf CMTServerRestFull-1.0-SNAPSHOT.war -C CMTServerRestFull-1.0-SNAPSHOT "+ new File(war + "/" + className + ".class"), null, new File(Constants.ROOTFOLDERFORJAR));
               // Thread.sleep(1000);
                
                Class[] parameters = new Class[]{URL.class};
                URL url = targetFileClass.toURI().toURL();
                
                URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class sysclass = URLClassLoader.class;
                Method method = sysclass.getDeclaredMethod("addURL", parameters);
                method.setAccessible(true);
                method.invoke(sysloader, new Object[]{url});
                Thread.currentThread().setContextClassLoader(sysloader);
                
              
                Object ob = Class.forName(clname);
                
            }
            else
                System.out.println("8>>>>>>>>>>>> Compile error");
	} catch (FileNotFoundException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HelperClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String toUppercaseFirstLetter(String st){
        String sub1l = st.substring(0, 1);
        String up1l = sub1l.toUpperCase();
        String ret = up1l + st.substring(1);
        return ret;
    }
    
    public static String getSimpleNameAll(String name){
                    String[] splitLastPoint = name.split("\\.");
                    int z = splitLastPoint.length;
                    String simpleClassName = name;
                    if(z>0){
                        simpleClassName= splitLastPoint[z-1];
                    }
                    return simpleClassName;
  
    }
    
    public static boolean isCMTFactType(String simpleClassName){
        
        FactType type = CMTDelegator.get().getFactTypeWithName(simpleClassName);
        if(type!=null){
            return true;
        }
        return false;
    }
}
