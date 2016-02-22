package be.ac.vub.wise.cmtgui.util;




public class CmtHelper {
	
//	public static void compileActivity(OutputHA output){
//		
//		try {
//			RmiInterface.getRMI().getStub().compileActivity(output);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public static void compileRule(){
//		
//	} 
	
	private static String capitalizeString(String str){
		String nameFinal = ""; 
		
			String[] strs = str.split("\\s");
			for(String st : strs){
				 char[] stringArray = st.trim().toCharArray();
				 System.out.println(stringArray[0]);
				char ch = Character.toUpperCase(stringArray[0]);
				stringArray[0] = ch;
				String fst = new String(stringArray);
			
				 System.out.println(fst);
			
				nameFinal += fst;
			}
			
		
		return nameFinal;
	}

}
