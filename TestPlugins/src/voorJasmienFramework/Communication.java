/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voorJasmienFramework;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sandra
 */
public class Communication {
    
    public static boolean getAUIHasProperty(String uiName, String name, String value){
        try {
        HttpResponse<JsonNode> request = Unirest.get(ConstantsAUI.URLREVITA+"/oc2/extended/ui/getAUIHasProperty/"+uiName+"/"+name+"/"+value).asJson();
        JSONObject urlRes = request.getBody().getObject();    
        boolean result = urlRes.getBoolean("result");
        return result;
        } catch (UnirestException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    // ui/getAUIsWithPropertyWithNameAndValue/{name}/{value}
    public static HashSet<String> getAUIsWithPropertyWithNameAndValue(String name, String value){
        HashSet<String> result = new HashSet<String>();
        try {
            HttpResponse<JsonNode> request = Unirest.get(ConstantsAUI.URLREVITA+"/oc2/extended/ui/getAUIsWithPropertyWithNameAndValue/"+name+"/"+value).asJson();
            JSONObject urlRes = request.getBody().getObject();    
            JSONArray arr = urlRes.getJSONArray("uis");
            for(int i=0; i<arr.length();i++){
                JSONObject uiO = arr.getJSONObject(i);
                result.add(uiO.getString("name"));
            }
        } catch (UnirestException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
}
