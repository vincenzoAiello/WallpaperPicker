import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.win32.W32APIOptions;
import net.bytebuddy.asm.Advice.This;

public class WallpaperPicker {
	
	public WallpaperPicker(String RicercaImmagine) throws InterruptedException 
	{
		ChromeOptions options = new ChromeOptions();				
		//options.addArguments("headless");							////Per nascondere pagina
		
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();			//Per non mostrare notifica
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
		chromePrefs.put("download.prompt_for_download", false);
		chromePrefs.put("download.default_directory", this.downloadFilepath);
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		
		this.driver= new ChromeDriver(options);								//Imposta Driver e url
		this.RicercaImmagine=RicercaImmagine;
		driver.get("https://unsplash.com/s/photos/" + RicercaImmagine);
		
		Random r = new Random();
		int scrollSize = r.nextInt(10000);
		System.out.println("Scroll Size:" + scrollSize);
		JavascriptExecutor jse = (JavascriptExecutor)driver;           //Per scrollare in basso
		jse.executeScript("scroll(0," +scrollSize + ");");
		
		Thread.sleep(1000);
	}
	
	public void CercaImmagine() throws InterruptedException, IOException
	{
		List<WebElement> immagine = driver.findElements(By.cssSelector("figure[itemprop='image']"));
		Random r = new Random();
		int numeroImmagine =0;
		
		numeroImmagine=r.nextInt(immagine.size()-1);						//seleziona immagine in modo random
		immagine.get(numeroImmagine).click();
		System.out.println("Immagine selezionata: " + numeroImmagine);
		System.out.println("Immagini Trovate" + immagine.size());
		
		Thread.sleep(1000);
		WebElement download = driver.findElement(By.cssSelector("div[class='_13Q- _27vvN _2iWc-'"));		//Scarica immagine
		download.click();
		
		while(!new File(this.downloadFilepath).exists());
		ImpostaSfondo();
		
		
	}
	
	public static interface User32 extends Library {
	     @SuppressWarnings("deprecation")
		User32 INSTANCE = (User32) Native.loadLibrary("user32",User32.class,W32APIOptions.DEFAULT_OPTIONS);        
	     boolean SystemParametersInfo (int one, int two, String s ,int three);         
	 }
	
	private void ImpostaSfondo() throws IOException, InterruptedException
	{
		Thread.sleep(1000);
		Object[] el = ElencoFile();
		System.out.println(el.length);
		for (int i = 0; i < el.length; i++) {
			if(((Path) el[i]).toString().contains(".jpg") || ((Path) el[i]).toString().contains(".png")) {
				System.out.println(((Path) el[i]).toString());
				User32.INSTANCE.SystemParametersInfo(0x0014, 0, ((Path) el[i]).toString() , 1);
			}
		}
		Thread.sleep(2000);
		deleteDir(new File(this.downloadFilepath));
	}
	
	//Elenco file presenti
	private Object[] ElencoFile() throws IOException
	{
		Object[] elencoFiles;
		elencoFiles=Files.list(Paths.get(this.downloadFilepath)).toArray();
		return elencoFiles;
	}
	
	private void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            if (! Files.isSymbolicLink(f.toPath())) {
	                deleteDir(f);
	            }
	        }
	    }
	    file.delete();
	}
	
	private WebDriver driver;
	private String RicercaImmagine;
	private String downloadFilepath = "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads\\sfondo";
}
