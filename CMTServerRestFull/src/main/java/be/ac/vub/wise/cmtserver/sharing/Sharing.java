/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.vub.wise.cmtserver.sharing;

import be.ac.vub.wise.cmtserver.blocks.Template;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author lars
 */
public interface Sharing {
    
    public JSONObject exportActivity(Template templ);
    public HashMap<String, HashMap<Integer, String>> importTemplateRule(JSONObject json);

    
    
}
